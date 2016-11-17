/*******************************************************************************
 *  *      Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
var testcases = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var _titleText = (sessionStorage.getItem("type") == "Conformance") ? 'Step 6<small> Test Plan generation</small>' : 'Step 7<small> Test Plan generation</small>';
	//-------------------------------------------------
	// TEST CASES TABLE
	//-------------------------------------------------
	var _$testCasesTable = jQuery('#tcTable');
	var _testCasesTableRows;
	//-------------------------------------------------
	// SELECT AND DESELECT BUTTONS
	//-------------------------------------------------
	var _$selectAllButton = jQuery('#selectAll');
	var _$deselectAllButton = jQuery('#deselectAll');
	//-------------------------------------------------
	// NAVIGATION BUTTONS MESSAGES
	//-------------------------------------------------
	var _noMoreStepsMessage = 'There are no more steps, please save your project';
	
	var init = function()
	{
		common.$title.html(_titleText);
		
		common.changeTooltipText(common.$nextStepButton.parent(), _noMoreStepsMessage);
		
		_initDataTable();
		_onClickFunctions();
	};
	
	var _initDataTable = function()
	{
		// Firefox has problems managing sessionStorage, so data is stored into a var before sending
		var data = {};
		for (var j = 0; j < sessionStorage.length; j++)
		{
			var key = sessionStorage.key(j);
			data[key] = sessionStorage.getItem(key);
		}
		
		// load all applicable test cases when loading dataTables
		_$testCasesTable.DataTable(
		{
			"ajax": {
				"type": "post",
				"url": "testcase/dataTable",
				"beforeSend": function(xhr)
			   	{
		        	xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
				"data": {
					data: data
				},
				"dataType": "json"
			},
			"columns": [
	            { "data": "id" },
	            { "data": "name", "width": "15%" },
	            { "data": "description", "width": "85%" },
	            { "data": "selected", "width": "5%",
	            	"render": function(data) {
	            		return data == true ? "<input class=\"is_checkbox\" type=\"checkbox\" checked/>" : "<input class=\"is_checkbox\" type=\"checkbox\"/>";
	            	}
	            }
			],
			autoWidth: false,
			paging: false,
			searching: false,
			info: false,
			scrollY: common.$dynamicSection.height() - 122,
			columnDefs: [        
				{ orderable: false, targets: 3},
				{ visible: false, targets: 0}
			],
			order: [0, 'asc']
		}).on('init.dt', function()
		{	
			_testCasesTableRows = _$testCasesTable.find('tbody').find('tr');
			// when finished, load all disabled by TCCL test cases
			$.ajax({
				url: "testcase/disabled",
				type: 'GET',
				data: {
					idProject : sessionStorage.getItem("idProject")
				},
				success: function(data)
				{										
					// for each disable test case
					$.each(data, function(i, disabled)
					{
						// get the associated row or -1 if it is not in the table
						var rowIndex = _$testCasesTable.DataTable().columns(0).data().eq(0).indexOf(disabled);
						
						if (rowIndex > -1)
						{
							// If it is not a Development and configured project
							var checkedCondition = ((sessionStorage.getItem("type") == "Development") && (sessionStorage.getItem("isConfigured") == "true"));
							
							if (!checkedCondition)
							{
								// Disabled test cases are unchecked
								$(_testCasesTableRows[rowIndex]).find('.is_checkbox').prop('checked', false);
							}
							
							// If it is not a development project, rows are disabled
							$(_testCasesTableRows[rowIndex]).find('.is_checkbox').prop('disabled', sessionStorage.getItem("type") != "Development");
							
							if (sessionStorage.getItem("type") != "Development")
							{
								$(_testCasesTableRows[rowIndex]).addClass('text-muted');
							}
						}
					});
				}
			});
			
			// if it is a Development project and no GUs have been selected, IOP test cases will be disabled
			if (sessionStorage.getItem("type") == "Development")
			{
				var noGUs = (jQuery('#gu-breadcrumb').text() == "Not selected");

				for (var j = 0; j < _testCasesTableRows.length; j++)
				{
					if ((noGUs) && (_$testCasesTable.DataTable().row($(_testCasesTableRows[j])).data().name.indexOf("IOP") >= 0))
					{
						$(_testCasesTableRows[j]).find('.is_checkbox').prop('checked', false);
						$(_testCasesTableRows[j]).find('.is_checkbox').prop('disabled', true);
						$(_testCasesTableRows[j]).addClass('text-muted');
					}
				}
			}
			
			// add switch value on double click to each enabled row
			_testCasesTableRows.dblclick(function()
			{
				if (!($(this).hasClass("text-muted")))
				{
					var val = $(this).find('.is_checkbox').is(':checked');
					$(this).find('.is_checkbox').prop('checked', !(val));
				}
			});
		});
	};
	
	var _onClickFunctions = function()
	{
		_onClickSaveProject();
		_onClickSelectors();
		_onClickPrevious();
	}
	
	var _onClickSaveProject = function()
	{
		common.$saveProjectButton.off('click').on('click', function()
		{
			for (var i = 0; i < _testCasesTableRows.length; i++)
			{
				var id = _$testCasesTable.DataTable().row($(_testCasesTableRows[i])).data().name;
				var value = $(_testCasesTableRows[i]).find('.is_checkbox').is(':checked');
				
				if (value == true)
				{
					sessionStorage.setItem(id, value);
				}
			}
			
			sessionStorage.setItem("applyTC", ($('.is_checkbox').length) - ($('.text-muted').length));
			
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
	                    
	                	common.disableButtons(common.$previousStepButton, common.$nextStepButton, common.$saveProjectButton);
	                    common.disableNavigationBar();
			        }
			});	
		});
	}
	
	var _onClickSelectors = function()
	{
		_$selectAllButton.on('click', function()
		{
			$('.is_checkbox').prop('checked', true);
			$('.text-muted').find('.is_checkbox').prop('checked', false);
			
		});

  		_$deselectAllButton.on('click', function()
  		{
			$('.is_checkbox').prop('checked', false);
		});
	}
	
	var _onClickPrevious = function()
	{
		common.$previousStepButton.off('click').on('click', function(e)
		{
			e.preventDefault();
			
			$('#idProject').val(sessionStorage.getItem('idProject'));
			
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
						common.adjustDataTablesHeader();
	                });
	                
					common.enableButtons(common.$nextStepButton);
					
					if (sessionStorage.getItem("isConfigured") == "false")
					{
						common.disableButtons(common.$saveProjectButton);
					}
					
					common.selectNavigationElement($('#gp-nav'));
				}
			});
		});
	}
	
	return {
		init: init
	}
})(testcases);