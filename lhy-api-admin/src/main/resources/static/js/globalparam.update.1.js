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
	 * 保存接口
	 */
	var addModalValidate = $("#ducomentForm").validate({
		errorElement : 'span',
		errorClass : 'help-block',
		focusInvalid : true,
		rules : {
			requestUrl : {
				required : true,
				maxlength: 50
			},
			name : {
				required : true,
				minlength: 3,
				maxlength: 50
			}
		},
		messages : {
			requestUrl : {
				required :"请输入“接口URL”",
				maxlength: "长度不可多余100"
			},
			name : {
				required :"请输入“接口名称”",
				minlength: "长度不可少于3",
				maxlength: "长度不可多余50"
			}
		},
		highlight : function(element) {
			$(element).closest('.form-group').addClass('has-error');
		},
		success : function(label) {
			label.closest('.form-group').removeClass('has-error');
			label.remove();
		},
		errorPlacement : function(error, element) {
			element.parent('div').append(error);
		},
		submitHandler : function(form) {

			// query params
			var queryParamList = new Array();
			if ($('#queryParams_parent').find('.queryParams_item').length > 0) {
				$('#queryParams_parent').find('.queryParams_item').each(function () {
					var notNull = $(this).find('.notNull').val();
					var type = $(this).find('.type').val();
					var name = $(this).find('.name').val();
					var value = $(this).find('.value').val();
					var desc = $(this).find('.desc').val();
					reg = new RegExp('"',"g");
					value = value!=null?value.replace(reg,'&quot;'):value;
					if (name) {
						queryParamList.push({
							'notNull':notNull,
							'type':type,
							'name':name,
							'value':value,
							'desc':desc
						});
					} else {
						if (desc) {
                            layer.open({
                                icon: '2',
                                content: '新增接口失败，请检查"参数"数据是否填写完整'
                            });
							return;
						}
					}
				});
			}
			var queryParams = JSON.stringify(queryParamList);

			// final params
			var params = $("#ducomentForm").serialize();
			params += '&' + $.param({
					'globalQueryParams':queryParams
			});

			$.post(base_url + "/globalparam/update", params, function(data, status) {
				if (data.code == "200") {
					$('#addModal').modal('hide');
					layer.open({
						icon: '1',
						content: "保存成功" ,
						end: function(layero, index){
							//var id = $('#ducomentForm input[name=id]').val();
							var projectId = $('#ducomentForm input[name=projectId]').val();
							window.location.href  = base_url + '/globalparam/updatePage?projectId=' + projectId;
						}
					});
				} else {
                    layer.open({
                        icon: '2',
                        content: (data.msg||'保存失败')
                    });
				}
			});
		}
	});

    // JSON 格式化并校验
    $('#successRespExample_2json').click(function () {
        try {
            var jsonStr = $('#successRespExample').val();
            var json = $.parseJSON(jsonStr);
            //$('#successRespExample').JSONView(json, { collapsed: false, nl2br: true, recursive_collapser: true });

            var prettyJson = JSON.stringify(json, undefined, 4);
            $('#successRespExample').val(prettyJson);
        } catch (e) {
            layer.open({
                icon: '2',
                content: "JSON格式化失败:" + e
            });
        }
    });
    $('#failRespExample_2json').click(function () {
        try {
            var jsonStr = $('#failRespExample').val();
            var json = $.parseJSON(jsonStr);

            var prettyJson = JSON.stringify(json, undefined, 4);
            $('#failRespExample').val(prettyJson);
        } catch (e) {
            layer.open({
                icon: '2',
                content: "JSON格式化失败:" + e
            });
        }
    });

});
