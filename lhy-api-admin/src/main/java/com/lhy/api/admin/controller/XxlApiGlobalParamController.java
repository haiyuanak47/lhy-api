package com.lhy.api.admin.controller;

import com.lhy.api.admin.core.consistant.RequestConfig;
import com.lhy.api.admin.core.model.ReturnT;
import com.lhy.api.admin.core.model.XxlApiGlobalParam;
import com.lhy.api.admin.core.model.XxlApiProject;
import com.lhy.api.admin.core.model.XxlApiUser;
import com.lhy.api.admin.core.util.JacksonUtil;
import com.lhy.api.admin.core.util.tool.ArrayTool;
import com.lhy.api.admin.core.util.tool.StringTool;
import com.lhy.api.admin.dao.IXxlApiGlobalParamDao;
import com.lhy.api.admin.dao.IXxlApiProjectDao;
import com.lhy.api.admin.service.impl.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 */
@Controller
@RequestMapping("/globalparam")
public class XxlApiGlobalParamController extends BaseController{

	private static Logger logger = LoggerFactory.getLogger(XxlApiGlobalParamController.class);

	@Resource
	private IXxlApiProjectDao xxlApiProjectDao;
	@Resource
	private IXxlApiGlobalParamDao xxlApiGlobalParamDao;


	private boolean hasBizPermission(HttpServletRequest request, int bizId){
		XxlApiUser loginUser = (XxlApiUser) request.getAttribute(LoginService.LOGIN_IDENTITY);
		if ( loginUser.getType()==1 ||
				ArrayTool.contains(StringTool.split(loginUser.getPermissionBiz(), ","), String.valueOf(bizId))
				) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 更新页面
	 * @return
	 */
	@RequestMapping("/updatePage")
	public String updatePage(HttpServletRequest request, Model model, int projectId) {

		//
		XxlApiGlobalParam xxlApiGlobalParam = new XxlApiGlobalParam();
		List<XxlApiGlobalParam> xxlApiGlobalParamList = xxlApiGlobalParamDao.loadByProjectId(projectId);
		if(xxlApiGlobalParamList.size()>0){
			xxlApiGlobalParam = xxlApiGlobalParamList.get(0);
		}
		if (xxlApiGlobalParam == null) {
			throw new RuntimeException("操作失败，全局参数ID非法");
		}
		model.addAttribute("globalparam", xxlApiGlobalParam);
		model.addAttribute("globalQueryParamsList", (StringTool.isNotBlank(xxlApiGlobalParam.getGlobalQueryParams()))? JacksonUtil.readValue(xxlApiGlobalParam.getGlobalQueryParams(), List.class):null );

		// project
		//int projectId = xxlApiGlobalParam.getProjectId();
		model.addAttribute("projectId", projectId);


		// 权限
		XxlApiProject project = xxlApiProjectDao.load(projectId);
		if (!hasBizPermission(request, project.getBizId())) {
			throw new RuntimeException("您没有相关业务线的权限,请联系管理员开通");
		}

		// enum
		model.addAttribute("queryParamTypeEnum", RequestConfig.QueryParamTypeEnum.values());

		return "globalparam/globalparam.update";
	}

	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(HttpServletRequest request, XxlApiGlobalParam xxlApiGlobalParam) {
		XxlApiGlobalParam oldVo = null;
		//if(xxlApiGlobalParam.getId()>0){
			//oldVo = xxlApiGlobalParamDao.load(xxlApiGlobalParam.getId());
            List<XxlApiGlobalParam> xxlApiGlobalParamList = xxlApiGlobalParamDao.loadByProjectId(xxlApiGlobalParam.getProjectId());
            if(xxlApiGlobalParamList.size()>0){
                oldVo = xxlApiGlobalParamList.get(0);
                xxlApiGlobalParam.setId(oldVo.getId());
                xxlApiGlobalParam.setAddTime(oldVo.getAddTime());
            }
			/*if (oldVo == null) {
				return new ReturnT<String>(ReturnT.FAIL_CODE, "操作失败，接口ID非法");
			}*/

		//}else{
		//	oldVo = xxlApiGlobalParam;
		//}

		// 权限
		XxlApiProject project = xxlApiProjectDao.load(xxlApiGlobalParam.getProjectId());
		if (!hasBizPermission(request, project.getBizId())) {
			return new ReturnT<String>(ReturnT.FAIL_CODE, "您没有相关业务线的权限,请联系管理员开通");
		}

		// fill not-change val
		//xxlApiGlobalParam.setProjectId(oldVo.getProjectId());
        //xxlApiGlobalParam.setUpdateTime(new Date());
        int ret = -1;
        if(xxlApiGlobalParam.getId()==0){
            //xxlApiGlobalParam.setAddTime(new Date());
            ret = xxlApiGlobalParamDao.add(xxlApiGlobalParam);
        }else{
            ret = xxlApiGlobalParamDao.update(xxlApiGlobalParam);
        }


		return (ret>0)?ReturnT.SUCCESS:ReturnT.FAIL;
	}


}
