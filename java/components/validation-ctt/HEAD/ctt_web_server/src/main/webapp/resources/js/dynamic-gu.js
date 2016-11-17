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
var goldenUnit = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var _titleText = "Step 3<small> Select/Edit/Create a Golden Unit (GU)</small>";
	//-------------------------------------------------
	// GOLDEN UNITS TABLE
	//-------------------------------------------------
	var _$goldenUnitsTable = jQuery('#table');
	//-------------------------------------------------
	// TOOLTIPS
	//-------------------------------------------------
	var _noSelectedGusMessage = 'You need to select at least 3 Golden Units to continue';
	//-------------------------------------------------
	// SECTION BUTTONS
	//-------------------------------------------------
	var _$editGuButton = jQuery('#editButton');
	var _$deleteButton = jQuery('#deleteButton');
	//-------------------------------------------------
	// GOLDEN UNIT CREATION MODAL
	//-------------------------------------------------
	var _$newGoldenUnitModal = jQuery('#newGuModal');
	
	var _$newGoldenUnitForm = jQuery('#newGuForm');
	var _$newGoldenUnitNameField = jQuery('#gu-name');
	var _$newGoldenUnitCategoryField = jQuery('#gu-category');
	var _$newGoldenUnitOemField = jQuery('#gu-manufacturer');
	var _$newGoldenUnitModelField = jQuery('#gu-model');
	var _$newGoldenUnitSwField = jQuery('#gu-sw-ver');
	var _$newGoldenUnitHwField = jQuery('#gu-hw-ver');
	var _$newGoldenUnitDescriptionField = jQuery('#gu-description');
	
	var _$confirmCreationButton = jQuery('#createGu');
	var _$cancelCreationButton = jQuery('#createCancel');
	//-------------------------------------------------
	// GOLDEN UNIT EDITION MODAL
	//-------------------------------------------------
	var _$editGoldenUnitModal = jQuery('#editGuModal');
	
	var _$editGoldenUnitForm = jQuery('#editGuForm');
	var _$editGoldenUnitIdField = jQuery('#edit-id');
	var _$editGoldenUnitNameField = jQuery('#edit-name');
	var _$editGoldenUnitCategoryField = jQuery('#edit-category');
	var _$editGoldenUnitOemField = jQuery('#edit-manufacturer');
	var _$editGoldenUnitModelField = jQuery('#edit-model');
	var _$editGoldenUnitSwField = jQuery('#edit-sw-ver');
	var _$editGoldenUnitHwField = jQuery('#edit-hw-ver');
	var _$editGoldenUnitDescriptionField = jQuery('#edit-description');
	
	var _$confirmEditionButton = jQuery('#editConfirm');
	var _$cancelEditionButton = jQuery('#editCancel');
	//-------------------------------------------------
	// GOLDEN UNIT DELETION MODAL
	//-------------------------------------------------
	var _$confirmDeletionButton = jQuery('#deleteConfirm');
	//-------------------------------------------------
	// NAVIGATION BUTTONS
	//-------------------------------------------------
	var _$nextStepForm = jQuery('#nextForm');
	
	var _numberOfSelectedRows = 0;
	
	var init = function()
	{
		_initStepTexts();
		_initGoldenUnitsTable();
		
		_onClickFunctions();
		_validateFormFunctions();
	};
	
	var _initStepTexts = function()
	{
		common.$title.html(_titleText);
		common.changeTooltipText(common.$nextStepButton.parent(), _noSelectedGusMessage);
	}
	
	var _initGoldenUnitsTable = function()
	{
		_$goldenUnitsTable.dataTable(
		{
			"ajax": {
				"type": "GET",
				"url": "gu/dataTable",
				"dataType": "json"
			},
			"columns": [
	            { "data": "id" },
	            { "data": "name" },
	            { "data": "created",
	            	"render": function(data)
	            	{
	            		return common.formatDateAndTime(data);
	            	}
	            },
	            { "data": "modified",
	            	"render": function(data)
	            	{
	            		return common.formatDateAndTime(data);
	            	}
	            },
	            { "data": "category" },
	            { "data": "manufacturer" },
	            { "data": "model" },
	            { "data": "swVer" },
	            { "data": "hwVer" },
	            { "data": "description" },
			],
			columnDefs: [
			             { visible: false, searchable: false, targets: 0}
			],
			pagingType: 'full_numbers',
			scrollY: common.$dynamicSection.height() - 164,
			order: [1, 'asc']
		}).on('init.dt', function()
		{
			// fill rows object
			var goldenUnitsTableRows = _$goldenUnitsTable.DataTable().rows().nodes().to$();
			
			// if project is configured, mark as selected the stored Golden Units
			if (sessionStorage.getItem("isConfigured") == "true")
			{
				var selectedGus = sessionStorage.getItem("associatedGu");
				var splitted = selectedGus.split(", ");
				
				for (var i = 0; i < splitted.length; i++)
				{
					var cell = goldenUnitsTableRows.find("td:first:contains('"+splitted[i]+"')");

					if (cell.length > 0)
					{
						cell.parent().addClass('selected');
						_numberOfSelectedRows++;
					}
				}
			}
			
			_checkNavigationButtonsStatus();
		});
	}
	
	var _checkNavigationButtonsStatus = function()
	{
		// Navigation buttons are enabled when 3 or more Golden Units are selected, or project type is Development and no Golden Units are selected
	   	if ((_numberOfSelectedRows > 2) || ((_numberOfSelectedRows == 0) && (sessionStorage.getItem("type") == "Development")))
	   	{
	   		common.enableButtons(common.$nextStepButton);
   			
   			if (sessionStorage.getItem("isConfigured") == "true")
	   		{
	   			common.enableButtons(common.$saveProjectButton);
	   		}
	   	}
	   	else
	   	{
	   		common.disableButtons(common.$nextStepButton, common.$saveProjectButton);
	   	}
	}
	
	var _onClickFunctions = function()
	{
		_onClickTableRow();
		_onClickSectionButtons();
		_onClickNavigationButtons();
	}
	
	var _onClickTableRow = function()
	{
		_$goldenUnitsTable.find('tbody').on('click', 'tr', function()
		{
		   	if ($(this).hasClass("selected"))
		   	{
		   		// if selected row was already selected, unselect it
		   		$(this).removeClass("selected");
		   		_numberOfSelectedRows--;
		   	}
		   	else
		   	{
		   		// else, select it
		   		$(this).addClass("selected");
		   		_numberOfSelectedRows++;
		   	}
		   	
		   	// Edit and Delete buttons are enabled only if one Golden Unit is selected
		   	_optionButtonsEnabled(_numberOfSelectedRows == 1);
		   	
		   	if (_numberOfSelectedRows == 1)
		   	{
		   		sessionStorage.setItem("idGu", _$goldenUnitsTable.DataTable().row('.selected').data().id);
		   	}
		   	else
		   	{
		   		sessionStorage.removeItem("idGu");
		   	}
		   	
		   	_checkNavigationButtonsStatus();
		});
	}
		
	function _optionButtonsEnabled(status)
	{
		_disableButton(_$editGuButton, !status);
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
	
	var _onClickSectionButtons = function()
	{
		_onClickCreateButton();
		_onClickEditButtons();
		_onClickDeleteButton();
	}
	
	var _onClickCreateButton = function()
	{
		_$newGoldenUnitForm.submit(function(e)
		{
			e.preventDefault();
			  
	    	$.ajax({
                url: _$newGoldenUnitForm.attr('action'),
                beforeSend: function(xhr)
                {
		            xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
                type: 'POST',
                data: _$newGoldenUnitForm.serialize(),
                success: function(goldenUnit)
                {
                	_$goldenUnitsTable.DataTable().row.add(goldenUnit).draw();
                	_$newGoldenUnitModal.modal('hide');
                	_clearFormFields(_$newGoldenUnitForm, _$newGoldenUnitNameField, _$newGoldenUnitCategoryField,
        					_$newGoldenUnitOemField, _$newGoldenUnitModelField, _$newGoldenUnitSwField, _$newGoldenUnitHwField, _$newGoldenUnitDescriptionField);
                }
            });
		})
		
	  	_$confirmCreationButton.on('click', function()
	  	{
	    	if (_$newGoldenUnitForm.valid())
	    	{
	    		_$newGoldenUnitForm.submit();
	    	}
	  	});
		
		_$cancelCreationButton.on('click', function()
		{
			_clearFormFields(_$newGoldenUnitForm, _$newGoldenUnitNameField, _$newGoldenUnitCategoryField,
					_$newGoldenUnitOemField, _$newGoldenUnitModelField, _$newGoldenUnitSwField, _$newGoldenUnitHwField, _$newGoldenUnitDescriptionField);
		});
	}
	
	var _clearFormFields = function(form, nameField, categoryField, oemField, modelField, swField, hwField, descriptionField)
	{
		nameField.val('');
		categoryField.val('1');
		oemField.val('');
		modelField.val('');
		swField.val('');
		hwField.val('');
		descriptionField.val('');
    	
    	form.clearValidation();
	}
	
	var _onClickEditButtons = function()
	{
		_$editGuButton.on('click', function()
		{
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'gu/edit',
				data :
				{
					idGu : sessionStorage.getItem("idGu")
				},
				success: function (data)
				{
					_$editGoldenUnitIdField.val(data.idGolden);
					_$editGoldenUnitNameField.val(data.name);
					_$editGoldenUnitCategoryField.val(data.category);
					_$editGoldenUnitOemField.val(data.manufacturer);
					_$editGoldenUnitModelField.val(data.model);
					_$editGoldenUnitSwField.val(data.swVer);
					_$editGoldenUnitHwField.val(data.hwVer);
					_$editGoldenUnitDescriptionField.val(data.description);
			   }
			});
		});
		
		_$editGoldenUnitForm.submit(function(e)
		{
			e.preventDefault();
			
			$.ajax({
                url: _$editGoldenUnitForm.attr('action'),
                beforeSend: function(xhr)
                {
                	xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
                type: 'POST',
                data: _$editGoldenUnitForm.serialize(),
                success: function(goldenUnit)
                {
                	var selectedRow = _$goldenUnitsTable.find('tbody').find('tr.selected');
                	goldenUnit.created = _$goldenUnitsTable.DataTable().row('.selected').data().created;
                	_$goldenUnitsTable.dataTable().fnUpdate(goldenUnit, selectedRow, undefined, false);
                	_$editGoldenUnitModal.modal('hide');
                	_clearFormFields(_$editGoldenUnitForm, _$editGoldenUnitNameField, _$editGoldenUnitCategoryField,
        					_$editGoldenUnitOemField, _$editGoldenUnitModelField, _$editGoldenUnitSwField, _$editGoldenUnitHwField, _$editGoldenUnitDescriptionField);
                }
            });
		})
		
		_$confirmEditionButton.on('click', function(e)
		{
			if (_$editGoldenUnitForm.valid())
			{
				_$editGoldenUnitForm.submit();
			}
		});
		
		_$cancelEditionButton.on('click', function()
		{
			_clearFormFields(_$editGoldenUnitForm, _$editGoldenUnitNameField, _$editGoldenUnitCategoryField,
					_$editGoldenUnitOemField, _$editGoldenUnitModelField, _$editGoldenUnitSwField, _$editGoldenUnitHwField, _$editGoldenUnitDescriptionField);
		});
	}
	
	var _onClickDeleteButton = function()
	{
		_$confirmDeletionButton.on('click', function()
		{
		    $.ajax({
				type : 'POST',
				url : 'gu/delete',
				beforeSend: function(xhr)
				{
					xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
				data :
				{
					idGu : sessionStorage.getItem("idGu")
				},
				success: function()
				{
					_$goldenUnitsTable.DataTable().row('.selected').remove().draw();
					
					sessionStorage.removeItem("idGu");
					_numberOfSelectedRows = 0;
					
					_optionButtonsEnabled(false);
					
					_checkNavigationButtonsStatus();
				}
			});
		});
	}
	
	var _onClickNavigationButtons = function()
	{
		_onClickNextButton();
		_onClickPreviousButton();
		_onClickSaveButton();
	}
	
	var _onClickNextButton = function()
	{
		_$nextStepForm.submit(function(e)
		{
			e.preventDefault(); // avoid to execute the actual submit of the form.
			 		
			_sendSelectedGoldenUnits().done(function(response)
			{
				common.$dynamicSection.fadeOut('fast', function()
        	   	{
        	   		common.$dynamicSection.html( response );
        	   	});
                
        	   	common.$dynamicSection.fadeIn('fast', function()
        	   	{
        	   		common.adjustDataTablesHeader();
                });
			});
		});
				
		common.$nextStepButton.off('click').on('click', function()
		{
			$('#idProject').val(sessionStorage.getItem('idProject'));
			
			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedGus", true);
			}
			
			var text = "";
			var textNames = "";
			
			_$goldenUnitsTable.find("tbody tr").each(function(index)
			{
				if ($(this).hasClass("selected"))
				{
					text += _$goldenUnitsTable.DataTable().row($(this)).data().id + ".";
					textNames += _$goldenUnitsTable.DataTable().row($(this)).data().name + ", ";
				}
    		});
			
			$('#gUnits').val(text);
			sessionStorage.setItem("guNames", textNames.substring(0, textNames.length - 2));
			_$nextStepForm.submit();
			
			$('#gu-breadcrumb').text(sessionStorage.getItem("guNames") !== "" ? sessionStorage.getItem("guNames") : "Not selected");
            $('#gu-breadcrumb').removeClass('hidden');
            
            common.selectNavigationElement($('#ics-nav'));
		});
	}
	
	var _sendSelectedGoldenUnits = function()
	{
		return $.ajax({
	           		type: "POST",
	           		url: _$nextStepForm.attr('action'),
		           beforeSend: function(xhr)
		           {
		        	   xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
			       },
		           data: _$nextStepForm.serialize()
		});
	}
	
	var _onClickPreviousButton = function()
	{		
		common.$previousStepButton.off('click').on('click', function()
		{		
			$.ajax({
		           type: "GET",
		           url: 'dut',
		           success: function(response)
		           {
		        	   	common.$dynamicSection.fadeOut('fast', function()
		        	   	{
		        	   		common.$dynamicSection.html( response );
		        	   	});
		        	   	
		        	   	common.$dynamicSection.fadeIn('fast');
		               
		        	   	$('#dut-breadcrumb').text('DUT');
		        	   	$('#dut-breadcrumb').addClass('hidden');
		               
		        	   	common.selectNavigationElement($('#dut-nav'));
		           }
		       });
		});
	}
	
	var _onClickSaveButton = function()
	{
		common.$saveProjectButton.off('click').on('click', function()
		{		
  			sessionStorage.setItem("modifiedGus", true);
  			
  			$('#idProject').val(sessionStorage.getItem('idProject'));
			
			var text = "";
			var textNames = "";
			
			_$goldenUnitsTable.find("tbody tr").each(function(index)
			{
				if ($(this).hasClass("selected"))
				{
					text += _$goldenUnitsTable.DataTable().row($(this)).data().id + ".";
					textNames += _$goldenUnitsTable.DataTable().row($(this)).data().name + ", ";
				}
    		});
			
			$('#gUnits').val(text);
			
			_sendSelectedGoldenUnits().done(function(response)
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
			});
  		});
	}
	
	var _validateFormFunctions = function()
	{
		_validateForm(_$newGoldenUnitForm, null, _$newGoldenUnitNameField);
		_validateForm(_$editGoldenUnitForm, _$editGoldenUnitIdField, _$editGoldenUnitNameField);
	}
	
	var _validateForm = function(form, idToValidate, nameToValidate)
	{
		form.validate(
		{
			rules: {
				name: {
					required: true,
					maxlength: 255,
					remote: {
						url: "gu/validateName",
						type: "get",
						data: {
							id: function()
							{
								if (idToValidate != null)
								{
									return idToValidate.val();
								}
								else
								{
									return 0;
								}
							},
							name: function()
							{
								return nameToValidate.val();
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
				hwVer: {
					required: true,
					maxlength: 60,
				},
				swVer: {
					required: true,
					maxlength: 60,
				},
				description: {
					required: true,
					maxlength: 255,
				}
			},
			messages: {
				name: {
					required: "Please enter GU name!",
					maxlength: "GU name must have a max of 255 characters!",
					remote: "GU already exists!"
				},
				manufacturer: {
					required: "Please enter OEM!",
					maxlength: "Manufacturer must have a max of 60 characters!"
				},
				model: {
					required: "Please enter model!",
					maxlength: "Model must have a max of 60 characters!"
				},
				hwVer: {
					required: "Please enter hardware version!",
					maxlength: "Hardware version must have a max of 60 characters!"
				},
				swVer: {
					required: "Please enter software version!",
					maxlength: "Software version must have a max of 60 characters!"
				},
				description: {
					required: "Please enter description!",
					maxlength: "Description must have a max of 255 characters!"
				}
			}
		});
	}
	
	return {
		init : init,
	};
	
})(goldenUnit);