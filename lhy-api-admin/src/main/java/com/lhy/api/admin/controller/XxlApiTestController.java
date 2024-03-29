package com.lhy.api.admin.controller;

import com.lhy.api.admin.core.consistant.RequestConfig;
import com.lhy.api.admin.core.model.*;
import com.lhy.api.admin.core.util.JacksonUtil;
import com.lhy.api.admin.core.util.ThrowableUtil;
import com.lhy.api.admin.core.util.tool.StringTool;
import com.lhy.api.admin.dao.IXxlApiDocumentDao;
import com.lhy.api.admin.dao.IXxlApiGlobalParamDao;
import com.lhy.api.admin.dao.IXxlApiProjectDao;
import com.lhy.api.admin.dao.IXxlApiTestHistoryDao;
import com.lhy.api.admin.utils.PlaceholderUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2017-04-04 18:10:54
 */
@Controller
@RequestMapping("/test")
public class XxlApiTestController extends BaseController{
	private static Logger logger = LoggerFactory.getLogger(XxlApiTestController.class);

	@Resource
	private IXxlApiDocumentDao xxlApiDocumentDao;
	@Resource
	private IXxlApiTestHistoryDao xxlApiTestHistoryDao;
	@Resource
	private IXxlApiProjectDao xxlApiProjectDao;
	@Resource
	private IXxlApiGlobalParamDao xxlApiGlobalParamDao;

	/**
	 * 接口测试，待完善
	 * @return
	 */
	@RequestMapping
	public String index(Model model,
						int documentId,
						@RequestParam(required = false, defaultValue = "0") int testId) {


		// params
		XxlApiDocument document = document = xxlApiDocumentDao.load(documentId);
		if (document == null) {
			throw new RuntimeException("接口ID非法");
		}
		XxlApiProject project = xxlApiProjectDao.load(document.getProjectId());

		List<Map<String, String>> requestHeaders = null;
		List<Map<String, String>> queryParams = null;
		List<Map<String, String>> projectGlobalQueryParams = null;
		List<Map<String, String>> globalQueryParams = null;
		if (testId > 0) {
			XxlApiTestHistory testHistory = xxlApiTestHistoryDao.load(testId);
			if (testHistory == null) {
				throw new RuntimeException("测试用例ID非法");
			}
			model.addAttribute("testHistory", testHistory);

			requestHeaders = (StringTool.isNotBlank(testHistory.getRequestHeaders()))? JacksonUtil.readValue(testHistory.getRequestHeaders(), List.class):null;
			queryParams = (StringTool.isNotBlank(testHistory.getQueryParams()))? JacksonUtil.readValue(testHistory.getQueryParams(), List.class):null;
			globalQueryParams = (StringTool.isNotBlank(testHistory.getGlobalQueryParams()))? JacksonUtil.readValue(testHistory.getGlobalQueryParams(), List.class):null;
		} else {
			requestHeaders = (StringTool.isNotBlank(document.getRequestHeaders()))? JacksonUtil.readValue(document.getRequestHeaders(), List.class):null;
			queryParams = (StringTool.isNotBlank(document.getQueryParams()))? JacksonUtil.readValue(document.getQueryParams(), List.class):null;
			globalQueryParams = (StringTool.isNotBlank(document.getGlobalQueryParams()))? JacksonUtil.readValue(document.getGlobalQueryParams(), List.class):null;
		}
        //查询全局参数
		XxlApiGlobalParam xxlApiGlobalParam = new XxlApiGlobalParam();
		List<XxlApiGlobalParam> xxlApiGlobalParamList = xxlApiGlobalParamDao.loadByProjectId(document.getProjectId());
		if(xxlApiGlobalParamList.size()>0){
			xxlApiGlobalParam = xxlApiGlobalParamList.get(0);
		}
		projectGlobalQueryParams = (StringTool.isNotBlank(xxlApiGlobalParam.getGlobalQueryParams()))? JacksonUtil.readValue(xxlApiGlobalParam.getGlobalQueryParams(), List.class):null;
		//从公共全局参数中拿到value
		if(globalQueryParams!=null && globalQueryParams.size()>0 && projectGlobalQueryParams!=null && projectGlobalQueryParams.size()>0){
			for(int a=0;a<globalQueryParams.size();a++){
				String key = globalQueryParams.get(a).get("key");
				String value = "";
				for(Map<String, String> projectGlobalQuery:projectGlobalQueryParams){
					String name = projectGlobalQuery.get("name");
					if(name.equals(key)){
						value = projectGlobalQuery.get("value");
					}
				}
				globalQueryParams.get(a).put("value",value);
			}
		}
		model.addAttribute("projectGlobalQueryParams", projectGlobalQueryParams);

		model.addAttribute("document", document);
		model.addAttribute("project", project);
		model.addAttribute("requestHeaders", requestHeaders);
		model.addAttribute("queryParams", queryParams);
        model.addAttribute("documentId", documentId);
        model.addAttribute("testId", testId);
		model.addAttribute("globalQueryParams", globalQueryParams);
		// enum
		model.addAttribute("RequestMethodEnum", RequestConfig.RequestMethodEnum.values());
		model.addAttribute("requestHeadersEnum", RequestConfig.requestHeadersEnum);
		model.addAttribute("QueryParamTypeEnum", RequestConfig.QueryParamTypeEnum.values());
		model.addAttribute("ResponseContentType", RequestConfig.ResponseContentType.values());

		return "test/test.index";
	}

	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<Integer> add(XxlApiTestHistory xxlApiTestHistory) {
		int ret = xxlApiTestHistoryDao.add(xxlApiTestHistory);
		return ret>0?new ReturnT<Integer>(xxlApiTestHistory.getId()):new ReturnT<Integer>(ReturnT.FAIL_CODE, null);
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlApiTestHistory xxlApiTestHistory) {
		int ret = xxlApiTestHistoryDao.update(xxlApiTestHistory);
		return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	@RequestMapping("/delete")
	@ResponseBody
	public ReturnT<String> delete(int id) {
		int ret = xxlApiTestHistoryDao.delete(id);
		return ret>0?ReturnT.SUCCESS:ReturnT.FAIL;
	}

	/**
	 * 测试Run
	 * @return
	 */
	@RequestMapping("/run")
	@ResponseBody
	public ReturnT<String> run(XxlApiTestHistory xxlApiTestHistory, HttpServletRequest request, HttpServletResponse response) {

		// valid
		RequestConfig.ResponseContentType contentType = RequestConfig.ResponseContentType.match(xxlApiTestHistory.getRespType());
		if (contentType == null) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "响应数据类型(MIME)非法");
		}

		if (StringTool.isBlank(xxlApiTestHistory.getRequestUrl())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请输入接口URL");
		}

		// request headers
		Map<String, String> requestHeaderMap = null;
		List<Map<String, String>> requestHeaders = (StringTool.isNotBlank(xxlApiTestHistory.getRequestHeaders()))? JacksonUtil.readValue(xxlApiTestHistory.getRequestHeaders(), List.class):null;
		if (requestHeaders!=null && requestHeaders.size()>0) {
			requestHeaderMap = new HashMap<String, String>();
			for (Map<String, String> item: requestHeaders) {
				requestHeaderMap.put(item.get("key"), item.get("value"));
			}
		}

		// query param
		Map<String, String> queryParamMap = null;
		List<Map<String, String>> queryParams = (StringTool.isNotBlank(xxlApiTestHistory.getQueryParams()))? JacksonUtil.readValue(xxlApiTestHistory.getQueryParams(), List.class):null;
		if (queryParams!=null && queryParams.size()>0) {
			queryParamMap = new HashMap<String, String>();
			for (Map<String, String> item: queryParams) {
				queryParamMap.put(item.get("key"), item.get("value"));
			}
		}
        //替换url中的占位符
        String requestUrl = PlaceholderUtils.resolvePlaceholders(xxlApiTestHistory.getRequestUrl(), queryParamMap);
        xxlApiTestHistory.setRequestUrl(requestUrl);

		//global query param
		//Map<String, String> globalQueryParamMap = null;
		List<Map<String, String>> globalQueryParams = (StringTool.isNotBlank(xxlApiTestHistory.getGlobalQueryParams()))? JacksonUtil.readValue(xxlApiTestHistory.getGlobalQueryParams(), List.class):null;
		logger.info("==========globalQueryParams",globalQueryParams);
		if (globalQueryParams!=null && globalQueryParams.size()>0) {
			//globalQueryParamMap = new HashMap<String, String>();
			if(queryParamMap==null){
				queryParamMap = new HashMap<String, String>();
			}
			for (Map<String, String> item: globalQueryParams) {
				logger.info("==========key:{},value:{}",item.get("key"),item.get("value"));
				queryParamMap.put(item.get("key"), item.get("value"));
			}
		}

		// invoke 1/3
		HttpRequestBase remoteRequest = null;
		if (RequestConfig.RequestMethodEnum.POST.name().equals(xxlApiTestHistory.getRequestMethod())) {
			HttpPost httpPost = new HttpPost(xxlApiTestHistory.getRequestUrl());
			// query params
			if (queryParamMap != null && !queryParamMap.isEmpty()) {
				List<NameValuePair> formParams = new ArrayList<NameValuePair>();
				for(Map.Entry<String,String> entry : queryParamMap.entrySet()){
					formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					logger.error(e.getMessage(), e);
				}
			}
			remoteRequest = httpPost;
		} else if (RequestConfig.RequestMethodEnum.GET.name().equals(xxlApiTestHistory.getRequestMethod())) {
			remoteRequest = new HttpGet(markGetUrl(xxlApiTestHistory.getRequestUrl(), queryParamMap));
		} else if (RequestConfig.RequestMethodEnum.PUT.name().equals(xxlApiTestHistory.getRequestMethod())) {
			remoteRequest = new HttpPut(markGetUrl(xxlApiTestHistory.getRequestUrl(), queryParamMap));
		} else if (RequestConfig.RequestMethodEnum.DELETE.name().equals(xxlApiTestHistory.getRequestMethod())) {
			remoteRequest = new HttpDelete(markGetUrl(xxlApiTestHistory.getRequestUrl(), queryParamMap));
		} else if (RequestConfig.RequestMethodEnum.HEAD.name().equals(xxlApiTestHistory.getRequestMethod())) {
			remoteRequest = new HttpHead(markGetUrl(xxlApiTestHistory.getRequestUrl(), queryParamMap));
		} else if (RequestConfig.RequestMethodEnum.OPTIONS.name().equals(xxlApiTestHistory.getRequestMethod())) {
			remoteRequest = new HttpOptions(markGetUrl(xxlApiTestHistory.getRequestUrl(), queryParamMap));
		} else if (RequestConfig.RequestMethodEnum.PATCH.name().equals(xxlApiTestHistory.getRequestMethod())) {
			remoteRequest = new HttpPatch(markGetUrl(xxlApiTestHistory.getRequestUrl(), queryParamMap));
		} else {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "请求方法非法");
		}

		// invoke 2/3
		if (requestHeaderMap!=null && !requestHeaderMap.isEmpty()) {
			for(Map.Entry<String,String> entry : requestHeaderMap.entrySet()){
				remoteRequest.setHeader(entry.getKey(), entry.getValue());
			}
		}

		// write response
		String responseContent = remoteCall(remoteRequest);
		return new ReturnT<String>(responseContent);
	}

	private String markGetUrl(String url, Map<String, String> queryParamMap){
		String finalUrl = url;
		if (queryParamMap!=null && queryParamMap.size()>0) {
			finalUrl = url + "?";
			for(Map.Entry<String,String> entry : queryParamMap.entrySet()){
				finalUrl += entry.getKey() + "=" + entry.getValue() + "&";
			}
			finalUrl = finalUrl.substring(0, finalUrl.length()-1);	// 后缀处理，去除 ？ 或 & 符号
		}
		return finalUrl;
	}

	private String remoteCall(HttpRequestBase remoteRequest){
		// remote test
		String responseContent = null;

		CloseableHttpClient httpClient = null;
		try{
			org.apache.http.client.config.RequestConfig requestConfig = org.apache.http.client.config.RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
			remoteRequest.setConfig(requestConfig);

			httpClient = HttpClients.custom().disableAutomaticRetries().build();

			// parse response
			HttpResponse response = httpClient.execute(remoteRequest);
			HttpEntity entity = response.getEntity();
			if (null != entity) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					responseContent = EntityUtils.toString(entity, "UTF-8");
				} else {
					responseContent = "请求状态异常：" + response.getStatusLine().getStatusCode();
					if (statusCode==302 && response.getFirstHeader("Location")!=null) {
						responseContent += "；Redirect地址：" + response.getFirstHeader("Location").getValue();
					}

				}
				EntityUtils.consume(entity);
			}
			logger.info("http statusCode error, statusCode:" + response.getStatusLine().getStatusCode());
		} catch (Exception e) {
			responseContent = "请求异常：" + ThrowableUtil.toString(e);
		} finally{
			if (remoteRequest!=null) {
				remoteRequest.releaseConnection();
			}
			if (httpClient!=null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		return responseContent;
	}

}
