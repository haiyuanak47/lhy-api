<!DOCTYPE html>
<html>
<head>
    <title>API管理平台</title>
    <link rel="shortcut icon" href="${request.contextPath}/static/favicon.ico" type="image/x-icon" />
    <#import "../common/common.macro.ftl" as netCommon>
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/select2/select2.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/adminlte/plugins/iCheck/square/_all.css">
    <@netCommon.commonStyle />
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/editor.md-1.5.0/main/editormd.min.css">
    <link rel="stylesheet" href="${request.contextPath}/static/plugins/jsontree/jquery.jsonview.css">

</head>
<body class="hold-transition skin-blue sidebar-mini <#if cookieMap?exists && cookieMap["adminlte_settings"]?exists && "off" == cookieMap["adminlte_settings"].value >sidebar-collapse</#if>">
<div class="wrapper">
    <!-- header -->
    <@netCommon.commonHeader />
    <!-- left -->
    <@netCommon.commonLeft "projectList" />

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>MarkDown</h1>
        </section>

        <section class="content">
            <form class="form-horizontal" id="ducomentForm" >
                <input type="hidden" name="id" value="${document.id}" >
                <input type="hidden" name="projectId" value="${document.projectId}" >
                <#-- markdown -->
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title"></h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-default btn-xs" type="button" onclick="javascript:window.location.href='${request.contextPath}/group?projectId=${projectId}'" >返回接口列表</button>
                            <button class="btn btn-default btn-xs" type="button" onclick="javascript:window.location.href='${request.contextPath}/document/detailPage?id=${document.id}'" >接口详情页</button>
                        </div>
                    </div>
                    <div class="box-body" >
                        <div class="box-body pad" id="remark" >
                            <textarea style="display:none;">


**简要描述：**

- ${document.name}

**请求URL：**
- ` ${document.requestUrl} `

**请求方式：**
- <#list RequestMethodEnum as item> <#if item == document.requestMethod>${item}</#if></#list>
<#if requestHeadersList?exists && requestHeadersList?size gt 0 >

**请求Header：**

|Header|Value|
|:---- |-----  |
<#list requestHeadersList as requestHeadersMap>
<#assign key = requestHeadersMap['key'] />
<#assign value = requestHeadersMap['value'] />
|${key}|${value}|
</#list>
</#if>

**参数：**

|参数名|必选|类型|说明|
|:----    |:---|:----- |-----   |
<#list queryParamList as queryParam>
|${queryParam.name} |<#if queryParam.notNull == "true" >是<#else>否</#if>  |${queryParam.type} |${queryParam.desc}  |
</#list>
<#if globalQueryParams?exists>
<#list globalQueryParams as queryParam>
    <#assign key = queryParam['key'] />
    <#assign value = queryParam['value'] />
    <#if projectGlobalQueryParams?exists>
        <#list projectGlobalQueryParams as item>
            <#if item.name == key>
|${queryParam.key} |<#if item.notNull == "true" >是<#else>否</#if> |${item.type}  |${item.desc}  |
            </#if>
        </#list>
    </#if>
</#list>
</#if>

**返回示例**

```
${document.successRespExample}
```

**返回参数说明**

|参数名|类型|说明|
|:-----  |:-----|-----|
|code |int   |状态码，0成功，其他失败  |

**备注**

${document.remark}
                            </textarea></div>
                    </div>
                </div>

            </form>

        </section>

    </div>

    <!-- footer -->
    <@netCommon.commonFooter />
</div>

<@netCommon.commonScript />

<script src="${request.contextPath}/static/adminlte/plugins/select2/select2.min.js"></script>
<script src="${request.contextPath}/static/adminlte/plugins/iCheck/icheck.min.js"></script>
<script src="${request.contextPath}/static/plugins/editor.md-1.5.0/main/editormd.min.js"></script>
<script src="${request.contextPath}/static/plugins/jsontree/jquery.jsonview.js"></script>
<script src="${request.contextPath}/static/js/document.update.1.js"></script>
</body>
</html>
