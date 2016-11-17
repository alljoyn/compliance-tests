/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
var duts = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var _titleText = "Step 2<small> Select/Edit/Create a Device Under Test (DUT)</small>";
	//-------------------------------------------------
	// DUTS TABLE
	//-------------------------------------------------
	var _$dutTable = jQuery('#dutTable');
	//-------------------------------------------------
	// TOOLTIPS
	//-------------------------------------------------
	var _noSelectedDutMessage = 'You need to select a DUT to continue';
	//-------------------------------------------------
	// DUT SECTION BUTTONS
	//-------------------------------------------------
	var _$editDutButton = jQuery('#editButton');
	var _$deleteButton = jQuery('#deleteButton');
	//-------------------------------------------------
	// DUT CREATION MODAL
	//-------------------------------------------------
	var _$newDutModal = jQuery('#newDutModal');
	
	var _$newDutForm = jQuery("#newDutForm");
	var _$newDutNameField = jQuery('#dut-name');
	var _$newDutOemField = jQuery('#dut-manufacturer');
	var _$newDutModelField = jQuery('#dut-model');
	var _$newDutDescriptionField = jQuery('#dut-description');
	var _$newDutDeviceIdField = jQuery('#dut-device-id');
	var _$newDutAppIdField = jQuery('#dut-app-id');
	var _$newDutSwField = jQuery('#dut-sw-ver');
	var _$newDutHwField = jQuery('#dut-hw-ver');
	
	var _$confirmCreationButton = jQuery('#createDut');
	var _$cancelCreationButton = jQuery('#createCancel');
	//-------------------------------------------------
	// DUT EDITION MODAL
	//-------------------------------------------------
	var _$editDutModal = jQuery('#editDutModal');
	
	var _$editDutForm = jQuery('#editDutForm');
	var _$editDutIdField = jQuery('#edit-id');
	var _$editDutNameField = jQuery('#edit-name');
	var _$editDutOemField = jQuery('#edit-manufacturer');
	var _$editDutModelField = jQuery('#edit-model');
	var _$editDutDescriptionField = jQuery('#edit-description');
	
	var _$confirmEditionButton = jQuery('#editConfirm');
	var _$cancelEditionButton = jQuery('#editCancel');
	//-------------------------------------------------
	// DUT DELETION MODAL
	//-------------------------------------------------
	var _$confirmDeletionButton = jQuery('#deleteConfirm');
	//-------------------------------------------------
	// SAMPLES TABLE
	//-------------------------------------------------
	var _$samplesTableModal = jQuery('#samplesTableModal');
	var _$sampleTable = jQuery('#sampleTable');
	//-------------------------------------------------
	// SAMPLES SECTION BUTTONS
	//-------------------------------------------------
	var _$backFromSampleToDutButton = jQuery('#sampleBack');
	var _$editSampleButton = jQuery('#editSample');
	var _$deleteSampleButton = jQuery('#deleteSample');
	
	var _$deleteSampleTooltip = jQuery('#deleteSampleButtonTooltip');
	//var _oneSampleMessage = 'At least one sample must be stored for each existing DUT';
	//-------------------------------------------------
	// SAMPLE CREATION MODAL
	//-------------------------------------------------
	var _$newSampleModal = jQuery('#newSampleModal');
	
	var _$newSampleForm = jQuery('#newSampleForm');
	var _$newSampleDeviceIdField = jQuery('#sample-device-id');
	var _$newSampleAppIdField = jQuery('#sample-app-id');
	var _$newSampleSwField = jQuery('#sample-sw-ver');
	var _$newSampleHwField = jQuery('#sample-hw-ver');
	
	var _$confirmSampleCreationButton = jQuery('#createSample');
	var _$cancelSampleCreationButton = jQuery('#cancelCreateSample');
	//-------------------------------------------------
	// SAMPLE EDITION MODAL
	//-------------------------------------------------
	var _$editSampleModal = jQuery('#editSampleModal');
	
	var _$editSampleForm = jQuery('#editSampleForm');
	var _$editSampleIdField = jQuery('#edit-sample-id');
	var _$editSampleDeviceIdField = jQuery('#edit-device-id');
	var _$editSampleAppIdField = jQuery('#edit-app-id');
	var _$editSampleSwField = jQuery('#edit-sw-ver');
	var _$editSampleHwField = jQuery('#edit-hw-ver');
	
	var _$confirmSampleEditionButton = jQuery('#editSampleConfirm');
	var _$cancelSampleEditionButton = jQuery('#cancelEditSample');
	//-------------------------------------------------
	// NAVIGATION BUTTONS
	//-------------------------------------------------
	var _$nextStepForm = jQuery('#nextForm');
	
	var init = function()
	{
		_initStepTexts();	
		_initDataTable();
		_initTooltips();
		
		onClickFunctions();
		_validateForms();
	};
	
	var _initStepTexts = function()
	{
		common.$title.html(_titleText);
		common.changeTooltipText(common.$nextStepButton.parent(), _noSelectedDutMessage);
	}
	
	var _initDataTable = function()
	{
		_$dutTable.dataTable({
			pagingType: 'full_numbers',
			scrollY: common.$dynamicSection.height() - 164,
			order: [0, 'asc'],
			columnDefs: [
			    { visible: false, searchable: false, targets: 0},
				{orderable: false, targets: 7}             
			]
		});
		
		var dutTableRows = _$dutTable.DataTable().rows().nodes().to$();
		
		for (var i = 0; i < dutTableRows.length; i++)
		{
			var data = _$dutTable.DataTable().row($(dutTableRows[i])).data();
			
			if ((data[1]) == sessionStorage.getItem("associatedDut"))
			{
				$(dutTableRows[i]).addClass('selected');
				
				sessionStorage.setItem("idDut", data[0]);
				sessionStorage.setItem("aIdDut", data[0]);
				sessionStorage.setItem("dutName", data[1]);

				common.enableButtons(common.$nextStepButton);
				_dutSectionButtonsEnabled(true);
			}
		}
	};
	
	var _dutSectionButtonsEnabled = function(status)
	{
		_disableButton(_$editDutButton, !status);
		_disableButton(_$deleteButton, !status);
	}
	
	var _disableButton = function(button, isDisabled)
	{
		if (isDisabled)
		{
			button.addClass('disabled');
		}
		else
		{
			button.removeClass('disabled');
		}
		
		button.prop("disabled", isDisabled);
	}
	
	var _initTooltips = function()
	{
		_$deleteSampleTooltip.tooltip({'placement': 'bottom'});
		_$deleteSampleTooltip.tooltip('disable');
	}
	
	var onClickFunctions = function()
	{
		_onClickTableRow();
		_onClickSampleTableRow();
		_onClickSectionButtons();
		_onClickSampleSectionButtons();
		_onClickNavigationButtons();
	}
	
	var _onClickTableRow = function()
	{
		_$dutTable.find('tbody').on('click', 'tr', function()
		{
			var data = _$dutTable.DataTable().row(this).data();
			
			if ($(this).hasClass('selected'))
			{
				$(this).removeClass('selected');
				sessionStorage.removeItem("idDut");

				common.disableButtons(common.$nextStepButton, common.$saveProjectButton);
				_dutSectionButtonsEnabled(false);
			}
			else
			{
				_$dutTable.DataTable().$('tr.selected').removeClass('selected');
				$(this).addClass('selected');

			   	sessionStorage.setItem("idDut", data[0]);
			   	sessionStorage.setItem("dutName", data[1]);

			   	common.enableButtons(common.$nextStepButton);
			   	
			   	if (sessionStorage.getItem("isConfigured") == "true")
			   	{
			   		common.enableButtons(common.$saveProjectButton);
			   	}
			   	_dutSectionButtonsEnabled(true);
			}
		});
		
		_$dutTable.find('tbody').on('click', '.sample-link', function(e)
		{
			e.preventDefault();
			
			if ($(this).parent().parent().hasClass('selected'))
			{
				e.stopPropagation();
			}
			
			var selectedDutId = _$dutTable.DataTable().row($(this).parent().parent()).data()[0];
			
			$('#sample-dut').val(selectedDutId);
			
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'dut/samples',
				data :
				{
					idDut: selectedDutId
				},
				success: function (samples)
				{
					_$sampleTable.find('tbody').empty();
					
					var samplesToAppend = "";
					
					$.each(samples, function(i, sample)
					{
						samplesToAppend += "<tr><td class=\"hide\">" + sample.idSample + "</td><td>"
								+ sample.deviceId + "</td><td>" + sample.appId + "</td><td>"
								+ sample.swVer + "</td><td>" + sample.hwVer + "</td></tr>";
					});
					
					_$sampleTable.find('tbody').append(samplesToAppend);
					
					_$samplesTableModal.modal('show');
			   }
			});
		});
	}
	
	var _onClickSampleTableRow = function()
	{
		_$sampleTable.find('tbody').on('click', 'tr', function()
		{
			if ($(this).hasClass('selected'))
			{
				$(this).removeClass('selected');
				sessionStorage.removeItem("idSample");
				
				_sampleSectionButtonsEnabled(false);
			}
			else
			{
				$(this).addClass('selected').siblings().removeClass('selected');    
			   	var id = $(this).find('td:first').html();
			   	sessionStorage.setItem("idSample", id);
			   	
			   	if (_$sampleTable.find('tbody tr').length > 1)
		   		{
			   		_sampleSectionButtonsEnabled(true);
		   		}
			   	else
		   		{
			   		_disableButton(_$editSampleButton, false);
			   		_$deleteSampleTooltip.tooltip('enable');
		   		}
			}
		});
	}
	
	var _sampleSectionButtonsEnabled = function(status)
	{
		_disableButton(_$editSampleButton, !status);
		_disableButton(_$deleteSampleButton, !status);
		_$deleteSampleTooltip.tooltip('disable');
	}
	
	var _onClickSectionButtons = function()
	{
		_onClickCreateButton();
		_onClickEditButtons();
		_onClickDeleteButton();
	}
	
	var _onClickCreateButton = function()
	{
		_$newDutForm.submit(function(e)
		{
			e.preventDefault();
			
			$.ajax({
	    		type: 'post',
	    		url: _$newDutForm.attr('action'),
	    		beforeSend: function(xhr)
	    		{
		            xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
	    		data: _$newDutForm.serialize(),
	    		success: function(dut)
	    		{
	    			_$dutTable.DataTable().row.add([dut.idDut, dut.name, common.formatDateAndTime(dut.createdDate), 
	    			                                common.formatDateAndTime(dut.modifiedDate), dut.manufacturer, dut.model, 
	    			                                dut.description, "<a href=\"#\" class=\"sample-link\">Samples</a>"]).draw();
                	_$newDutModal.modal('hide');
                	_clearCreateDutForm();
	    		}
	    	})
		});
		
		_$confirmCreationButton.on('click', function()
		{
	    	if (_$newDutForm.valid())
	    	{
	    		_$newDutForm.submit();
	    	}
	  	});
		
		_$cancelCreationButton.on('click', function()
		{
			_clearCreateDutForm();
		});
	}
	
	var _clearCreateDutForm = function()
	{
		_$newDutNameField.val('');
		_$newDutOemField.val('');
		_$newDutModelField.val('');
		_$newDutDescriptionField.val('');
		_$newDutDeviceIdField.val('');
		_$newDutAppIdField.val('');
		_$newDutSwField.val('');
		_$newDutHwField.val('');
    	
    	_$newDutForm.clearValidation();
	}

	var _onClickEditButtons = function()
	{
		_$editDutButton.on('click', function()
		{
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'dut/edit',
				data :
				{
					idDut : sessionStorage.getItem("idDut")
				},
				success: function (data)
				{	
					_$editDutIdField.val(data.idDut);
					_$editDutNameField.val(data.name);
					_$editDutOemField.val(data.manufacturer);
					_$editDutModelField.val(data.model);
					_$editDutDescriptionField.val(data.description);
			   }
			});
		});
		
		_$editDutForm.submit(function(e)
		{
			e.preventDefault();
			
			$.ajax({
                url: _$editDutForm.attr('action'),
                type: 'POST',
                beforeSend: function(xhr)
                {
                	xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
                data: _$editDutForm.serialize(),
                success: function(dut)
                {
                	var selectedRow = _$dutTable.find('tbody').find('tr.selected');
                	
                	_$dutTable.dataTable().fnUpdate(dut.name, selectedRow, 1, false);
                	_$dutTable.dataTable().fnUpdate(common.formatDateAndTime(dut.modifiedDate), selectedRow, 3, false);
                	_$dutTable.dataTable().fnUpdate(dut.manufacturer, selectedRow, 4, false);
                	_$dutTable.dataTable().fnUpdate(dut.model, selectedRow, 5, false);
                	_$dutTable.dataTable().fnUpdate(dut.description, selectedRow, 6, false);
                	
                	_$editDutModal.modal('hide');
                	_clearEditDutForm();
                }
            });
		});
		
		_$confirmEditionButton.on('click', function(e)
		{
			if (_$editDutForm.valid())
			{
				_$editDutForm.submit();
			}
		});
		
		_$cancelEditionButton.on('click', function()
		{
			_clearEditDutForm();
		});
	}
	
	var _clearEditDutForm = function()
	{
		_$editDutIdField.val('');
		_$editDutNameField.val('');
		_$editDutOemField.val('');
		_$editDutModelField.val('');
		_$editDutDescriptionField.val('');
		
		_$editDutForm.clearValidation();
	}
	
	var _onClickDeleteButton = function()
	{
		_$confirmDeletionButton.on('click', function()
		{  
		    $.ajax({
					type : 'POST',
					url : 'dut/delete',
					beforeSend: function(xhr)
					{
						xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
			        },
					data :
					{
						idDut : sessionStorage.getItem("idDut")
					},
					success: function()
					{						
						_$dutTable.DataTable().row('.selected').remove().draw();
						
						if (sessionStorage.getItem("idDut") == sessionStorage.getItem("aIdDut"))
						{
							$('.nav-stacked li').addClass('disabled');
					   		$('.nav-stacked li a').attr('disabled', true);
		            		common.disableButtons(common.$nextStepButton, common.$saveProjectButton);
		            		
		            		sessionStorage.getItem("aIdDut");
		            		sessionStorage.setItem("isConfigured", false);
						}
						
						sessionStorage.removeItem("idDut");
						
						common.disableButtons(common.$nextStepButton, common.$saveProjectButton);
						_dutSectionButtonsEnabled(false);
					}
			});
		});
	}
	
	var _onClickSampleSectionButtons = function()
	{
		_onClickBackSampleButton();
		_onClickCreateSampleButton();
		_onClickEditSampleButtons();
		_onClickDeleteSampleButton();
	}
	
	var _onClickBackSampleButton = function()
	{
		_$backFromSampleToDutButton.on('click', function()
		{
			_sampleSectionButtonsEnabled(false);
		});
	}
	
	var _onClickCreateSampleButton = function()
	{
		_$newSampleForm.submit(function(e)
		{
			e.preventDefault();
	    	
	    	$.ajax({
	    		type: 'post',
	    		url: _$newSampleForm.attr('action'),
	    		beforeSend: function(xhr)
	    		{
	    			xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
	    		data: _$newSampleForm.serialize(),
	    		success: function(sample)
	    		{
	    			_$sampleTable.find('tbody').append("<tr><td class=\"hide\">" + sample.idSample + "</td><td>"
							+ sample.deviceId + "</td><td>" + sample.appId + "</td><td>"
							+ sample.swVer + "</td><td>" + sample.hwVer + "</td></tr>");
	    			
	    			_$newSampleModal.modal('hide');
	    			_clearSampleFormFields(_$newSampleForm, _$newSampleDeviceIdField, _$newSampleAppIdField, _$newSampleSwField, _$newSampleHwField);
	    			_$samplesTableModal.modal('show');
	    			
	    			_$deleteSampleTooltip.tooltip('disable');
	    			if (_$sampleTable.find('tbody tr.selected'))
    				{
	    				_disableButton(_$deleteSampleButton, false);
    				}
	    		}
	    	})
		})
		
	  	_$confirmSampleCreationButton.on('click', function()
	  	{
	    	if (_$newSampleForm.valid())
	    	{
	    		_$newSampleForm.submit();
	    	}
	  	});
		
		_$cancelSampleCreationButton.on('click', function()
		{
			_clearSampleFormFields(_$newSampleForm, _$newSampleDeviceIdField, _$newSampleAppIdField, _$newSampleSwField, _$newSampleHwField);
		});
	}
	
	var _clearSampleFormFields = function(form, deviceIdField, appIdField, swField, hwField)
	{
		deviceIdField.val('');
		appIdField.val('');
		swField.val('');
		hwField.val('');
    	
    	form.clearValidation();
	}
	
	var _onClickEditSampleButtons = function()
	{
		_$editSampleButton.on('click', function()
		{
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'dut/samples/edit',
				data :
				{
					idSample : sessionStorage.getItem("idSample")
				},
				success: function (data)
				{
					_$editSampleDeviceIdField.val(data.deviceId);
					_$editSampleAppIdField.val(data.appId);
					_$editSampleSwField.val(data.swVer);
					_$editSampleHwField.val(data.hwVer);
					_$editSampleIdField.val(data.idSample);
			   }
			});
		});
		
		_$editSampleForm.submit(function(e)
		{
			e.preventDefault();
			
			$.ajax({
	    		type: 'post',
	    		url: _$editSampleForm.attr('action'),
	    		beforeSend: function(xhr)
	    		{
	    			xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
	    		data: _$editSampleForm.serialize(),
	    		success: function(sample)
	    		{
	    			var sampleTableRows = _$sampleTable.find('tbody').find('tr');
	    			
					for (var i = 0; i < sampleTableRows.length; i++)
					{
						if (($(sampleTableRows[i]).find('td:eq(0)').html()) == sessionStorage.getItem("idSample"))
						{
							$(sampleTableRows[i]).empty();
							$(sampleTableRows[i]).append("<td class=\"hide\">" + sample.idSample + "</td><td>"
										+ sample.deviceId + "</td><td>" + sample.appId + "</td><td>"
										+ sample.swVer + "</td><td>" + sample.hwVer + "</td>");
						}
					}
	    			_$editSampleModal.modal('hide');
	    			_clearSampleFormFields(_$editSampleForm, _$editSampleDeviceIdField, _$editSampleAppIdField, _$editSampleSwField, _$editSampleHwField);
	    			_$samplesTableModal.modal('show');
	    		}
	    	})
		})
		
	    _$confirmSampleEditionButton.on('click', function(e)
	    {
			if (_$editSampleForm.valid())
			{
				_$editSampleForm.submit();
			}
		});
		
		_$cancelSampleEditionButton.on('click', function()
		{
			_clearSampleFormFields(_$editSampleForm, _$editSampleDeviceIdField, _$editSampleAppIdField, _$editSampleSwField, _$editSampleHwField);
		});
	}
	
	var _onClickDeleteSampleButton = function()
	{
		_$deleteSampleButton.on('click', function()
		{		    
		    $.ajax({
		    		type : 'POST',
					url : 'dut/samples/delete',
					beforeSend: function(xhr)
					{
			            xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
			        },
					data :
					{
						idSample : sessionStorage.getItem("idSample")
					},
					success: function()
					{
					    var sampleTableRows = _$sampleTable.find('tbody').find('tr');
					    
						for (var i = 0; i < sampleTableRows.length; i++)
						{
							if (($(sampleTableRows[i]).find('td:eq(0)').html()) == sessionStorage.getItem("idSample"))
							{
								$(sampleTableRows[i]).fadeOut(400, function()
								{
									$(this).remove();
								});
							}
						}
						
						sessionStorage.removeItem("idSample");
						
						//disableSampleButtons();
						_sampleSectionButtonsEnabled(false);
					}
			});
		});
	}
	
	var _onClickNavigationButtons = function()
	{
		_onClickNextButton();
		_onClickPrevButton();
		_onClickSaveButton();
	}
	
	var _onClickNextButton = function()
	{
		_$nextStepForm.submit(function(e)
		{	
			e.preventDefault(); // avoid to execute the actual submit of the form.
			
		    $.ajax({
		           type: "POST",
		           url: _$nextStepForm.attr('action'),
		           beforeSend: function(xhr)
		           {
		        	   xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
			        },
		           data: _$nextStepForm.serialize(),
		           success: function(response)
		           {
		        	   	common.$dynamicSection.fadeOut('fast', function()
		        		{
		        	   		common.$dynamicSection.html( response );
		            	});
		                
		        	   	common.$dynamicSection.fadeIn('fast', function()
		        	   	{
		                	common.adjustDataTablesHeader();
		                });
		           }
		    });
		})

		common.$nextStepButton.off('click').on('click', function(e)
		{
			e.preventDefault();
			
			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedDut", true);
			}
			
			$('#idProject').val(sessionStorage.getItem("idProject"));
			$('#idDut').val(sessionStorage.getItem("idDut"));
			sessionStorage.setItem("associatedDut", sessionStorage.getItem("dutName"));	
			
			_$nextStepForm.submit();
			
			$('#dut-breadcrumb').text(sessionStorage.getItem("dutName"));
	        $('#dut-breadcrumb').removeClass('hidden');
	        
	        if ((sessionStorage.getItem("type") != "Development") && (sessionStorage.getItem("type") != "Conformance"))
	        {
	        	common.disableButton(common.$nextStepButton);
	        }
	        
	        if (sessionStorage.getItem("type") != "Conformance")
	        {
	        	common.selectNavigationElement($('#gu-nav'));
	        }
	        else
	        {
	        	common.selectNavigationElement($('#ics-nav'));
	        }
		});
	}
	
	var _onClickPrevButton = function()
	{
		common.$previousStepButton.off('click').on('click', function()
		{
			$.ajax({
	            type: "GET",
	            url: 'project',
	            success: function(response)
	            {
	            	common.disableNavigationBar();
	            	
	            	common.$dynamicSection.fadeOut('fast', function()
	            	{
	            		common.$dynamicSection.html( response );
	            	});
	                
	            	common.$dynamicSection.fadeIn('fast', function()
	            	{
	                	common.adjustDataTablesHeader();
	                });
	            	
	            	common.disableButtons(common.$previousStepButton, common.$nextStepButton, common.$saveProjectButton);
	                
	                $('#project-breadcrumb').text('Project');
	                $('#project-breadcrumb').addClass('hidden');
	            }
	        });
		});
	}
	
	var _onClickSaveButton = function()
	{
		common.$saveProjectButton.off('click').on('click', function(e)
		{
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedDut", true);
  			
  			$('#idProject').val(sessionStorage.getItem("idProject"));
			$('#idDut').val(sessionStorage.getItem("idDut"));
  			
  			$.ajax({
 	           type: "POST",
 	           url: _$nextStepForm.attr('action'),
 	           beforeSend: function(xhr)
 	           {
 	        	   xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
 		        },
 	           data: _$nextStepForm.serialize(),
 	           success: function(response)
 	           {
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
	 	 	                		common.$dynamicSection.html( response );
	 	 	                	});
	 	 	                    common.$dynamicSection.fadeIn('fast');
	 	 	                    
	 	 	                    common.disableNavigationBar();
	 	 	                    common.disableButtons(common.$nextStepButton, common.$previousStepButton, common.$saveProjectButton);
	 	 			        }
	 	 			});	
 	           }
 	         });	
  		});
	}
	
	var _validateForms = function()
	{
		_validateDutForms();
		_validateSampleForms();
	}
	
	var _validateDutForms = function()
	{
		_$newDutForm.validate({
			rules: {
				name: {
					required: true,
					maxlength: 255,
					remote: {
						url: "dut/validateName",
						type: "get",
						data: {
							id: 0,
							name: function() {
								return $('#dut-name').val();
							}
						}
					}
				},
				manufacturer: {
					required: true,
					maxlength: 60,
				},
				model: {
					required: true,
					maxlength: 60,
				},
				description: {
					maxlength: 255,
				},
				deviceId: {
					required: true,
					maxlength: 60,
				},
				appId: {
					required: true,
					maxlength: 36,
					pattern: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
				},
				swVer: {
					required: true,
					maxlength: 60,
				},
				hwVer: {
					maxlength: 60,
				}
			},
			messages: {
				name: {
					required: "Please enter DUT name!",
					maxlength: "DUT name must have a max of 255 characters!",
					remote: "DUT already exists!"
				},
				manufacturer: {
					required: "Please enter OEM!",
					maxlength: "Manufacturer must have a max of 60 characters!"
				},
				model: {
					required: "Please enter model!",
					maxlength: "Model must have a max of 60 characters!"
				},
				description: {
					maxlength: "Description must have a max of 255 characters!"
				},
				deviceId: {
					required: "Please enter device ID of the sample!",
					maxlength: "Device ID must have a max of 60 characters!"
				},
				appId: {
					required: "Please enter app ID of the sample!",
					maxlength: "App ID must have a max of 60 characters!",
					pattern: "Please enter a valid UUID! (Example: 12345678-9abc-1def-8012-000000000000)"
				},
				swVer: {
					required: "Please enter software version of the sample!",
					maxlength: "Software version must have a max of 60 characters!"
				},
				hwVer: {
					maxlength: "Hardware version must have a max of 60 characters!"
				}
			}
		});
		
		_$editDutForm.validate({
			rules: {
				name: {
					required: true,
					maxlength: 255,
					remote: {
						url: "dut/validateName",
						type: "get",
						data: {
							id: function() {
								return $('#edit-id').val();
							},
							name: function() {
								return $('#edit-name').val();
							}
						}
					}
				},
				manufacturer: {
					required: true,
					maxlength: 60,
				},
				model: {
					required: true,
					maxlength: 60,
				},
				description: {
					maxlength: 255,
				}
			},
			messages: {
				name: {
					required: "Please enter DUT name!",
					maxlength: "DUT name must have a max of 255 characters!",
					remote: "DUT already exists!"
				},
				manufacturer: {
					required: "Please enter OEM!",
					maxlength: "Manufacturer must have a max of 60 characters!"
				},
				model: {
					required: "Please enter model!",
					maxlength: "Model must have a max of 60 characters!"
				},
				description: {
					maxlength: "Description must have a max of 255 characters!"
				}
			}
		});
	}
	
	var _validateSampleForms = function()
	{
		_$newSampleForm.validate({
			rules: {
				deviceId: {
					required: true,
					maxlength: 60,
				},
				appId: {
					required: true,
					maxlength: 36,
					pattern: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
				},
				swVer: {
					required: true,
					maxlength: 60,
				},
				hwVer: {
					maxlength: 60,
				}
			},
			messages: {
				deviceId: {
					required: "Please enter device ID of the sample!",
					maxlength: "Device ID must have a max of 60 characters!"
				},
				appId: {
					required: "Please enter app ID of the sample!",
					maxlength: "App ID must have a max of 60 characters!",
					pattern: "Please enter a valid UUID! (Example: 12345678-9abc-1def-8012-000000000000)"
				},
				swVer: {
					required: "Please enter software version of the sample!",
					maxlength: "Software version must have a max of 60 characters!"
				},
				hwVer: {
					maxlength: "Hardware version must have a max of 60 characters!"
				}
			}
		});
		
		_$editSampleForm.validate({
			rules: {
				deviceId: {
					required: true,
					maxlength: 60,
				},
				appId: {
					required: true,
					maxlength: 36,
					pattern: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
				},
				swVer: {
					required: true,
					maxlength: 60,
				},
				hwVer: {
					maxlength: 60,
				}
			},
			messages: {
				deviceId: {
					required: "Please enter device ID of the sample!",
					maxlength: "Device ID must have a max of 60 characters!"
				},
				appId: {
					required: "Please enter app ID of the sample!",
					maxlength: "App ID must have a max of 60 characters!",
					pattern: "Please enter a valid UUID! (Example: 12345678-9abc-1def-8012-000000000000)"
				},
				swVer: {
					required: "Please enter software version of the sample!",
					maxlength: "Software version must have a max of 60 characters!"
				},
				hwVer: {
					maxlength: "Hardware version must have a max of 60 characters!"
				}
			}
		});
	}
	
	return {
		init: init
	}
})(duts);