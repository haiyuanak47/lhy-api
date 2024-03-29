$(function() {

	// base init
	$(".select2").select2();
	$(".select2_tag").select2({tags: true});

	$('.iCheck').iCheck({
		labelHover : false,
		cursor : true,
		checkboxClass : 'icheckbox_square-blue',
		radioClass : 'iradio_square-blue',
		increaseArea : '20%'
	});

	/**
	 * 请求头部，新增一行
	 */
	$('#requestHeaders_add').click(function () {
		var html = $('#requestHeaders_example').html();
		$('#requestHeaders_parent').append(html);

		$("#requestHeaders_parent .select2_tag_new").each(function () {
			var $select2 = $(this);
			$($select2).removeClass('select2_tag_new');
			$($select2).addClass('select2_tag');
			$($select2).select2({tags: true});
		});
	});
	/**
	 * 请求头部，删除一行
	 */
	$('#requestHeaders_parent').on('click', '.delete',function () {
		$(this).parents('.requestHeaders_item').remove();
	});

	/**
	 * 请求参数，新增一行
	 */
	$('#queryParams_add').click(function () {
		var html = $('#queryParams_example').html();
		$('#queryParams_parent').append(html);

		$("#queryParams_parent .select2_tag_new").each(function () {
			var $select2 = $(this);
			$($select2).removeClass('select2_tag_new');
			$($select2).addClass('select2_tag');
			$($select2).select2();
		});
	});
	/**
	 * 请求参数，删除一行
	 */
	$('#queryParams_parent').on('click', '.delete',function () {
		$(this).parents('.queryParams_item').remove();
	});

	/**
	 * 全局请求参数，新增一行
	 */
	$('#globalQueryParams_add').click(function () {
		var html = $('#globalQueryParams_example').html();
		$('#globalQueryParams_parent').append(html);

		$('#globalQueryParams_parent .select2_tag_new').last().change(function () {
			var globalQueryParams_item = $(this).parent().parent();
			var input = globalQueryParams_item.find('input');
			input.val($(this).val());
		});

		$("#globalQueryParams_parent .select2_tag_new").each(function () {
			var $select2 = $(this);
			$($select2).removeClass('select2_tag_new');
			$($select2).addClass('select2_tag');
			$($select2).select2();
		});

	});
	/**
	 * 全局请求参数，删除一行
	 */
	$('#globalQueryParams_parent').on('click', '.delete',function () {
		$(this).parents('.globalQueryParams_item').remove();
	});

	/**
	 * projectBaseUrlUpdate
	 */
	$('#projectBaseUrlUpdate').change(function () {
		$('#requestUrl').val($(this).val());
	});
	$('#projectBaseUrlUpdate').change();

	//选择全局参数select
	$('#globalQueryParams_parent .select2_tag').change(function () {
		var globalQueryParams_item = $(this).parent().parent();
		var input = globalQueryParams_item.find('input');
		input.val($(this).val());
		//alert($(this).val());
	});
	$('#globalQueryParams_parent .select2_tag').change();
	/**
	 * 运行
	 */
	$('#run').click(function () {

		// param
		var requestMethod = $('#requestMethod').val();
		var requestUrl = $('#requestUrl').val();
		var respType = $('#respType_parent input[name=respType]:checked').val();

		if (!requestUrl) {
            layer.open({
                icon: '2',
                content: '请输入"接口URL"'
            });
			return;
		}

		// request headers
		var requestHeaderList = new Array();
		if ($('#requestHeaders_parent').find('.requestHeaders_item').length > 0) {
			$('#requestHeaders_parent').find('.requestHeaders_item').each(function () {
				var key = $(this).find('.key').val();
				var value = $(this).find('.value').val();
				/*reg = new RegExp('"',"g");
				value = value!=null?value.replace(reg,'&quot;'):value;*/
				if (key) {
					requestHeaderList.push({
						'key':key,
						'value':value
					});
				} else {
					if (value) {
                        layer.open({
                            icon: '2',
                            content: '请检查"请求头部"数据是否填写完整'
                        });
						return;
					}
				}
			});
		}
		var requestHeaders = JSON.stringify(requestHeaderList);

		// query params
		var queryParamList = new Array();
		if ($('#queryParams_parent').find('.queryParams_item').length > 0) {
			$('#queryParams_parent').find('.queryParams_item').each(function () {
				var key = $(this).find('.key').val();
				var value = $(this).find('.value').val();
				/*reg = new RegExp('"',"g");
				value = value!=null?value.replace(reg,'&quot;'):value;*/
				if (key) {
					queryParamList.push({
						'key':key,
						'value':value
					});
				} else {
					if (desc) {
                        layer.open({
                            icon: '2',
                            content: '请检查"请求参数"数据是否填写完整'
                        });
						return;
					}
				}
			});
		}
		var queryParams = JSON.stringify(queryParamList);

		// global query params
		var globalQueryParamList = new Array();
		if ($('#globalQueryParams_parent').find('.globalQueryParams_item').length > 0) {
			$('#globalQueryParams_parent').find('.globalQueryParams_item').each(function () {
				//var key = $(this).find('.key').val();
				var key = $(this).find('.key').find("option:selected").data('key');
				var value = $(this).find('.value').val();
				/*reg = new RegExp('"',"g");
				value = value!=null?value.replace(reg,'&quot;'):value;*/
				if (key) {
					globalQueryParamList.push({
						'key':key,
						'value':value
					});
				} else {
					if (key) {
						layer.open({
							icon: '2',
							content: '请检查"请求参数"数据是否填写完整'
						});
						return;
					}
				}
			});
		}
		var globalQueryParams = JSON.stringify(globalQueryParamList);

		// final params
		var params = {
			'requestMethod':requestMethod,
			'requestUrl':requestUrl,
			'requestHeaders':requestHeaders,
			'queryParams':queryParams,
			'globalQueryParams':globalQueryParams,
			'respType':respType
		}
		//清空响应结果
		$('#respContent').text("请求中...");

		$.post(base_url + "/test/run", params, function(data, status) {
			var $respContent = $('#respContent');
			if (data.code == "200") {
				$($respContent).text(data.content);

				if ('JSON'==respType || 'JSONP'==respType) {
					var json = eval('('+ data.content +')');
					$('#respContent').JSONView(json, { collapsed: false, nl2br: true, recursive_collapser: true });
				}
			} else {
                layer.open({
                    icon: '2',
                    content: (data.msg||'请求失败')
                });
			}
		});

	});


	/**
	 * 保存
	 */
	$('#save').click(function () {
		// param
		var requestMethod = $('#requestMethod').val();
		var requestUrl = $('#requestUrl').val();
		var respType = $('#respType_parent input[name=respType]:checked').val();

		if (!requestUrl) {
            layer.open({
                icon: '2',
                content: '请输入"接口URL"'
            });
			return;
		}

		// request headers
		var requestHeaderList = new Array();
		if ($('#requestHeaders_parent').find('.requestHeaders_item').length > 0) {
			$('#requestHeaders_parent').find('.requestHeaders_item').each(function () {
				var key = $(this).find('.key').val();
				var value = $(this).find('.value').val();
				reg = new RegExp('"',"g");
				value = value!=null?value.replace(reg,'&quot;'):value;
				if (key) {
					requestHeaderList.push({
						'key':key,
						'value':value
					});
				} else {
					if (value) {
                        layer.open({
                            icon: '2',
                            content: '请检查"请求头部"数据是否填写完整'
                        });
						return;
					}
				}
			});
		}
		var requestHeaders = JSON.stringify(requestHeaderList);
		var remark = $('#remark').val();
		// query params
		var queryParamList = new Array();
		if ($('#queryParams_parent').find('.queryParams_item').length > 0) {
			$('#queryParams_parent').find('.queryParams_item').each(function () {
				var key = $(this).find('.key').val();
				var value = $(this).find('.value').val();
				reg = new RegExp('"',"g");
				value = value!=null?value.replace(reg,'&quot;'):value;
				if (key) {
					queryParamList.push({
						'key':key,
						'value':value
					});
				} else {
					if (desc) {
                        layer.open({
                            icon: '2',
                            content: '请检查"请求参数"数据是否填写完整'
                        });
						return;
					}
				}
			});
		}
		var queryParams = JSON.stringify(queryParamList);

		// id
		var documentId = $(this).attr('documentId');
		var testId = $(this).attr('testId');

		// global query params
		var globalQueryParamList = new Array();
		if ($('#globalQueryParams_parent').find('.globalQueryParams_item').length > 0) {
			$('#globalQueryParams_parent').find('.globalQueryParams_item').each(function () {
				//var key = $(this).find('.key').val();
				var key = $(this).find('.key').find("option:selected").data('key');
				var value = $(this).find('.value').val();
				reg = new RegExp('"',"g");
				value = value!=null?value.replace(reg,'&quot;'):value;
				if (key) {
					globalQueryParamList.push({
						'key':key,
						'value':value
					});
				} else {
					if (key) {
						layer.open({
							icon: '2',
							content: '请检查"请求参数"数据是否填写完整'
						});
						return;
					}
				}
			});
		}
		var globalQueryParams = JSON.stringify(globalQueryParamList);

		// final params
		var params = {
			'requestMethod':requestMethod,
			'requestUrl':requestUrl,
			'requestHeaders':requestHeaders,
			'queryParams':queryParams,
			'globalQueryParams':globalQueryParams,
			'respType':respType,
			'documentId':documentId,
			'remark':remark,
			'id':testId
		}

		// url
		var url = base_url + "/test/add";
		if (testId > 0) {
			url = base_url + "/test/update";
		}

		$.post(url, params, function(data, status) {
			if (data.code == "200") {
				if (testId == 0 && data.content>0) {
					$('#save').attr('testId', data.content);
				}
                layer.open({
                    icon: '1',
                    content: '保存成功'
                });
			} else {
                layer.open({
                    icon: '2',
                    content: '保存失败'
                });
			}
		});
	});




});
