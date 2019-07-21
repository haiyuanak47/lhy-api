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
			<h1>全局参数</h1>
		</section>

        <section class="content">
            <form class="form-horizontal" id="ducomentForm" >
                <input type="hidden" name="projectId" value="${projectId}" >

                <#--参数-->
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">参数信息</h3>
                        <div class="box-tools pull-right">
                            <button class="btn btn-default btn-xs" type="button" onclick="javascript:window.location.href='${request.contextPath}/group?projectId=${projectId}'" >返回接口列表</button>
                            <button class="btn btn-info btn-xs" type="submit" >保存参数</button>
                            <button type="button" class="btn btn-box-tool" id="queryParams_add" ><i class="fa fa-plus"></i></button>
                        </div>
                    </div>
                    <div id="queryParams_example" style="display: none;" >
                        <div class="form-group queryParams_item" >
                            <#--<label class="col-sm-1 control-label">参数</label>-->
                            <div class="col-sm-2 item">
                                <input type="text" class="form-control name">
                            </div>
                           <#-- <label class="col-xs-1 control-label">值</label>-->
                            <div class="col-sm-2 item">
                                <input type="text" class="form-control value">
                            </div>
                            <#--<label class="col-sm-1 control-label">说明</label>-->
                            <div class="col-sm-3 item">
                                <input type="text" class="form-control desc">
                            </div>
                            <#--<label class="col-sm-1 control-label">类型</label>-->
                            <div class="col-sm-2 item">
                                <select class="form-control select2_tag_new type" style="width: 100%;">
                                    <#list queryParamTypeEnum as item>
                                        <option value="${item}">${item}</option>
                                    </#list>
                                </select>
                            </div>
                            <div class="col-sm-2 item">
                                <select class="form-control select2_tag_new notNull" style="width: 100%;">
                                    <option value="true">必填</option>
                                    <option value="false">非必填</option>
                                </select>
                            </div>

                            <button type="button" class="col-sm-1 btn btn-box-tool delete" ><i class="fa fa-fw fa-close"></i></button>
                        </div>
                    </div>

                    <div class="box-body" id="queryParams_parent" >
                        <div class="form-group queryParams_item" >
                            <label class="col-sm-2 ">参数</label>
                            <label class="col-sm-2">值</label>
                            <label class="col-sm-3 ">说明</label>
                            <label class="col-sm-2 ">类型</label>
                            <label class="col-sm-2 ">是否必填</label>
                        </div>
                        <#if globalQueryParamsList?exists>
                            <#list globalQueryParamsList as queryParam>
                                <div class="form-group queryParams_item" >
                                    <#--<label class="col-sm-1 control-label">参数</label>-->
                                    <div class="col-sm-2 item">
                                        <input type="text" class="form-control name" value="${queryParam.name}" readonly>
                                    </div>
                                   <#-- <label class="col-xs-1 control-label">值</label>-->
                                    <div class="col-sm-2 item">
                                        <input type="text" class="form-control value" value="${queryParam.value?replace('"', '&quot;')}" >
                                    </div>
                                   <#-- <label class="col-sm-1 control-label">说明</label>-->
                                    <div class="col-sm-3 item">
                                        <input type="text" class="form-control desc" value="${queryParam.desc}" >
                                    </div>
                                    <#--<label class="col-sm-1 control-label">类型</label>-->
                                    <div class="col-sm-2 item">
                                        <select class="form-control select2_tag type" style="width: 100%;">
                                            <#list queryParamTypeEnum as item>
                                                <option value="${item}" <#if queryParam.type == item>selected</#if> >${item}</option>
                                            </#list>
                                        </select>
                                    </div>
                                    <div class="col-sm-2 item">
                                        <select class="form-control select2_tag notNull" style="width: 100%;">
                                            <#if queryParam.notNull?exists>
                                            <option value="true" <#if queryParam.notNull == "true" >selected</#if> >必填</option>
                                            <option value="false" <#if queryParam.notNull == "false" >selected</#if> >非必填</option>
                                            <#else>
                                                <option value="true">必填</option>
                                                <option value="false">非必填</option>
                                            </#if>
                                        </select>
                                    </div>

                                    <button type="button" class="col-sm-1 btn btn-box-tool delete" ><i class="fa fa-fw fa-close"></i></button>
                                </div>
                            </#list>
                        </#if>
                    </div>
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
<script src="${request.contextPath}/static/js/globalparam.update.1.js"></script>
</body>
</html>
