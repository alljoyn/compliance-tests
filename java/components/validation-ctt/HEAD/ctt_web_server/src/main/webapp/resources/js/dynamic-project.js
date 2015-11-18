/*******************************************************************************
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *      Permission to use, copy, modify, and/or distribute this software for any
 *      purpose with or without fee is hereby granted, provided that the above
 *      copyright notice and this permission notice appear in all copies.
 *      
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *      WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *      MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *      ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *      WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *      ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *      OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
var projects = (function()
{
	var projectsWindow = jQuery(window);
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var title = jQuery('#title');
	var titleText = "Step 1<small> Select/Edit/Create a project or view results</small>";
	//-------------------------------------------------
	// NAVIGATION BUTTONS
	//-------------------------------------------------
	var nextButton = jQuery('#nextButton');
	var navigationList = jQuery('#navigation li');
	//-------------------------------------------------
	// PROJECTS TABLE AND BUTTONS
	//-------------------------------------------------
	var projectsTable = jQuery('#table');
	
	var createButton = jQuery('#createButton');
	var createConfirmButton = jQuery('#createConfirm');
	
	var editButton = jQuery('#editButton');
	var editConfirmButton = jQuery('#editConfirm');
	
	var deleteButton = jQuery('#deleteButton');
	var deleteConfirmButton = jQuery('#deleteConfirm');
	//-------------------------------------------------
	// PROJECT FORMS AND FIELDS
	//-------------------------------------------------
	var newProjectModal = jQuery('#newProjectModal');
	var newProjectForm = jQuery('#newProjectForm');
	var newProjectNameField = jQuery('#projectname');
	var newProjectTypeField = jQuery('#project-type');
	var newProjectCrField = jQuery('#project-release');
	var newProjectTcclField = jQuery('#project-tccl');
	var newProjectCarIdField = jQuery('#project-car-id');
	var newProjectServicesField = jQuery('#new-project-services');
	
	var editProjectModal = jQuery('#editProjectModal');
	var editProjectForm = jQuery('#editProjectForm');
	var editProjectIdField = jQuery('#edit_id');
	var editProjectNameField = jQuery('#edit_name');
	var editProjectTypeField = jQuery('#edit-type');
	var editProjectCrField = jQuery('#edit-release');
	var editProjectTcclField = jQuery('#edit-tccl');
	var editProjectCarIdField = jQuery('#edit-car-id');
	var editProjectServicesField = jQuery('#scroll-services');
	//-------------------------------------------------
	// POST REQUEST TOKEN AND HEADER
	//-------------------------------------------------
	var token = jQuery("meta[name='_csrf']").attr("content");
	var header = jQuery("meta[name='_csrf_header']").attr("content");
	
	var init = function()
	{
		title.html(titleText);
		$('.selectpicker').selectpicker();
		sessionStorage.clear();
		
		$('#project-breadcrumb').text('Project');
        $('#project-breadcrumb').addClass('hidden');
        $('#dut-breadcrumb').text('DUT');
        $('#dut-breadcrumb').addClass('hidden');
    	$('#gu-breadcrumb').text('GU');
        $('#gu-breadcrumb').addClass('hidden');
		
		validateForms();
		initDataTable();
		onClickFunctions();
		onChangeFunctions();
	};
	
	var initDataTable = function()
	{
		projectsTable.DataTable(
		{
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
	            	"render": function(data) {
	            		return formatDateAndTime(data);
	            	}
	            },
	            { "data": "type" },
	            { "data": "cr" },
	            { "data": "services" },
	            { "data": "configured" },
	            { "data": "results", 
	            	"render": function(data) {
	            		return data == true ? "<a href=\"#\" class=\"result-link\">Link to results</a>" : "No results";
	            	}
	            },
	            { "data": "created",
	            	"render": function(data) {
	            		return formatDateAndTime(data);
	            	}
	            },
	            { "data": "tccl" },
	            { "data": "dut" },
	            { "data": "gu" },
	            { "data": "cri" }
			],
			pagingType: 'full_numbers',
			scrollY: ($(window).height()/2),
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
		}).on('init.dt', function()
		{
			onClickResultLink();
			
			projectsTable.find("tbody").on('click', 'tr', function ()
			{
				var data = projectsTable.DataTable().row(this).data();
				
				if ($(this).hasClass("selected"))
				{
					$(this).removeClass('selected');
					sessionStorage.removeItem("idProject");
					$('.nav-stacked li').addClass('disabled');
			   		$('.nav-stacked li a').attr('disabled', true);
					modifyButtonsState(false);
				}
				else
				{
					projectsTable.DataTable().$('tr.selected').removeClass('selected');
					$(this).addClass('selected');   
					
				   	sessionStorage.setItem("idProject",data.id);
				   	sessionStorage.setItem("projectName",data.name);
				   	sessionStorage.setItem("type",data.type);
				   	sessionStorage.setItem("associatedDut", data.configured == "Yes" ? data.dut : "N/A");
				   	sessionStorage.setItem("associatedGu", data.configured == "Yes" ? data.gu : "N/A");
				   	sessionStorage.setItem("isConfigured", data.configured == "Yes");
	
				   	if (data.configured == "Yes")
			   		{
				   		//changeNavigationState(data[4], true);
				   		$('.nav-stacked li').removeClass('disabled');
				   		$('.nav-stacked li a').removeAttr('disabled', false);
				   		
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
				   		//changeNavigationState(null, false);
				   		$('.nav-stacked li').addClass('disabled');
				   		$('.nav-stacked li a').attr('disabled', true);
				   	}
					modifyButtonsState(true);
				}
			});
		});
	};
	
	var onClickResultLink = function()
	{
		$(projectsTable.dataTable().fnGetNodes()).find('.result-link').on('click', function(e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			var id2 = projectsTable.DataTable().row($(this).parent().parent()).data().id
			$('#idProject').val(id2);
			
			sessionStorage.setItem("idProject", id2);
			sessionStorage.setItem("projectName", projectsTable.DataTable().row($(this).parent().parent()).data().name);
			
			$.ajax({
	            type: "GET",
	            url: $('#resultForm').attr('action'),
	            data: $('#resultForm').serialize(),
	            success: function(response) {
	            	$('#dynamic').fadeOut('fast', function() {
	            		$("#dynamic").html( response );
	            	});
	                
	            	$('#dynamic').fadeIn('fast', function() {
	                	var table = $.fn.dataTable.fnTables(true);
	                	if ( table.length > 0 ) {
	                	    $(table).dataTable().fnAdjustColumnSizing();
	                	}
	                });
	                
	                $('#nextButton').addClass('disabled');
	                $('#nextButton').attr('disabled', true);
	                $('#prevButton').removeClass('disabled');
	                $('#prevButton').removeAttr('disabled', false);
	            }
	        });
		});
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
	
	var onClickFunctions = function()
	{	
		createButton.on('click', function()
		{	
			$.ajax({
				cache: false,
				type: 'GET',
				url: 'project/loadCertRel',
				data :
				{
					pjType : "Conformance"
				},
				success: function(data)
				{
					var selectedCr = null;
					
					data.sort(sortComparator);
					
					newProjectCrField.empty();
					$.each(data, function(i, release) {
						if (i == data.length -1) {
							newProjectCrField.append("<option selected value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
							selectedCr = release.idCertrel;
						} else {
							newProjectCrField.append("<option value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
						}
					});
					
					$.ajax({
						cache: false,
						type: 'GET',
						url: 'project/loadTccl',
						data : {
							idCertRel : selectedCr
						},
						success: function(data) {
							newProjectTcclField.empty();
							$.each(data, function(i, tccl) {
								newProjectTcclField.append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
							});
						}
					});
					
					loadServiceFrameworks(newProjectServicesField, null);
				}
			});
		});
		
		newProjectForm.submit(function(e)
		{
			var text="";
	    	
	    	newProjectServicesField.find('option:selected').each(function (index)
	    	{
	    		text += $(this).val() + ".";
	    	});
	    	
	    	$('#supportedServices').val(text);
	    	//newProjectForm.submit();
	    	
	    	$.ajax({
                url: newProjectForm.attr('action'),
                type: 'POST',
                data: newProjectForm.serialize(),
                success: function(project)
                {
                	projectsTable.DataTable().row.add(project).draw();
                	newProjectNameField.val('');
                	newProjectCrField.empty();
                	newProjectTcclField.empty();
                	newProjectCarIdField.val('');
                	newProjectServicesField.empty();
                	newProjectModal.modal('hide');	
                }
            });
	    	
	    	e.preventDefault();
		});
		
		createConfirmButton.on('click', function()
		{			
			if (newProjectForm.valid())
			{
				newProjectForm.submit();
			}
	  	});

		deleteConfirmButton.on('click', function (e)
		{
		    $.ajax({
					type : 'POST',
					url : 'project/delete',
					beforeSend: function (xhr) {
			            xhr.setRequestHeader(header, token);
			        },
					data : {
						idProject : sessionStorage.getItem("idProject")
					},
					success: function ()
					{						
						projectsTable.DataTable().row('.selected').remove().draw();
						sessionStorage.removeItem("idProject");
						modifyButtonsState(false);
				   }
			});
		});
		
		editButton.on('click', function()
		{
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'project/edit',
				data : {
					idProject : sessionStorage.getItem("idProject")
				},
				success: function (data) {
					
					editProjectIdField.val(data.idProject);
					editProjectNameField.val(data.name);
					editProjectTypeField.val(data.type);
					editProjectCarIdField.val(data.carId);
					
					$.ajax({
						cache: false,
						type: 'GET',
						url: 'project/loadCertRel',
						data : {
							pjType : data.type
						},
						success: function(releases) {
							releases.sort(sortComparator);
							
							editProjectCrField.empty();
							$.each(releases, function(i, release) {
								editProjectCrField.append("<option value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
							});
							
							editProjectCrField.val(data.idCertrel);
							
							if (data.type != "Development")
							{
								$.ajax({
									cache: false,
									type: 'GET',
									url: 'project/loadTccl',
									data : {
										idCertRel : data.idCertrel
									},
									success: function(tccls) {
										editProjectTcclField.empty();
										editProjectTcclField.prop('disabled', false);
										editProjectCarIdField.empty();
										editProjectCarIdField.prop('disabled', false);
										$.each(tccls, function(i, tccl) {
											editProjectTcclField.append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
										});
									}
								});
							}
							else
							{
								editProjectTcclField.empty();
								editProjectTcclField.prop('disabled', true);
								editProjectCarIdField.empty();
								editProjectCarIdField.prop('disabled', true);
							}
						}
					});
					
					editProjectTcclField.val(data.idTccl);

					var services = data.supportedServices.split(".");
					loadServiceFrameworks(editProjectServicesField, services);
					//editProjectServicesField.selectpicker('val', services);
			   }
			});
		});
		
		editProjectForm.submit(function(e)
		{
			e.preventDefault();
			
			var text="";
	    	
	    	editProjectServicesField.find('option:selected').each(function (index)
	    	{
	    		text += $(this).val() + ".";
	    	});
	    	
	    	$('#edit-services').val(text);
			
			$.ajax({
                url: editProjectForm.attr('action'),
                type: 'POST',
                data: editProjectForm.serialize(),
                success: function(project)
                {
                	var selectedRow = projectsTable.find('tbody').find('tr.selected');
                	project.created = projectsTable.DataTable().row('.selected').data().created;
                	projectsTable.dataTable().fnUpdate(project, selectedRow, undefined, false);
                	editProjectModal.modal('hide');	
                }
            });
		});
		
		editConfirmButton.on('click', function(e)
		{
			if (editProjectForm.valid())
			{
				editProjectForm.submit();
			}
		});
		
		$('#nextButton').off('click').on('click', function(e)
		{
			e.preventDefault();
			
			$.ajax({
	            type: "GET",
	            url: 'dut',
	            success: function(response) {
	            	$('#dynamic').fadeOut('fast', function() {
	            		$("#dynamic").html( response );
	            	});
	                
	            	$('#dynamic').fadeIn('fast', function() {
	                	var table = $.fn.dataTable.fnTables(true);
	                	if ( table.length > 0 ) {
	                	    $(table).dataTable().fnAdjustColumnSizing();
	                	}
	                });
	                
	                $('#prevButton').removeClass('disabled');
	                $('#prevButton').removeAttr('disabled', false);
	                
	                if (sessionStorage.getItem("isConfigured") == "true")
	                {
	                	$('#endButton').removeClass('disabled');
		                $('#endButton').removeAttr('disabled', false);
	                }
	                else
	                {
	                	$('#nextButton').addClass('disabled');
	                	$('#nextButton').attr('disabled', true);
	                }
	                
	                $('#project-breadcrumb').text(sessionStorage.getItem("projectName"));
	                $('#project-breadcrumb').removeClass('hidden');
	            }
	        });
		});
	};
	
	var loadServiceFrameworks = function(field, values)
	{	
		$.ajax({
			cache: false,
			type: 'GET',
			url: 'project/loadServiceFrameworks',
			success: function(data)
			{
				field.empty();
				$.each(data, function(i, serviceFramework)
				{
					if (serviceFramework.name == "Core Interface")
					{
						field.append("<option selected disabled value=\""+serviceFramework.idService+"\">"+serviceFramework.name+"</option>");
					}
					else
					{
						field.append("<option value=\""+serviceFramework.idService+"\">"+serviceFramework.name+"</option>");
					}
				});
				
				if (field.is(editProjectServicesField))
				{
					field.selectpicker('val', values);
				}
				field.selectpicker('refresh');
			}
		});
	}
	
	var modifyButtonsState = function(enabled)
	{
		modifyState(editButton, enabled);
		modifyState(deleteButton, enabled);
		modifyState(nextButton, enabled);
	};
	
	var modifyState = function(button, enabled)
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
	
	var onChangeFunctions = function()
	{
		onChange(newProjectTypeField, newProjectCrField, newProjectTcclField, newProjectCarIdField);
		onChange(editProjectTypeField, editProjectCrField, editProjectTcclField, editProjectCarIdField);
	};
	
	var onChange = function(typeField, crField, tcclField, carIdField)
	{
		typeField.change(function() {
			var selectedType = $(this).find("option:selected").val();
			
			$.ajax({
				cache: false,
				type: 'GET',
				url: 'project/loadCertRel',
				data : {
					pjType : selectedType
				},
				success: function(data) {
					var selectedCr = null;
					
					data.sort(sortComparator);
					
					crField.empty();
					$.each(data, function(i, cr) {
						if (i == data.length - 1) {
							crField.append("<option selected value=\""+cr.idCertrel+"\">"+cr.name+": ("+cr.description+")</option>");
							selectedCr = cr.idCertrel;
						} else {
							crField.append("<option value=\""+cr.idCertrel+"\">"+cr.name+" ("+cr.description+")</option>");
						}
					});
					
					if (selectedType != "Development")
					{
						$.ajax({
							cache: false,
							type: 'GET',
							url: 'project/loadTccl',
							data : {
								idCertRel : selectedCr
							},
							success: function(data) {
								tcclField.empty();
								tcclField.prop('disabled', false);
								carIdField.val("");
								carIdField.prop('disabled', false);
								$.each(data, function(i, tccl) {
									tcclField.append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
								});
							}
						});
					} else {
						tcclField.empty();
						tcclField.prop('disabled', true);
						carIdField.val("");
						carIdField.prop('disabled', true);
					}
				}
			});
		});
		
		crField.change(function() {
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
					success: function(data) {
						tcclField.empty();
						$.each(data, function(i, tccl) {
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
	
	var validateForms = function()
	{
		validate(newProjectForm, null, newProjectNameField, newProjectTypeField);
		validate(editProjectForm, editProjectIdField, editProjectNameField, editProjectTypeField);
	};
	
	var validate = function(form, idField, nameField, typeField)
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
				},
				carId: {
					required: {
						depends: function(element)
						{
				          return typeField.val() != "Development";
						}
			        },
					maxlength: 60,
				}
			},
			messages: {
				name: {
					required: "Please enter project name!",
					maxlength: "Project name must have a max of 255 characters!",
					remote: "Project already exists!"
				},
				carId: {
					required: "Please enter CRI!",
					maxlength: "CRI must have a max of 60 characters!"
				}
			}
		});
	};
	
	return {
		init : init,
	};
})(projects);