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
var admin = (function()
{
	//-------------------------------------------------
	// URLS
	//-------------------------------------------------
	var newTcclCrsUrl = 'admin/crVersions';
	var newTcclTestCasesUrl = 'admin/testcases';
	var confirmNewTcclUrl = 'admin/tccl/add';
	
	var editTcclTestCasesUrl = 'admin/tccl/edit';
	
	var deleteTcclTestCasesUrl = 'admin/tccl/delete';
	
	var testCasesPackagesUrl = "admin/availablePackages";
	var deleteTestCasesPackageUrl = "admin/deleteTCP";
	
	var localAgentInstallersUrl = "admin/availableInstallers";
	var deleteLocalAgentInstallerUrl= "admin/deleteLA";
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var adminTitle = jQuery('#title');
	var titleText = "Administration Tasks";
	//-------------------------------------------------
	// TCCL TABLE AND BUTTONS
	//-------------------------------------------------
	var tcclTable = jQuery('#table');
	
	var newTcclButton = jQuery('#newTcclButton');
	var crSelector = jQuery('#tccl-certrel');
	var createContinue = jQuery("#createContinue");
	var newTcclTable = jQuery('#tcTable');
	var newTcclTableBody = jQuery('#tcBody');
	var createConfirm = jQuery("#createTccl");
	var testCasesModal = jQuery("#testCasesModal");
	
	var viewButton = jQuery('#viewButton');
	var viewTcclTableBody = jQuery("#viewTcBody");
	var viewTcclTable = jQuery('#viewTcTable');
	var viewTcclModal = jQuery("#viewTcclModal");
	
	var editButton = jQuery('#editButton');
	var editTcclTableBody = jQuery("#editTcBody");
	var editTcclTable = jQuery('#editTcTable');
	var editTcclModal = jQuery("#editTcclModal");
	var editConfirm = jQuery('#editConfirm');
	
	var deleteButton = jQuery('#deleteButton');
	var deleteConfirm = jQuery('#deleteConfirm');
	//-------------------------------------------------
	// TEST CASES PACKAGES UPLOAD PANEL
	//-------------------------------------------------
	var tcPackages = jQuery('#tcPackages');
	var testCasesPackage = jQuery('#testCasesPackage');
	var uploadPackageButton = jQuery('#uploadPackageButton');
	var tcUploadResults = jQuery('#tcUploadResults');
	var pleaseWaitDialog = jQuery('#pleaseWaitDialog');
	//-------------------------------------------------
	// LOCAL AGENT INSTALLERS UPLOAD PANEL
	//-------------------------------------------------
	var laInstallers = jQuery('#laInstallers');
	var installers = jQuery('#installers');
	var localAgentInstaller = jQuery('#localAgentInstaller');
	var uploadInstallerButton = jQuery('#uploadInstallerButton');
	var laUploadResults = jQuery('#laUploadResults');
	//-------------------------------------------------
	// ICS MANAGEMENT PANEL
	//-------------------------------------------------
	var newIcsForm = jQuery('#newIcsForm');
	var newIcsServiceGroup = jQuery('#ics-service-group');
	var addIcs = jQuery('#addIcs');
	var icsNews = jQuery('#icsSavingResults');
	//-------------------------------------------------
	// IXIT MANAGEMENT PANEL
	//-------------------------------------------------
	var newIxitForm = jQuery('#newIxitForm');
	var newIxitServiceGroup = jQuery('#ixit-service-group');
	var addIxit = jQuery('#addIxit');
	var ixitNews = jQuery('#ixitSavingResults');
	//-------------------------------------------------
	// TEST CASES MANAGEMENT PANEL
	//-------------------------------------------------
	var newTcForm = jQuery('#newTcForm');
	var newTcServiceGroup = jQuery('#tc-service-group');
	var newTcCertificationReleases = jQuery('#tc-certification-releases');
	var newTcSupportedCrs = jQuery('#supportedCrs');
	var addTc = jQuery('#addTc');
	var tcNews = jQuery('#tcSavingResults');
	//-------------------------------------------------
	// POST REQUEST TOKEN AND HEADER
	//-------------------------------------------------
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	
	var init = function()
	{
		adminTitle.text(titleText);
		initTcclTable();
		
		onChangeFunctions();
		onClickFunctions();
		onDrawFunctions();
		
		loadUploadedFiles(testCasesPackagesUrl, tcPackages);
		loadUploadedFiles(localAgentInstallersUrl, laInstallers);
		loadServiceFrameworks();
		
		loadCertificationReleases();
	};
	
	var initTcclTable = function()
	{
		tcclTable.dataTable({
			scrollY: ($(window).height()/2), //[AT4] This condition has to be improved
			columnDefs: [
				{visible: false, searchable: false, targets: 0},
			],
			order: [1, 'asc'] //[AT4] TCCL table is ordered by name (this is the same as order by Certification Release, in this case)
		});
	};
	
	var onChangeFunctions = function()
	{
		onChangeSelectedFile(testCasesPackage, uploadPackageButton);
		onChangeSelectedFile(localAgentInstaller, uploadInstallerButton);
	};
	
	var onChangeSelectedFile = function(selectedFile, buttonToChange)
	{
		selectedFile.on('change', function()
        {
            if ($(this).val())
            {
            	buttonToChange.prop('disabled', false);
            	buttonToChange.removeClass('disabled');
            }
            else
            {
            	buttonToChange.prop('disabled',true);
            	buttonToChange.addClass('disabled');
            } 
        });
	};
	
	var onClickFunctions = function()
	{
		tcclTable.find("tbody").on('click', 'tr', function()
		{
		   	onClickTcclTableRow($(this));
		});
		
		onClickTcclButtons();
		onClickUploadButtons();
		onClickSaveButtons();
	}
	
	var onClickTcclTableRow = function(clickedRow)
	{
		if (clickedRow.hasClass("selected"))
		{
			clickedRow.removeClass('selected');
			sessionStorage.removeItem("idTccl");
			
			disableTcclButtons(true);
		}
		else
		{ 
			tcclTable.DataTable().$('tr.selected').removeClass('selected');
			clickedRow.addClass('selected');
		   	
		   	sessionStorage.setItem("idTccl", tcclTable.DataTable().row(clickedRow).data()[0]);

		   	disableTcclButtons(false);
		}
	}
	
	var onClickTcclButtons = function()
	{
		newTcclButton.on('click', function()
		{
			$.ajax({
				cache: false,
				type: 'GET',
				url : newTcclCrsUrl,
				success : function (data)
				{
					crSelector.empty();
					$.each(data, function(i, cr)
					{
						if (i != data.length - 1)
						{
							crSelector.append("<option value=\""+cr.idCertrel+"\">"+cr.name+"</option>");
						}
						else
						{
							crSelector.append("<option selected value=\""+cr.idCertrel+"\">"+cr.name+"</option>");
						}
					});
				}
			});
		});
		
		createContinue.on('click', function()
		{
			$.ajax({
				cache: false,
				type : 'GET',
				url : newTcclTestCasesUrl,
				data :
				{
					idCertRel : crSelector.val()
				},
				success: function (data)
				{
					var selector = "<td width=\"10%\"><select class=\"form-control\">"
						+"<option value=\"A\">A</option>"
						+"<option value=\"B\">B</option>"
						+"<option value=\"D\">D</option>"
						+"<option value=\"N\">N</option>"
						+"<option value=\"P\">P</option></td>";

					newTcclTableBody.empty();
					
					$.each(data, function(i, tc)
					{
						newTcclTableBody.append("<tr><td class=\"hide\">"+tc["idTC"]+"</td><td width=\"20%\">"
								+tc["name"]+"</td><td width=\"60%\">"+tc["description"]+selector
								+"</td><td width=\"10%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\">");
					});
					
					testCasesModal.show();
					
					if (!newTcclTable.hasClass("dataTable"))
					{
						newTcclTable.dataTable({
							paging: false,
							"sDom": '<"top">rt<"bottom"flp><"clear">',
							scrollY: ($(window).height()/2),
							columnDefs: [        
								{ orderable: false, targets: [3, 4]},
							],
							order: [0, 'asc'],
							"bStateSave" : false
						});				
					}
					
					newTcclTable.DataTable().search('');
					
					newTcclTableBody.find('.is_checkbox').prop('checked', true);
					newTcclTableBody.find('.is_checkbox').prop('disabled', true);
			   }
			});
		});
		
		createConfirm.on('click', function()
		{
			var array = new Array();
			var certrel = new Object();
			certrel.id = crSelector.find('option:selected').val();
			certrel.name = crSelector.find('option:selected').html();
			
			var MyRows = newTcclTable.find('tbody').find('tr');
			
			for (var i = 0; i < MyRows.length; i++)
			{
				var json = new Object();
				json.id = $(MyRows[i]).find('td:eq(0)').html();
				json.type = $(MyRows[i]).find('.form-control option:selected').html();
				json.enabled = $(MyRows[i]).find('.is_checkbox').is(':checked');
				array.push(json);
			}

			$.ajax({
				type : 'POST',
				url : confirmNewTcclUrl,
				beforeSend: function(xhr)
				{
		            xhr.setRequestHeader(header, token);
		        },
				data :
				{
					certrel : certrel,
					json : array
				},
				success: function (tccl)
				{
					testCasesModal.modal('hide');
					
					Number.prototype.padLeft = function(base, chr)
					{
					   var  len = (String(base || 10).length - String(this).length)+1;
					   return len > 0? new Array(len).join(chr || '0')+this : this;
					}
				    
				    tcclTable.DataTable().row.add([tccl.idTccl, tccl.name, formatDateAndTime(tccl.createdDate), formatDateAndTime(tccl.modifiedDate), tccl.nameCertrel]).draw();
			   }
			});
		});
		
		viewButton.on('click', function()
		{
			$.ajax({
				cache: false,
				type : 'GET',
				url : editTcclTestCasesUrl,
				data :
				{
					idTccl : sessionStorage.getItem("idTccl")
				},
				success: function (data)
				{
					viewTcclTableBody.empty();
					
					$.each(data, function(i, tc)
					{
						viewTcclTableBody.append("<tr><td class=\"hide\">"+tc[0]+"</td><td width=\"20%\">"
								+tc[1]+"</td><td width=\"60%\">"+tc[2]+"</td>"
								+"<td width=\"10%\">"+tc[3]
								+"</td><td width=\"10%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\">");
						viewTcclTableBody.find('tr:last').find('.is_checkbox').prop('checked',tc[4]);
					});
					
					viewTcclModal.show();
					
					if (!viewTcclTable.hasClass("dataTable"))
					{
						viewTcclTable.dataTable({
							paging: false,
							"sDom": '<"top">rt<"bottom"flp><"clear">',
							scrollY: ($(window).height()/2),
							columnDefs: [        
								{ orderable: false, targets: [3, 4]},
							],
							order: [0, 'asc']
						});				
					}
					
					viewTcclTable.DataTable().search('');
					
					viewTcclTableBody.find('.is_checkbox').prop('disabled', true);
			   }
			});
		});
		
		editButton.on('click', function()
		{
			$.ajax({
				cache: false,
				type : 'GET',
				url : editTcclTestCasesUrl,
				data :
				{
					idTccl : sessionStorage.getItem("idTccl")
				},
				success: function (data)
				{
					var selector = "<td width=\"10%\"><select class=\"form-control\">"
						+"<option value=\"A\">A</option>"
						+"<option value=\"B\">B</option>"
						+"<option value=\"D\">D</option>"
						+"<option value=\"N\">N</option>"
						+"<option value=\"P\">P</option></td>";

					if (editTcclTable.dataTable() != null)
					{
						editTcclTable.dataTable().fnDestroy();
					}
					
					editTcclTableBody.empty();
					
					$.each(data, function(i, tc)
					{
						editTcclTableBody.append("<tr><td class=\"hide\">"+tc[0]+"</td><td width=\"20%\">"
								+tc[1]+"</td><td width=\"60%\">"+tc[2]+selector
								+"</td><td width=\"10%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\">");
						editTcclTableBody.find('tr:last').find('.form-control').val(tc[3]);
						editTcclTableBody.find('tr:last').find('.is_checkbox').prop('checked',tc[4]);
					});
					
					editTcclModal.show();
					
					if (!editTcclTable.hasClass("dataTable"))
					{
						editTcclTable.dataTable({
							paging: false,
							"sDom": '<"top">rt<"bottom"flp><"clear">',
							scrollY: ($(window).height()/2),
							columnDefs: [        
								{ orderable: false, targets: [3, 4]},
							],
							order: [0, 'asc'],
						});				
					}
					
					editTcclTable.DataTable().search('');
					editTcclTableBody.find('.is_checkbox').prop('disabled', true);
			   }
			});
		});
		
		editConfirm.on('click', function()
		{
			var array = new Array();
			
			var MyRows = editTcclTableBody.find('tr');
			
			for (var i = 0; i < MyRows.length; i++)
			{
				var json = new Object();
				json.id = $(MyRows[i]).find('td:eq(0)').html();
				json.type = $(MyRows[i]).find('.form-control option:selected').html();
				json.enabled = $(MyRows[i]).find('.is_checkbox').is(':checked');
				array.push(json);
			}

			$.ajax({
				type : 'POST',
				url : editTcclTestCasesUrl,
				beforeSend: function(xhr)
				{
		            xhr.setRequestHeader(header, token);
		        },
				data :
				{
					idTccl : sessionStorage.getItem("idTccl"),
					json : array
				},
				success: function (tccl)
				{
					editTcclModal.modal('hide');
					
					Number.prototype.padLeft = function(base, chr)
					{
					   var  len = (String(base || 10).length - String(this).length)+1;
					   return len > 0? new Array(len).join(chr || '0')+this : this;
					}
					
					var MyRows = tcclTable.find('tbody').find('tr');
					for (var i = 0; i < MyRows.length; i++)
					{
						var data = tcclTable.DataTable().row($(MyRows[i])).data();

						if (data[0] == tccl.idTccl)
						{
							tcclTable.dataTable().fnUpdate(formatDateAndTime(tccl.modifiedDate), $(MyRows[i]), 3);
							tcclTable.dataTable().fnAdjustColumnSizing();
						}
					}
			   }
			});
		});
		
		deleteConfirm.on('click', function(e)
		{   
		    $.ajax({
					type : 'POST',
					url : deleteTcclTestCasesUrl,
					beforeSend: function(xhr)
					{
			            xhr.setRequestHeader(header, token);
			        },
					data :
					{
						idTccl : sessionStorage.getItem("idTccl")
					},
					success: function ()
					{
						var MyRows = tcclTable.find('tbody').find('tr');
						for (var i = 0; i < MyRows.length; i++)
						{
							if ((tcclTable.DataTable().row($(MyRows[i])).data()[0]) == sessionStorage.getItem("idTccl"))
							{	
								var table = tcclTable.DataTable();
								table.row($(MyRows[i])).remove().draw();
							}
						}
						
						sessionStorage.removeItem("idTccl");
						
						disableTcclButtons(true);
				   }
			});
		});
	}
	
	var disableTcclButtons = function(isDisabled)
	{
		disableButton(viewButton, isDisabled);
		disableButton(editButton, isDisabled);
		disableButton(deleteButton, isDisabled);
	}
	
	var disableButton = function(buttonToDisable, isDisabled)
	{
		buttonToDisable.prop("disabled", isDisabled);
		
		if (isDisabled)
		{
			buttonToDisable.addClass('disabled');
		}
		else
		{
			buttonToDisable.removeClass('disabled');
		}
	}
	
	var formatDateAndTime = function(dateAndTime)
	{
		var cd = new Date(dateAndTime);
		return [cd.getFullYear(), (cd.getMonth()+1).padLeft(), cd.getDate().padLeft()].join('-')
               		+ ' ' + [cd.getHours().padLeft(), cd.getMinutes().padLeft(), cd.getSeconds().padLeft()].join(':');
	}
	
	var onClickUploadButtons = function() {
		onClickUploadButton('admin/uploadTCP', testCasesPackagesUrl, testCasesPackage, uploadPackageButton, tcUploadResults, tcPackages);
		onClickUploadButton('admin/uploadLA', localAgentInstallersUrl, localAgentInstaller, uploadInstallerButton, laUploadResults, laInstallers);
	}
	
	var onClickUploadButton = function(uploadUrl, uploadedUrl, file, button, results, list)
	{
		button.on('click', function(e)
		{
			e.preventDefault();
			
			pleaseWaitDialog.modal('show');
			
			var oMyForm = new FormData();
			oMyForm.append("file", file[0].files[0]);
			
			if (button.is(uploadPackageButton))
			{
				oMyForm.append("description", $('#tcDescription').val());
			}
			
			$.ajax({
				url: uploadUrl,
				beforeSend: function(xhr)
				{
		            xhr.setRequestHeader(header, token);
		        },
				data: oMyForm,
				dataType: "json",
				processData: false,
				contentType: false,
				type: 'POST',
				success: function(data)
				{
					pleaseWaitDialog.modal('hide');
					
					if (data.result == "Success")
					{
						results.append('<a class=\"list-group-item list-group-item-success\"><span class=\"badge alert-success pull-right\">Success</span>'+data.message+'</a>');
						
						loadUploadedFiles(uploadedUrl, list);
					}
					else
					{
						results.append('<a class=\"list-group-item list-group-item-danger\"><span class=\"badge alert-danger pull-right\">Fail</span>'+data.message+'</a>');
					}

					file.val('');
					button.prop('disabled',true);
                    button.addClass('disabled');
				}
			});
		});
	}
	
	var loadUploadedFiles = function(url, section)
	{
		$.ajax({
			cache: false,
			type: 'GET',
			url: url,
			success: function (data)
			{	
				section.html('');
				
				var textToAppend = '<ul>';
				
				$.each(Object.keys(data).sort(), function (i, parentField)
				{
					textToAppend += '<li><span><i class=\"glyphicon glyphicon-folder-close\"></i> ' + parentField + '</span><ul>';
					
					if (section.is(tcPackages))
					{
						textToAppend += '<li hidden><span><i class=\"glyphicon glyphicon-info-sign\"></i> ' + data[parentField][data[parentField].length -1] + '</span></li>';
						
						$.each(data[parentField], function (j, childField)
						{
							if (j < data[parentField].length - 1)
							{
								textToAppend += '<li hidden><span><i class=\"glyphicon glyphicon-file\"></i> ' + childField + '</span> <a role=\"button\" class=\"btn btn-sm btn-danger\" href=\"\"><i class=\"glyphicon glyphicon-trash\"></i></a></li>';
							}
						});
						
						textToAppend += '</ul></li>';
					}
					else
					{
						$.each(data[parentField].sort(), function (j, childField)
						{
							textToAppend += '<li hidden><span><i class=\"glyphicon glyphicon-file\"></i> ' + childField + '</span> <a role=\"button\" class=\"btn btn-sm btn-danger\" href=\"\"><i class=\"glyphicon glyphicon-trash\"></i></a></li>';
						});
						
						textToAppend += '</ul></li>';
					}
				});
				
				textToAppend += '</ul>';

				section.append(textToAppend);
				
				configureTree(section);
			}
		});
	};
	
	var onClickSaveButtons = function()
	{
		onClickSave(addIcs, newIcsForm, icsNews);
		onClickSave(addIxit, newIxitForm, ixitNews);
		onClickSave(addTc, newTcForm, tcNews);
	}
	
	var onClickSave = function(button, form, results)
	{
		button.on('click', function()
		{
			if (button.is(addTc))
			{
				var text = "";
		    	
		    	newTcCertificationReleases.find('option:selected').each(function (index)
		    	{
		    		text += $(this).val() + ".";
		    	});

		    	newTcSupportedCrs.val(text);
			}
			
			$.ajax({
	            url: form.attr('action'),
	            type: 'POST',
	            data: form.serialize(),
	            success: function(response)
	            {
	            	results.append('<a class=\"list-group-item list-group-item-success\"><span class=\"badge alert-success pull-right\">Success</span>'+response+'</a>');
	            	form.trigger("reset");
	            }
	        });
		});
	}
	
	var compareTcPackages = function(a, b)
	{
		alert(a);
	}
	
	var configureTree = function(section)
	{
	    section.find('li:has(ul)').addClass('parent_li').find(' > span').attr('title', 'Collapse this branch');

	    section.find('li.parent_li > span').on('click', function (e)
	    {
	        var children = $(this).parent('li.parent_li').find(' > ul > li');

	        if (children.is(":visible"))
	        {
	            children.hide('fast');
	            $(this).attr('title', 'Expand this branch').find(' > i').addClass('glyphicon-folder-close').removeClass('glyphicon-folder-open');
	        }
	        else
	        {
	            children.show('fast');
	            $(this).attr('title', 'Collapse this branch').find(' > i').addClass('glyphicon-folder-open').removeClass('glyphicon-folder-close');
	        }
	        e.stopPropagation();
	    });
	    
	    section.find('li.parent_li > ul > li > a').on('click', function(e)
		{
	    	e.preventDefault();

	    	if (section.is(laInstallers))
    		{
	    		deleteFile(deleteLocalAgentInstallerUrl, $(this).parent('li').find('span').text(), laUploadResults, localAgentInstallersUrl, section);
    		}
	    	else
    		{
	    		deleteFile(deleteTestCasesPackageUrl, $(this).parent('li').find('span').text(), tcUploadResults, testCasesPackagesUrl, section);
    		}
		});
	}
	
	var deleteFile = function(url, file, results, reloadUrl, section)
	{
		$.ajax({
			cache: false,
			url: url,
			beforeSend: function(xhr)
			{
	            xhr.setRequestHeader(header, token);
	        },
			data: 
			{
				fileToDelete : file
			},
			type: 'POST',
			success: function(data)
			{
				if (data.result == "Success")
				{
					results.append('<a class=\"list-group-item list-group-item-success\"><span class=\"badge alert-success pull-right\">Success</span>'+data.message+'</a>');
					
					loadUploadedFiles(reloadUrl, section);
				}
				else
				{
					results.append('<a class=\"list-group-item list-group-item-danger\"><span class=\"badge alert-danger pull-right\">Fail</span>'+data.message+'</a>');
				}
			}
		});
	}
	
	var onDrawFunctions = function()
	{
		onDraw(newTcclTable);
		onDraw(editTcclTable);
	}
	
	var onDraw = function(table)
	{
		table.on('draw.dt', function()
		{	
			$(this).find('.form-control').change(function()
			{
				var selected = $(this).find("option:selected").val();
				var state = ((selected === "A") || (selected === "B"));
				$(this).closest('tr').find('[type=checkbox]').prop('checked', state);
			});
		});
	}
	
	var loadServiceFrameworks = function()
	{
		$.ajax({
			cache: false,
			type: 'GET',
			url: 'admin/loadServiceFrameworks',
			success: function(data)
			{
				var text = "";
				$.each(data, function(i, serviceFramework)
				{
					text += "<option value=\""+serviceFramework.idService+"\">"+serviceFramework.name+"</option>";
				});
				
				newIcsServiceGroup.append(text);
				newIxitServiceGroup.append(text);
				newTcServiceGroup.append(text);
			}
		})
	}
	
	var loadCertificationReleases = function()
	{	
		newTcCertificationReleases.empty();
		$.ajax({
			cache: false,
			type: 'GET',
			url: 'admin/loadCertificationReleases',
			success: function(data)
			{
				text = "";
				$.each(data, function(i, certificationRelease)
				{
					text += "<option value=\""+certificationRelease.idCertrel+"\">"+certificationRelease.name+"</option>";
				});
				
				newTcCertificationReleases.append(text);
				newTcCertificationReleases.selectpicker();
			}
		});
	}
	
	return {
		init : init,
	};
	
})(admin);