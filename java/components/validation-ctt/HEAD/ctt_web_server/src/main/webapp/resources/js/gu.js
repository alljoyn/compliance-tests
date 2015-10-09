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
var goldenUnit = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var stepTitle = jQuery('#title');
	var titleText = "STEP 3: SELECT/EDIT/CREATE A GOLDEN UNIT (GU)";
	//-------------------------------------------------
	// HEADER TEXT
	//-------------------------------------------------
	var selectedProject = jQuery('#selectedProject');
	//-------------------------------------------------
	// GOLDEN UNITS TABLE AND BUTTONS
	//-------------------------------------------------
	var goldenUnitsTable = jQuery('#table');
	
	var noSelectedGusMessage = jQuery('#guSelect');
	
	var confirmCreationButton = jQuery('#createGu');
	
	var editButton = jQuery('#editButton');
	var confirmEditionButton = jQuery('#editConfirm');
	
	var deleteButton = jQuery('#deleteButton');
	var confirmDeletionButton = jQuery('#deleteConfirm');
	
	var nextButton = jQuery('#nextButton');
	var continueButton = jQuery('#continueButton');
	//-------------------------------------------------
	// GOLDEN UNIT CREATION AND EDITION FORM
	//-------------------------------------------------
	var newGoldenUnitForm = jQuery('#newGuForm');
	var newGoldenUnitNameField = jQuery('#gu-name');
	
	var editGoldenUnitForm = jQuery('#editGuForm');
	var editGoldenUnitIdField = jQuery('#edit-id');
	var editGoldenUnitNameField = jQuery('#edit-name');
	//-------------------------------------------------
	// POST REQUEST TOKEN AND HEADER
	//-------------------------------------------------
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	//-------------------------------------------------
	// LOGOUT FORM
	//-------------------------------------------------
	var logoutForm = jQuery('#logoutForm');
	
	var numberOfSelectedRows = 0;
	
	var init = function()
	{
		initStepTexts();
		initGoldenUnitsTable();
		onClickFunctions();
		validateFormFunctions();
		
		if (sessionStorage.getItem("type") == "Development")
		{
			disableButton(nextButton, false);
		}
	};
	
	var initStepTexts = function()
	{
		stepTitle.text(titleText);
		
		selectedProject.append("Project: " + sessionStorage.getItem("projectName") + 
				" / DUT: "+sessionStorage.getItem("dutName"));
	}
	
	var initGoldenUnitsTable = function()
	{
		goldenUnitsTable.removeClass('hide');
		goldenUnitsTable.dataTable(
		{
			autoWidth: false,
			pagingType: 'full_numbers',
			scrollY: ($(window).height()/2),
			order: [0, 'asc']
		});
	}
	
	var onClickFunctions = function()
	{
		onClickTableRow();
		onClickTableButtons();
	}
	
	var onClickTableRow = function()
	{
		goldenUnitsTable.find("tbody tr").click(function()
		{			
		   	if ($(this).hasClass("selected"))
		   	{
		   		$(this).removeClass("selected");
		   		numberOfSelectedRows--;
		   	}
		   	else
		   	{
		   		$(this).addClass("selected");
		   		numberOfSelectedRows++;
		   	}

		   	if (numberOfSelectedRows >= 3)
		   	{
		   		disableButton(nextButton, false);
		   	}
		   	else if (numberOfSelectedRows == 2)
		   	{
				optionButtonsEnabled(false);
				disableButton(nextButton, true);
			   	sessionStorage.removeItem("idGu");
		   	}
		   	else if (numberOfSelectedRows == 1)
		   	{
		   		optionButtonsEnabled(true);
				
				if (sessionStorage.getItem("type") == "Development")
				{
					disableButton(nextButton, true);
				}
				
			   	sessionStorage.setItem("idGu", $(this).find('td:first').html());
		   	}
		   	else if (numberOfSelectedRows == 0)
		   	{
		   		if (sessionStorage.getItem("type") == "Development")
		   		{
		   			disableButton(nextButton, false);
		   		}
		   		
		   		optionButtonsEnabled(false);
		   		sessionStorage.removeItem("idGu");
		   	}
		});
	}
	
	var onClickTableButtons = function()
	{
		onClickCreateButton();
		onClickEditButtons();
		onClickDeleteButton();
		onClickNextButtons();
	}
	
	var onClickCreateButton = function()
	{
	  	confirmCreationButton.on('click', function(e)
	  	{
	    	e.preventDefault();
	  
	    	newGoldenUnitForm.submit();
	  	});
	}
	
	var onClickEditButtons = function()
	{
		editButton.on('click', function()
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
					$('#edit-id').val(data.idGolden);
					$('#edit-name').val(data.name);
					$('#edit-category').val(data.category);
					$('#edit-manufacturer').val(data.manufacturer);
					$('#edit-model').val(data.model);
					$('#edit-sw-ver').val(data.swVer);
					$('#edit-hw-ver').val(data.hwVer);
					$('#edit-description').val(data.description);
			   }
			});
		});
		
		confirmEditionButton.on('click', function(e)
		{
			e.preventDefault();
			
			editGoldenUnitForm.submit();
		});
	}
	
	var onClickDeleteButton = function()
	{
		confirmDeletionButton.on('click', function()
		{
		    $.ajax({
				type : 'POST',
				url : 'gu/delete',
				beforeSend: function(xhr)
				{
		            xhr.setRequestHeader(header, token);
		        },
				data :
				{
					idGu : sessionStorage.getItem("idGu")
				},
				success: function()
				{
				    var MyRows = goldenUnitsTable.find('tbody').find('tr');
					
				    for (var i = 0; i < MyRows.length; i++)
				    {
						if (($(MyRows[i]).find('td:eq(0)').html()) == sessionStorage.getItem("idGu"))
						{
							$(MyRows[i]).click();
							var table = goldenUnitsTable.DataTable();
							table.row($(MyRows[i])).remove().draw();
						}
					}
					
					sessionStorage.removeItem("idGu");
					
					nextButtonEnabled(false);
					optionButtonsEnabled(false);
				}
			});
		});
	}
	
	var onClickNextButtons = function()
	{
		nextButton.on('click', function()
		{
			$('#idProject').val(sessionStorage.getItem('idProject'));
			
			var text = "";
			var textNames = "";
			
			goldenUnitsTable.find("tbody tr").each(function(index)
			{
				if ($(this).hasClass("selected"))
				{
					text += $(this).find('td:first').html() + ".";
					textNames += $(this).find('td:eq(1)').html() + ", ";
				}
    		});
			
			$('#gUnits').val(text);
			sessionStorage.setItem("guNames", textNames.substring(0, textNames.length - 2));
			$('#nextForm').submit();	
		});
		
		continueButton.on('click', function()
		{
			$('#idProject').val(sessionStorage.getItem('idProject'));
			$('#idGolden').val(sessionStorage.getItem('idGu'));
			sessionStorage.setItem("associatedGu", sessionStorage.getItem("idGu"));
			$('#nextForm').submit();
		});
	}
	
	var validateFormFunctions = function()
	{
		validateForm(newGoldenUnitForm, null, newGoldenUnitNameField);
		validateForm(editGoldenUnitForm, editGoldenUnitIdField, editGoldenUnitNameField);
	}
	
	var validateForm = function(form, idToValidate, nameToValidate)
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
							id: function() {
								if (idToValidate != null)
								{
									return idToValidate.val();
								}
								else
								{
									return 0;
								}
							},
							name: function() {
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
					required: "Please enter manufacturer!",
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
	
	var disableButton = function(button, isDisabled)
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
		
		if (button.is(nextButton))
		{
			noSelectedGusMessage.prop("hidden", !isDisabled);
		}
	}
	
	function optionButtonsEnabled(status)
	{
		disableButton(editButton, !status);
		disableButton(deleteButton, !status);
	}
	
	var logoutFormSubmit = function()
	{
		logoutForm.submit();
	};
	
	return {
		init : init,
		logout : logoutFormSubmit
	};
	
})(goldenUnit);