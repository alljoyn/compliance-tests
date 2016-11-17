/*******************************************************************************
 * Copyright (c) 2016 Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright 2016 Open Connectivity Foundation and Contributors to
 *      AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
var common = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var $title = jQuery('#title');
	//-------------------------------------------------
	// COMMON BUTTONS
	//-------------------------------------------------	
	var _$apiKeyButton = jQuery('#api-key-button');

	var _$viewTcclButton = jQuery('#view-tccls');
	var _$viewTcclModal = jQuery('#viewTcclModal');
	var _$viewTcclTable = jQuery('#viewTcclTable');
	
	//var _$viewUserManualButton = jQuery('#view-user-manual');
	//-------------------------------------------------
	// NAVIGATION BAR
	//-------------------------------------------------
	var _$navigationList = jQuery('.asa-left-nav');
	var _$navigationElements = jQuery('.asa-left-nav li');
	//-------------------------------------------------
	// BREADCRUMBS
	//-------------------------------------------------	
	var _$projectBreadcrumb = jQuery('#project-breadcrumb');
	var _$dutBreadcrumb = jQuery('#dut-breadcrumb');
	var _$guBreadcrumb = jQuery('#gu-breadcrumb');
	//-------------------------------------------------
	// DYNAMIC SECTION
	//-------------------------------------------------	
	var $dynamicSection = jQuery('#dynamic');
	//-------------------------------------------------
	// NAVIGATION BUTTONS
	//-------------------------------------------------	
	var $nextStepButton = jQuery('#nextButton');
	var $previousStepButton = jQuery('#prevButton');
	var $saveProjectButton = jQuery('#endButton');
	//-------------------------------------------------
	// TOOLTIPS
	//-------------------------------------------------	
	var _$nextStepButtonTooltip = jQuery('#nextButtonTooltip');
	//-------------------------------------------------
	// POST REQUEST TOKEN AND HEADER
	//-------------------------------------------------
	var token = jQuery("meta[name='_csrf']").attr("content");
	var header = jQuery("meta[name='_csrf_header']").attr("content");
	//-------------------------------------------------
	// LOGOUT
	//-------------------------------------------------
	var _$logoutForm = jQuery("#logoutForm");
	
	var _lastWindowSize = $(window).height();
	
	var init = function()
	{
		$dynamicSection.css({height: $(window).height() - 220});
		
		$(window).resize(function()
		{
			var windowHeight = $(window).height();
			var tableHeight = $('div.dataTables_scrollBody').height();
			$dynamicSection.css({height: windowHeight - 220});
			
			adjustDataTablesHeader();
			
			$('div.dataTables_scrollBody').height(tableHeight - _lastWindowSize + windowHeight );
			_lastWindowSize = windowHeight;
		})
		
		_initTooltips();
		
		// init click functions
		_onClickFunctions();
		
		// load project view
		$.ajax({
            type: "GET",
            url: 'project',
            success: function(response)
            {
            	$dynamicSection.fadeOut('fast', function()
            	{
            		$dynamicSection.html(response);
            	});
            	
            	$dynamicSection.fadeIn('fast');
            }
        });
	}
	
	var _initTooltips = function()
	{
		_$nextStepButtonTooltip.tooltip({'placement': 'right'});
		/*_$previousStepButtonTooltip.tooltip({'placement': 'left'});
		_$saveProjectButtonTooltip.tooltip({'placement': 'bottom'});*/
	}
	
	var _onClickFunctions = function()
	{
		// generate a new CTT Local Agent key
		_$apiKeyButton.on('click', function()
		{
			$.ajax({
				type : 'POST',
				url : 'project/generateKey',
				beforeSend: function(xhr)
				{
		            xhr.setRequestHeader(header, token);
		        },
				success : function (newCttPass)
				{
					var message = "Your CTT Local Agent password is:";
					message += "<br>" + newCttPass + "<br>";
					message += "Please copy it because it cannot be retrieved";

					// open the modal to show the password
					yesNoModal.show(message, {title: "CTT Local Agent password", yesButton: false, noLabel: 'Close', noOnClick: function() {
						$('.modal-body').find('h4').empty();
					}});
				}
			});	
		});
		
		/*_$viewUserManualButton.on('click', function(e)
				{
					e.preventDefault();

					window.open('common/user-manual');
				});*/
		
		_$viewTcclButton.on('click', function()
		{
			$.ajax({
				type: 'get',
				url: 'common/tccl',
				success: function(tcclList)
				{
					var tcclToAppend = '<select id="tccl-selector" class="form-control">'
					$.each(tcclList, function(i, tccl)
					{
						tcclToAppend += "<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>";
					})
					
					tcclToAppend += '</select>';
					
					yesNoModal.show(tcclToAppend, {title: "Select a TCCL to view",
							yesLabel: '<span class="glyphicon glyphicon-search"></span> View',
							noLabel: '<span class="glyphicon glyphicon-chevron-left"></span> Back',
							yesOnClick: function()
							{
								$.ajax({
									cache: false,
									type : 'GET',
									url : 'common/tccl/edit',
									data :
									{
										idTccl : $('#tccl-selector').val()
									},
									success: function (data)
									{
										yesNoModal.hide();
										
										_$viewTcclModal.find('h3').html($('#tccl-selector option:selected').text());
										_$viewTcclTable.find('tbody').empty();
										
										var tcclToAppend = '';
										
										$.each(data, function(i, tc)
										{
											tcclToAppend += '<tr><td class="hide">' + tc[0] + '</td><td width="20%">'
													+ tc[1] + '</td><td width="60%">' + tc[2] + '</td>'
													+ '<td width="10%">' + tc[3]
													+'</td><td width="10%" style="text-align: center"><input class="is_checkbox" type="checkbox"';
											
											if (tc[4])
											{
												tcclToAppend += 'checked';
											}
											
											tcclToAppend += '></tr>';
										});
										
										_$viewTcclTable.find('tbody').append(tcclToAppend);
										
										_$viewTcclModal.modal('show');
										
										_$viewTcclModal.on('shown.bs.modal', function()
										{
											_$viewTcclTable.DataTable().columns.adjust().draw();
										})
										
										if (!_$viewTcclTable.hasClass("dataTable"))
										{
											_$viewTcclTable.DataTable({
												paging: false,
												"sDom": '<"top">rt<"bottom"flp><"clear">',
												scrollY: ($(window).height()/2),
												columnDefs: [        
													{ orderable: false, targets: [3, 4]},
												],
												order: [0, 'asc']
											});			
										}
										
										_$viewTcclTable.DataTable().search('');
										
										_$viewTcclTable.find('tbody').find('.is_checkbox').prop('disabled', true);
								   }
								});
							}
					});
				}
			})
		});
		
		_$navigationElements.on('click', function(e)
		{
			e.preventDefault();
			var navigationElement = $(this);
			var link = $(this).find('a').attr('href');
			
			if ((link == 'ics') || (link == 'parameter') || (link == 'testcase'))
			{
				link+='?idProject='+sessionStorage.getItem('idProject');
			}
			else if (link == 'ixit')
			{
				link+='?idProject='+sessionStorage.getItem('idProject')+'&idDut='+sessionStorage.getItem('idDut');
			}
			
			//jQuery.address.value(link);
			
			if ((!$(this).hasClass('disabled')) && (!$(this).hasClass('selected-step')))
			{
				$.ajax({
		            type: "GET",
		            url: link,
		            success: function(response)
		            {	
		            	if (link == 'project')
	            		{
		            		//navigationElementStatus(false);
		            		disableNavigationBar();
		            				            		
		            		disableNavigationButtons($previousStepButton, $nextStepButton, $saveProjectButton);
		            		
		            		sessionStorage.clear();
	            		}
		            	else
	            		{
		            		enableNavigationButtons($previousStepButton, $saveProjectButton);
		            		            		
		            		if (link.indexOf('testcase') >= 0)
	            			{
		            			disableNavigationButtons($nextStepButton);
	            			}
		            		else
		            		{
		            			enableNavigationButtons($nextStepButton);
		            		}
	            		}
		            	
		            	$dynamicSection.fadeOut('fast', function()
		            	{
		            		$dynamicSection.html(response);
		            	});
		            	
		            	$dynamicSection.fadeIn('fast', function()
		                {
		                	var table = $.fn.dataTable.fnTables(true);
		                	if (table.length > 0)
		                	{
		                	    $(table).dataTable().fnAdjustColumnSizing();
		                	}
		                });
		                
		                _updateBreadcrumbs(link, sessionStorage.getItem('projectName'), sessionStorage.getItem('type'), 
		                		sessionStorage.getItem('associatedDut'), sessionStorage.getItem('associatedGu'));
		                
		                selectNavigationElement(navigationElement);
		            }
		        });
			}
		});
	}
	
	var _updateBreadcrumbs = function(url, projectName, projectType, dutName, guNames)
	{
		if (url.indexOf('project') < 0)
		{
			_showBreadcrumb(_$projectBreadcrumb, projectName, true);
            
            if (url.indexOf('dut') < 0)
            {
            	_showBreadcrumb(_$dutBreadcrumb, dutName, true);
                
                if ((projectType != "Conformance") && (url.indexOf('gu') < 0))
                {
                	_showBreadcrumb(_$guBreadcrumb, guNames, true);
                }
                else
                {
                	_showBreadcrumb(_$guBreadcrumb, 'GU', false);
                }
            }
            else
            {
            	_showBreadcrumb(_$dutBreadcrumb, 'DUT', false);
            }
		}
	}
	
	var _showBreadcrumb = function(breadcrumb, value, shown)
	{
		breadcrumb.text(value);
		
		if (shown)
		{
			breadcrumb.removeClass('hidden');
		}
		else
		{
			breadcrumb.addClass('hidden');
		}
	}
	
	var navigationElementStatus = function(enabled)
	{
		if (enabled)
		{
			_$navigationElements.removeClass('disabled');
	   		_$navigationElements.find('a').removeAttr('disabled', false);
		}
		else
		{
			_$navigationElements.addClass('disabled');
	   		_$navigationElements.find('a').attr('disabled', true);
		}
	}
	
	var logoutFormSubmit = function()
	{
		_$logoutForm.submit();
	};
	
	var disableNavigationButtons = function()
	{
		for (var i = 0; i < arguments.length; i++)
		{
			arguments[i].addClass('disabled');
			arguments[i].attr('disabled', true);
			arguments[i].parent().tooltip('enable');
		}
	}
	
	var enableNavigationButtons = function()
	{
   		for (var i = 0; i < arguments.length; i++)
		{
			arguments[i].removeClass('disabled');
			arguments[i].removeAttr('disabled', false);
			arguments[i].parent().tooltip('disable');
		}
	}
	
	var disableNavigationBar = function()
	{
		_$navigationElements.each(function(i)
		{
			$(this).find('a').attr('disabled', true);

			if (i === 0)
			{
				$(this).addClass('selected-step');
			}
			if (i > 0)
			{
				$(this).addClass('disabled');
				$(this).removeClass('selected-step');
		   		//$(this).find('a').attr('disabled', true);
			}
		});
	}
	
	var enableNavigationBar = function(projectType)
	{
		_$navigationElements.each(function(i)
		{
			if (i === 0)
			{
				$(this).find('a').removeAttr('disabled', false);
			}
			else if (!((i === 2) && (projectType === "Conformance")))
			{
				$(this).removeClass('disabled');
		   		$(this).find('a').removeAttr('disabled', false);
			}
		});
	}
	
	var selectNavigationElement = function(navigationElement)
	{
		var previousSelectedElement = _$navigationList.find('li.selected-step');

		if (previousSelectedElement.is($('#project-nav')))
		{
			previousSelectedElement.find('a').removeAttr('disabled', false);
		}
		else if (sessionStorage.getItem("isConfigured") == "false")
		{
			previousSelectedElement.addClass('disabled');
		}

		navigationElement.removeClass('disabled');
		previousSelectedElement.removeClass('selected-step');
		navigationElement.addClass('selected-step');
	}
	
	var changeTooltipText = function(tooltip, newText)
	{
		tooltip.tooltip().attr('data-original-title', newText);
	}
	
	var adjustDataTablesHeader = function()
	{
		var table = $.fn.dataTable.fnTables(true);
    	if (table.length > 0)
    	{
    	    $(table).dataTable().fnAdjustColumnSizing();
    	}
	}
	
	var formatDateAndTime = function(dateAndTime)
	{
		Number.prototype.padLeft = function(base, chr)
		{
		   var  len = (String(base || 10).length - String(this).length) + 1;
		   return len > 0 ? new Array(len).join(chr || '0') + this : this;
		}
		
		var cd = new Date(dateAndTime);
		return [cd.getFullYear(), (cd.getMonth()+1).padLeft(), cd.getDate().padLeft()].join('-')
               		+ ' ' + [cd.getHours().padLeft(), cd.getMinutes().padLeft(), cd.getSeconds().padLeft()].join(':');
	}
	
	return {
		init: init,
		navigationElementStatus: navigationElementStatus,
		logout: logoutFormSubmit,
		$title: $title,
		csrfToken: token,
		csrfHeader: header,
		$dynamicSection: $dynamicSection,
		$nextStepButton: $nextStepButton,
		$previousStepButton: $previousStepButton,
		$saveProjectButton: $saveProjectButton,
		disableButtons: disableNavigationButtons,
		enableButtons: enableNavigationButtons,
		disableNavigationBar: disableNavigationBar,
		enableNavigationBar: enableNavigationBar,
		selectNavigationElement: selectNavigationElement,
		changeTooltipText: changeTooltipText,
		adjustDataTablesHeader: adjustDataTablesHeader,
		formatDateAndTime: formatDateAndTime
	}
})(common);