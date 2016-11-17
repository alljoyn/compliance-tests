/*******************************************************************************
 *  * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
 *
 *      SPDX-License-Identifier: Apache-2.0
 *
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *      Alliance. All rights reserved.
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
var ics = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var _titleText = (sessionStorage.getItem("type") == "Conformance") ? 'Step 3<small> Configure your ICS</small>' : 'Step 4<small> Configure your ICS</small>';
	//-------------------------------------------------
	// ALERT MESSAGES
	//-------------------------------------------------
	var _scrNeededMessage = 'You need to perform SCR verification';
	var _fixErrorsMessage = 'You need to correct invalid ICS values';
	//-------------------------------------------------
	// ICS TABLES
	//-------------------------------------------------
	var _$icsTables = jQuery('.table');
	var _icsTablesRows;
	//-------------------------------------------------
	// BUTTONS
	//-------------------------------------------------
	var _$scrButton = jQuery('#scrButton')
	var _$changeSfIcsButton = jQuery('#changeButton');
	
	var init = function()
	{		
		common.$title.html(_titleText);
		
		common.changeTooltipText(common.$nextStepButton.parent(), _scrNeededMessage);
		
		if (sessionStorage.getItem("isConfigured") == "true")
		{
			common.enableButtons(common.$nextStepButton);
		}
		else
		{
			common.disableButtons(common.$nextStepButton);
		}
		
		initDataTable();
		onClickFunctions();
		onChangeFunctions();
	};
	
	var initDataTable = function()
	{
		$('#1').addClass('in active');
		
		$("#1").find('table').dataTable({
			autoWidth: false,
			paging: false,
			searching: false,
			info: false,
			scrollY: common.$dynamicSection.height() - 128,
			columnDefs: [       
			    { width: "3%", targets: 0},
			    { width: "30%", targets: 1},
			    { width: "57%", targets: 2},
				{ width: "10%", orderable: false, targets: 3}
			],
			order: [0, 'asc']
		});
		
		_icsTablesRows = _$icsTables.find('tbody').find('tr');
		
		for (var i = 0; i < _icsTablesRows.length; i++)
		{
			var id = $(_icsTablesRows[i]).find('td:eq(1)').html();
			
			if (sessionStorage.hasOwnProperty(id))
			{
				$(_icsTablesRows[i]).find('.form-control').val(sessionStorage.getItem(id));
			}
		}
	
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e)
		{
		   e.target; // active tab
		   e.relatedTarget; // previous tab
		   var tab = $(e.target).attr('href');
		   
		   if (!$("" + tab).find('table').hasClass('dataTable'))
		   {
			   $("" + tab).find('table').dataTable({
				    autoWidth: false,
				   	paging: false,
					searching: false,
					info: false,
					scrollY: common.$dynamicSection.height() - 128,
					columnDefs: [        
						{ width: "3%", targets: 0},
					    { width: "30%", targets: 1},
					    { width: "57%", targets: 2},
						{ width: "10%", orderable: false, targets: 3}
					],
					order: [0, 'asc']
			   });
		   }
		});
	};
	
	var onClickFunctions = function()
	{
		_icsTablesRows.dblclick(function()
		{
			var val = $(this).find('.form-control').val();
			$(this).find('.form-control').val((val == "false").toString());
			
			common.disableButtons(common.$nextStepButton, common.$saveProjectButton);
		});
		
		common.$previousStepButton.off('click').on('click', function(e)
		{
			e.preventDefault();
			
			$('#prevIdProject').val(sessionStorage.getItem('idProject'));
			
			$.ajax({
				type: 'GET',
				url: $('#prevForm').attr('action'),
				data: $('#prevForm').serialize(),
				success: function(response)
				{
					common.$dynamicSection.fadeOut('fast', function()
					{
	            		common.$dynamicSection.html(response);
	            	});
	                common.$dynamicSection.fadeIn('fast', function()
					{
	                	if (sessionStorage.getItem("isConfigured") == "true")
	            		{
	                		common.enableButtons(common.$saveProjectButton);
	            		}
	                	
	                	if (sessionStorage.getItem("type") != "Conformance")
	                	{
	                		$('#gu-breadcrumb').text('GU');
			                $('#gu-breadcrumb').addClass('hidden');
			                common.selectNavigationElement($('#gu-nav'));
	                	}
	                	else
	                	{
	                		$('#dut-breadcrumb').text('DUT');
			                $('#dut-breadcrumb').addClass('hidden');
			                common.selectNavigationElement($('#dut-nav'));
	                	}
	                	
	                	common.adjustDataTablesHeader();
	                });
				}
			});
			
		});
  		
		common.$nextStepButton.off('click').on('click', function(e)
		{
			e.preventDefault();
			$('#idProject').val(sessionStorage.getItem('idProject'));
			$('#idDut').val(sessionStorage.getItem('idDut'));
			
			sessionStorage.setItem('setIcs', true);
			
			for (var i = 0; i < _icsTablesRows.length; i++)
			{
				var id = $(_icsTablesRows[i]).find('td:eq(1)').html();
				var value = $(_icsTablesRows[i]).find('.form-control option:selected').html();

				sessionStorage.setItem(id, value);
			}
			
			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedIcs", true);
			}
			
			$.ajax({
				type: 'GET',
				url: $('#nextForm').attr('action'),
				data: $('#nextForm').serialize(),
				success: function(response)
				{
					common.$dynamicSection.fadeOut('fast', function()
					{
						common.$dynamicSection.html(response);
	            	});
	                
					common.$dynamicSection.fadeIn('fast', function()
					{
	                	common.adjustDataTablesHeader();
	                });
					
					common.selectNavigationElement($('#ixit-nav'));
				}
			});	
		});
  		
  		_$scrButton.on('click', function()
  		{	
  			waitingDialog.show('Processing...');
  			
  			$('.badge').text("");
  			
			for (var i = 0; i < _icsTablesRows.length; i++)
			{
				var id = $(_icsTablesRows[i]).find('td:eq(1)').text();
				var value = $(_icsTablesRows[i]).find('.form-control').val();

				sessionStorage.setItem(id, value);
			}
			
			var data = {};
			for (var j = 0; j < sessionStorage.length; j++)
			{
				var key = sessionStorage.key(j);
				data[key] = sessionStorage.getItem(key);
			}
			
  			$.ajax({
  				   cache: false,
				   url: "ics/scr",
				   type: 'GET',
				   data: {
						data : data
					},
					dataType : 'json',
				   success: function (data)
				   {
						var wrong = 0;
						$.each(data, function(i, result)
						{
							if (!result.result)
							{
								for (var i = 0; i < _icsTablesRows.length; i++)
								{
									if($(_icsTablesRows[i]).find('td:eq(0)').html() == result.id)
									{
										$(_icsTablesRows[i]).addClass('danger');
									}
								}
								var label = "badge" + result.idService;
								
								if (($('#' + label).text()) == "")
								{
									$('#' + label).text("1");
								}
								else
								{
									var e = parseInt($('#' + label).text());
									e += 1;
									$('#' + label).text(e);
								}
								wrong++;	
							}
							else
							{
								for (var i = 0; i < _icsTablesRows.length; i++)
								{
									if ($(_icsTablesRows[i]).find('td:eq(0)').html() == result.id)
									{
										$(_icsTablesRows[i]).removeClass('danger');
									}
								}
							}
							waitingDialog.hide();
						});
						
						if (wrong == 0)
						{
							common.enableButtons(common.$nextStepButton);
							
							if (sessionStorage.getItem("isConfigured") == "true")
							{
								common.enableButtons(common.$saveProjectButton);
							}
						}
						else
						{
							common.changeTooltipText(common.$nextStepButton.parent(), _fixErrorsMessage);
						}
				   }
			});
  		});
  		
  		_$changeSfIcsButton.on('click', function()
  		{
  			common.disableButtons(_$scrButton);
  			
  			$.each($(".tab-pane.active").find('.form-control'), function()
  			{
  				$(this).val(($(this).val() == "false").toString());
  			});
  			
  			common.enableButtons(_$scrButton);
			
			common.disableButtons(common.$nextStepButton, common.$saveProjectButton);
  		});
  		
  		common.$saveProjectButton.off('click').on('click', function(e)
  		{
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedIcs", true);
  			
			for (var i = 0; i < _icsTablesRows.length; i++)
			{
				var id = $(_icsTablesRows[i]).find('td:eq(1)').html();
				var value = $(_icsTablesRows[i]).find('.form-control option:selected').html();

				sessionStorage.setItem(id, value);
			}
			
			var data = {};
			for (var j = 0; j < sessionStorage.length; j++)
			{
				var key = sessionStorage.key(j);
				data[key] = sessionStorage.getItem(key);
			}
			
			$.ajax({
			   	url: 'end/save',
			   	type: 'POST',
			   	beforeSend: function(xhr)
			   	{
		        	xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
			   	data:
				{
				   	data : data	
				},
		        success: function(response)
		        {
		        	common.$dynamicSection.fadeOut('fast', function()
                	{
                		common.$dynamicSection.html(response);
                	});
                    common.$dynamicSection.fadeIn('fast');
                    
                    common.disableNavigationBar();
                    common.disableButtons(common.$previousStepButton, common.$nextStepButton, common.$saveProjectButton);
		        }
			});	
  		});
	}
	
	var onChangeFunctions = function()
	{
		$('.form-control').on('change', function()
		{
			common.disableButtons(common.$nextStepButton, common.$saveProjectButton);
			common.changeTooltipText(common.$nextStepButton.parent(), _scrNeededMessage);
    	});
	}
	
	return {
		init: init
	}
})(ics);