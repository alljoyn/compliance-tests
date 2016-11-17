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
var projects = (function()
{
	var projectsWindow = jQuery(window);
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var _titleText = 'Step 1<small> Select/Edit/Create a project or view results</small>';
	//-------------------------------------------------
	// NAVIGATION BUTTONS
	//-------------------------------------------------
	var navigationList = jQuery('#navigation li');
	//-------------------------------------------------
	// PROJECTS TABLE AND BUTTONS
	//-------------------------------------------------
	var _$projectsTable = jQuery('#table');
	
	var _$createButton = jQuery('#createButton');
	var _$createConfirmButton = jQuery('#createConfirm');
	var _$createCancelButton = jQuery('#createCancel');
	
	var _$editButton = jQuery('#editButton');
	var _$editConfirmButton = jQuery('#editConfirm');
	var _$editCancelButton = jQuery('#editCancel');
	
	var _$deleteButton = jQuery('#deleteButton');
	var _$deleteConfirmButton = jQuery('#deleteConfirm');
	//-------------------------------------------------
	// PROJECT FORMS AND FIELDS
	//-------------------------------------------------
	var _$newProjectModal = jQuery('#newProjectModal');
	var _$newProjectForm = jQuery('#newProjectForm');
	var _$newProjectNameField = jQuery('#projectname');
	var _$newProjectTypeField = jQuery('#project-type');
	var _$newProjectCrField = jQuery('#project-release');
	var _$newProjectTcclField = jQuery('#project-tccl');
	var _$newProjectCarIdField = jQuery('#project-car-id');
	var _$newProjectServicesField = jQuery('#new-project-services');
	
	var _$editProjectModal = jQuery('#editProjectModal');
	var _$editProjectForm = jQuery('#editProjectForm');
	var _$editProjectIdField = jQuery('#edit_id');
	var _$editProjectNameField = jQuery('#edit_name');
	var _$editProjectTypeField = jQuery('#edit-type');
	var _$editProjectCrField = jQuery('#edit-release');
	var _$editProjectTcclField = jQuery('#edit-tccl');
	var _$editProjectCarIdField = jQuery('#edit-car-id');
	var _$editProjectServicesField = jQuery('#scroll-services');
	
	var _lastCriValue;
	
	var init = function()
	{	
		_initStepTexts();
		$('.selectpicker').selectpicker();
		sessionStorage.clear();
		
		$('#project-breadcrumb').text('Project');
        $('#project-breadcrumb').addClass('hidden');
        $('#dut-breadcrumb').text('DUT');
        $('#dut-breadcrumb').addClass('hidden');
    	$('#gu-breadcrumb').text('GU');
        $('#gu-breadcrumb').addClass('hidden');
		
		_validateForms();
		_initDataTable();
		_onClickFunctions();
		_onChangeFunctions();
	};
	
	var _initStepTexts = function()
	{
		common.$title.html(_titleText);
		common.changeTooltipText(common.$nextStepButton.parent(), 'You need to select a project to continue');
	}
	
	var _initDataTable = function()
	{
		_$projectsTable.DataTable({
			"ajax": {
				"type": "GET",
				"url": "project/dataTable",
				"dataType": "json"
			},
			"columns": [
			    { "data": null, "sDefaultContent": "" },
	            { "data": "id" },
	            { "data": "name" },
	            { "data": "modified",
	            	"render": function(data)
	            	{
	            		return common.formatDateAndTime(data);
	            	}
	            },
	            { "data": "type" },
	            { "data": "cr" },
	            { "data": "services" },
	            { "data": "configured" },
	            { "data": "results", 
	            	"render": function(data)
	            	{
	            		return data == true ? "<a href=\"#\" class=\"result-link\">Link to results</a>" : "No results";
	            	}
	            },
	            { "data": "created",
	            	"render": function(data)
	            	{
	            		return common.formatDateAndTime(data);
	            	}
	            },
	            { "data": "tccl" },
	            { "data": "dut" },
	            { "data": "gu" },
	            { "data": "cri" }
			],
			pagingType: 'full_numbers',
			scrollY: common.$dynamicSection.height() - 164,
			responsive: {
				details: {
					type: 'column',
					target: 0
				}
			},
			columnDefs: [
			    { className: 'none', searchable: false, targets: [1,9,10,11,12,13]},         
				{ className: 'control', orderable: false, targets: 0},
			],
			order: [2, 'asc'],
		});
	};
	
	var _onClickFunctions = function()
	{	
		_onClickTableRow();
		_onClickSectionButtons();
		_onClickNavigationButtons();
	};
	
	var _onClickTableRow = function()
	{
		_onClickRow();
		_onClickResultLink();
	}
	
	var _onClickRow = function()
	{
		_$projectsTable.find("tbody").on('click', 'tr', function ()
		{
			var data = _$projectsTable.DataTable().row(this).data();
			
			if ($(this).hasClass("selected"))
			{
				$(this).removeClass('selected');
				sessionStorage.removeItem("idProject");

				common.disableNavigationBar();
				_modifyButtonsState(false);
				common.disableButtons(common.$nextStepButton);
			}
			else
			{
				_$projectsTable.DataTable().$('tr.selected').removeClass('selected');
				$(this).addClass('selected');   
				
			   	sessionStorage.setItem("idProject", data.id);
			   	sessionStorage.setItem("projectName", data.name);
			   	sessionStorage.setItem("type", data.type);
			   	sessionStorage.setItem("associatedDut", data.configured == "Yes" ? data.dut : "N/A");
			   	sessionStorage.setItem("associatedGu", data.configured == "Yes" ? data.gu : "N/A");
			   	sessionStorage.setItem("isConfigured", data.configured == "Yes");

			   	if (data.configured == "Yes")
		   		{
			   		common.enableNavigationBar(data.type);
			   		
			   		if (data.type == "Conformance")
			   		{
			   			$('#gu-nav').addClass('disabled');
			   			$('#gu-nav a').attr('disabled', true);
			   		}
			   		
			   		$.ajax({
			   			url: 'dut/getId',
			   			data: {
			   				dutName: data.dut
			   			},
			   			type: 'get',
			   			success: function(dutId)
			   			{
			   				sessionStorage.setItem('idDut', dutId);
			   			}	
			   		});
		   		}
			   	else
			   	{
			   		common.disableNavigationBar();
			   	}
				_modifyButtonsState(true);
				common.enableButtons(common.$nextStepButton);
			}
		});
	}
	
	var _onClickResultLink = function()
	{
		_$projectsTable.find('tbody').on('click', '.result-link', function(e)
		{
			e.preventDefault();
			if ($(this).parent().parent().hasClass('selected'))
			{
				e.stopPropagation();
			}
			
			var id2 = _$projectsTable.DataTable().row($(this).parent().parent()).data().id;
			$('#idProject').val(id2);
			
			common.enableNavigationBar(_$projectsTable.DataTable().row($(this).parent().parent()).data().type);
			
			sessionStorage.setItem("idProject", id2);
			sessionStorage.setItem("projectName", _$projectsTable.DataTable().row($(this).parent().parent()).data().name);
			
			$.ajax({
	            type: "GET",
	            url: $('#resultForm').attr('action'),
	            data: $('#resultForm').serialize(),
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
	                
	            	common.disableButtons(common.$nextStepButton);
	            	common.enableButtons(common.$previousStepButton);
	            }
	        });
		});
	}
	
	var _onClickSectionButtons = function()
	{
		_onClickCreateButtons();
		_onClickEditButtons();
		_onClickDeleteButton();
	}
	
	var _onClickCreateButtons = function()
	{
		_$createButton.on('click', function()
		{	
			_loadProjectTypes().done(function(data)
			{				
				var typesToAppend = "";
				var referenceType = data.length > 1 ? "Conformance" : "Development";
				$.each(data, function(i, type)
				{
					typesToAppend += "<option value=\"" + type + "\">" + type + "</option>";
				});
				
				_$newProjectTypeField.append(typesToAppend);
				
				_loadCertificationReleases(referenceType).done(function(data)
				{
					var selectedCr = null;
					
					data.sort(sortComparator);
					
					var crsToAppend = "";
					$.each(data, function(i, release)
					{
						if (i == data.length - 1)
						{
							crsToAppend += "<option selected value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>";
							selectedCr = release.idCertrel;
						}
						else
						{
							crsToAppend += "<option value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>";
						}
					});
					
					_$newProjectCrField.append(crsToAppend);
					
					if (referenceType != "Development")
					{
						_loadTccls(selectedCr).done(function(data)
						{
							var tcclsToAppend = "";
							$.each(data, function(i, tccl)
							{
								tcclsToAppend += "<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>";
							});
							
							_$newProjectTcclField.append(tcclsToAppend);
						});
						
						_loadCris().done(function(data)
						{
							var crisToAppend = "";
							
							$.each(data, function(i, cri)
							{
								crisToAppend += "<option value=\""+cri+"\">"+cri+"</option>";
							});
							
							_$newProjectCarIdField.append(crisToAppend);
						});
					}
					else
					{
						_disableTcclAndCri(_$newProjectTcclField, _$newProjectCarIdField);
					}
					
					_loadServiceFrameworks().done(function(data)
					{
						var sfToAppend = "";
						$.each(data, function(i, serviceFramework)
						{
							if (serviceFramework.name == "Core Interface")
							{
								sfToAppend += "<option selected disabled value=\""+serviceFramework.idService+"\">"+serviceFramework.name+"</option>";
							}
							else
							{
								sfToAppend += "<option value=\""+serviceFramework.idService+"\">"+serviceFramework.name+"</option>";
							}
						});
						
						_$newProjectServicesField.append(sfToAppend);
						_$newProjectServicesField.selectpicker('refresh');
					});
				});
			});
		});
		
		_$newProjectForm.submit(function(e)
		{
			var sfToString = "";
	    	
			_$newProjectServicesField.find('option:selected').each(function (index)
	    	{
	    		sfToString += $(this).val() + ".";
	    	});
	    	
	    	$('#supportedServices').val(sfToString);
	    	
	    	$.ajax({
                url: _$newProjectForm.attr('action'),
                type: 'POST',
                data: _$newProjectForm.serialize(),
                success: function(project)
                {
                	_$projectsTable.DataTable().row.add(project).draw();
                	_$newProjectModal.modal('hide');
                	_clearFormFields(_$newProjectForm, _$newProjectNameField, _$newProjectTypeField, _$newProjectCrField, _$newProjectTcclField, _$newProjectCarIdField, _$newProjectServicesField);
                }
            });
	    	
	    	e.preventDefault();
		});
		
		_$createConfirmButton.on('click', function()
		{			
			if (_$newProjectForm.valid())
			{
				_$newProjectForm.submit();
				_clearFormFields(_$newProjectForm, _$newProjectNameField, _$newProjectTypeField, _$newProjectCrField, _$newProjectTcclField, _$newProjectCarIdField, _$newProjectServicesField);
			}
	  	});
		
		_$createCancelButton.on('click', function()
		{
			_clearFormFields(_$newProjectForm, _$newProjectNameField, _$newProjectTypeField, _$newProjectCrField, _$newProjectTcclField, _$newProjectCarIdField, _$newProjectServicesField);
		})
	}
	
	var _onClickEditButtons = function()
	{
		_$editButton.on('click', function()
		{			
			_getProjectInfo().done(function(data)
			{
				var projectInfo = data;
				
				_$editProjectIdField.val(projectInfo.idProject);
				_$editProjectNameField.val(projectInfo.name);
				
				_loadProjectTypes().done(function(data)
				{
					var referenceType = data.length > 1 ? projectInfo.type : "Development";
					var typesToAppend = "";
					
					$.each(data, function(i, type)
					{
						typesToAppend += "<option value=\"" + type + "\">" + type + "</option>";
					});
					
					_$editProjectTypeField.append(typesToAppend);
					_$editProjectTypeField.val(referenceType);
					
					_loadCertificationReleases(referenceType).done(function(data)
					{
						var selectedCr = null;
						
						data.sort(sortComparator);
						
						var crsToAppend = "";
						$.each(data, function(i, release)
						{
							crsToAppend += "<option value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>";
						});
						
						_$editProjectCrField.append(crsToAppend);
						_$editProjectCrField.val(projectInfo.idCertrel);
						
						if (referenceType != "Development")
						{
							_loadTccls(projectInfo.idCertrel).done(function(data)
							{
								var tcclsToAppend = "";
								$.each(data, function(i, tccl)
								{
									tcclsToAppend += "<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>";
								});
								
								_$editProjectTcclField.append(tcclsToAppend);
								_$editProjectTcclField.val(projectInfo.idTccl);
							});
							
							_loadCris().done(function(data)
							{
								var crisToAppend = "";
								
								$.each(data, function(i, cri)
								{
									crisToAppend += "<option value=\""+cri+"\">"+cri+"</option>";
								});
								
								_$editProjectCarIdField.append(crisToAppend);
								_$editProjectCarIdField.val(projectInfo.carId);
							});
						}
						else
						{
							_disableTcclAndCri(_$editProjectTcclField, _$editProjectCarIdField);
						}
						
						_loadServiceFrameworks().done(function(data)
						{
							var sfToAppend = "";
							$.each(data, function(i, serviceFramework)
							{
								if (serviceFramework.name == "Core Interface")
								{
									sfToAppend += "<option selected disabled value=\""+serviceFramework.idService+"\">"+serviceFramework.name+"</option>";
								}
								else
								{
									sfToAppend += "<option value=\""+serviceFramework.idService+"\">"+serviceFramework.name+"</option>";
								}
							});
							
							_$editProjectServicesField.append(sfToAppend);
							_$editProjectServicesField.selectpicker('val', projectInfo.supportedServices.split("."));
							_$editProjectServicesField.selectpicker('refresh');
						});
					});
				});
			});
		});
		
		_$editProjectForm.submit(function(e)
		{
			e.preventDefault();
			
			var text="";
	    	
			_$editProjectServicesField.find('option:selected').each(function (index)
	    	{
	    		text += $(this).val() + ".";
	    	});
	    	
	    	$('#edit-services').val(text);
			
			$.ajax({
                url: _$editProjectForm.attr('action'),
                type: 'POST',
                data: _$editProjectForm.serialize(),
                success: function(project)
                {
                	var selectedRow = _$projectsTable.find('tbody').find('tr.selected');
                	project.created = _$projectsTable.DataTable().row('.selected').data().created;
                	project.results = _$projectsTable.DataTable().row('.selected').data().results;
                	_$projectsTable.dataTable().fnUpdate(project, selectedRow, undefined, false);
                	sessionStorage.setItem("projectName", project.name);
                	sessionStorage.setItem("type", project.type);
                	if (sessionStorage.getItem("isConfigured") == "true")
                	{
                		common.disableNavigationBar();
                		common.enableNavigationBar(project.type);
                	}
                	_$editProjectModal.modal('hide');
    				_clearFormFields(_$editProjectForm, _$editProjectNameField, _$editProjectTypeField, _$editProjectCrField, _$editProjectTcclField, _$editProjectCarIdField, _$editProjectServicesField);
                }
            });
		});
		
		_$editConfirmButton.on('click', function(e)
		{
			if (_$editProjectForm.valid())
			{
				_$editProjectForm.submit();
			}
		});
		
		_$editCancelButton.on('click', function()
		{
			_clearFormFields(_$editProjectForm, _$editProjectNameField, _$editProjectTypeField, _$editProjectCrField, _$editProjectTcclField, _$editProjectCarIdField, _$editProjectServicesField);
		});
	}
	
	var _onClickDeleteButton = function()
	{
		_$deleteConfirmButton.on('click', function (e)
		{		
			_deleteProject().done(function()
			{
				_$projectsTable.DataTable().row('.selected').remove().draw();
				sessionStorage.removeItem("idProject");
				_modifyButtonsState(false);
				common.disableButtons(common.$nextStepButton);
			})
		});
	}
	
	var _onClickNavigationButtons = function()
	{
		common.$nextStepButton.off('click').on('click', function(e)
		{
			e.preventDefault();
			
			$.ajax({
	            type: "GET",
	            url: 'dut',
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
	                
	            	common.enableButtons(common.$previousStepButton);
	                
	                if (sessionStorage.getItem("isConfigured") == "true")
	                {
	                	common.enableButtons(common.$saveProjectButton);
	                }
	                else
	                {
	                	common.disableButtons(common.$nextStepButton);
	                }
	                
	                $('#project-breadcrumb').text(sessionStorage.getItem("projectName"));
	                $('#project-breadcrumb').removeClass('hidden');
	                
	                common.selectNavigationElement($('#dut-nav'));
	            }
	        });
		});
	}
	
	var _modifyButtonsState = function(enabled)
	{
		_modifyState(_$editButton, enabled);
		_modifyState(_$deleteButton, enabled);
	};
	
	var _modifyState = function(button, enabled)
	{
		if (enabled)
		{
			button.removeClass('disabled');
			button.removeAttr("disabled", false);
		}
		else
		{
			button.addClass('disabled');
			button.attr("disabled", true);
		}
		
		button.prop('disabled', !enabled);
		
	};
	
	var _onChangeFunctions = function()
	{
		_onChange(_$newProjectTypeField, _$newProjectCrField, _$newProjectTcclField, _$newProjectCarIdField);
		_onChange(_$editProjectTypeField, _$editProjectCrField, _$editProjectTcclField, _$editProjectCarIdField);
	};
	
	var _onChange = function(typeField, crField, tcclField, carIdField)
	{
		typeField.change(function()
		{
			var selectedType = $(this).val();
			
			// load the list of certification releases
			_loadCertificationReleases(selectedType).done(function(data)
			{
				var selectedCr = crField.val();
				
				// sort them by name
				data.sort(sortComparator);
				
				// update the selector
				crField.empty();
				$.each(data, function(i, cr)
				{
					crField.append("<option value=\""+cr.idCertrel+"\">"+cr.name+" ("+cr.description+")</option>");
				});
				
				// set the same value than before
				crField.val(selectedCr);
				
				// if the value does not exist, select the last certification release
				if (crField.val() === null)
				{
					selectedCr = crField.find(':last').val();
					crField.val(selectedCr);
				}
				
				// if it is not a Development project, load TCCLs and CRIs
				if (selectedType != "Development")
				{
					_loadTccls(selectedCr).done(function(data) 
					{
						tcclField.empty();
						_enableTcclAndCri(tcclField, carIdField);
						//_retrieveCri(carIdField, _lastCriValue);
						$.each(data, function(i, tccl)
						{
							tcclField.append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
						});
						
						_loadCris().done(function(data)
						{
							carIdField.empty();
							
							var criToAppend = "";
							$.each(data, function(i, cri)
							{
								criToAppend += "<option value=\""+cri+"\">"+cri+"</option>";
							});
							
							carIdField.append(criToAppend);
								
							if (_lastCriValue != null) //[AT4] Easy checking. Review
							{
								carIdField.val(_lastCriValue);
							}
						});
					});
				}
				else
				{
					_lastCriValue = carIdField.val();
					_disableTcclAndCri(tcclField, carIdField);
				}
			});
		});
		
		crField.change(function()
		{
			var selectedType = typeField.find("option:selected").val();
			
			if (selectedType != "Development")
			{
				var selectedCr = $(this).find("option:selected").val();
				
				$.ajax({
					cache: false,
					type: 'GET',
					url: 'project/loadTccl',
					data : {
						idCertRel : selectedCr
					},
					success: function(data)
					{
						tcclField.empty();
						$.each(data, function(i, tccl)
						{
							tcclField.append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
						});
					}
				});
			}
		});
	};
	
	var sortComparator = function(a, b)
	{
		if (a.name < b.name)
			return -1;
		if (a.name > b.name)
			return 1;
		return 0;
	};
	
	var _validateForms = function()
	{
		_validate(_$newProjectForm, null, _$newProjectNameField, _$newProjectTypeField);
		_validate(_$editProjectForm, _$editProjectIdField, _$editProjectNameField, _$editProjectTypeField);
	};
	
	var _validate = function(form, idField, nameField, typeField)
	{
		form.validate({
			rules: {
				name: {
					required: true,
					minlength: 2,
					maxlength: 255,
					remote: {
						url: "project/validateName",
						type: "get",
						data: {
							id: function()
							{
								if (idField != null)
								{
									return idField.val();
								}
								else
								{
									return 0;
								}
							},
							name: function()
							{
								return nameField.val();
							}
						}
					}
				}
			},
			messages: {
				name: {
					required: "Please enter project name!",
					maxlength: "Project name must have a max of 255 characters!",
					remote: "Project already exists!"
				}
			}
		});
	};
	
	var _loadProjectTypes = function()
	{	
		return $.ajax({
			cache: false,
			type: 'GET',
			url: 'project/loadProjectTypes'
		});
	}
	
	var _loadCertificationReleases = function(projectType)
	{
		return $.ajax({
			cache: false,
			type: 'GET',
			url: 'project/loadCertRel',
			data :
			{
				pjType : projectType
			}
		});
	}
	
	var _loadTccls = function(certificationRelease)
	{
		return $.ajax({
			cache: false,
			type: 'GET',
			url: 'project/loadTccl',
			data : {
				idCertRel : certificationRelease
			}
		});
	}
	
	var _loadCris = function()
	{
		return $.ajax({
			cache: false,
			type: 'GET',
			url: 'project/loadCris',
		});
	}
	
	var _loadServiceFrameworks = function()
	{	
		return $.ajax({
			cache: false,
			type: 'GET',
			url: 'project/loadServiceFrameworks',
		});
	}
	
	var _disableTcclAndCri = function(tcclField, carIdField)
	{
		tcclField.empty();
		tcclField.prop('disabled', true);
		carIdField.empty();
		carIdField.prop('disabled', true);
	}
	
	var _enableTcclAndCri = function(tcclField, carIdField)
	{
		tcclField.prop('disabled', false);
		carIdField.prop('disabled', false);
	}
	
	var _clearFormFields = function(form, nameField, typeField, crField, tcclField, criField, sfField)
	{
		nameField.val('');
		typeField.empty();
    	crField.empty();
    	tcclField.empty();
    	criField.empty();
    	sfField.empty();
    	
    	form.clearValidation();
    	
    	_enableTcclAndCri(tcclField, criField);
	}
	
	var _deleteProject = function()
	{
		return $.ajax({
			type : 'POST',
			url : 'project/delete',
			beforeSend: function (xhr)
			{
	            xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
	        },
			data :
			{
				idProject : sessionStorage.getItem("idProject")
			}
		});
	}
	
	var _getProjectInfo = function()
	{
		return $.ajax({
			cache: false,
			type : 'GET',
			url : 'project/edit',
			data :
			{
				idProject : sessionStorage.getItem("idProject")
			}
		});
	}
	
	return {
		init : init,
	};
})(projects);