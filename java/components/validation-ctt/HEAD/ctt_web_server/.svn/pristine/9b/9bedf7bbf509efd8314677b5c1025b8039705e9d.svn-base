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
	var titleText = "Step 3<small> Select/Edit/Create a Golden Unit (GU)</small>";
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
	
	var endButton = jQuery('#endButton');
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
		stepTitle.html(titleText);
	}
	
	var initGoldenUnitsTable = function()
	{
		goldenUnitsTable.dataTable(
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
	            	"render": function(data) {
	            		return formatDateAndTime(data);
	            	}
	            },
	            { "data": "modified",
	            	"render": function(data) {
	            		return formatDateAndTime(data);
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
			//autoWidth: false,
			pagingType: 'full_numbers',
			scrollY: ($(window).height()/2),
			order: [1, 'asc']
		}).on('init.dt', function()
		{
			if (sessionStorage.getItem("isConfigured") == "true")
			{
				var selectedGus = sessionStorage.getItem("associatedGu");
				var splitted = selectedGus.split(", ");

				var MyRows = goldenUnitsTable.DataTable().rows().nodes().to$();
				
				for (var i = 0; i < splitted.length; i++)
				{
					var cell = MyRows.find("td:first:contains('"+splitted[i]+"')");

					if (cell.length > 0)
					{
						cell.parent().addClass('selected');
						numberOfSelectedRows++;
					}
				}
				
				if ((numberOfSelectedRows > 2) || ((numberOfSelectedRows == 0) && (sessionStorage.getItem("type") == "Development")))
				{
					disableButton(nextButton, false);
				}
				else
				{
					disableButton(nextButton, true);
				}
			}
			
			onClickTableRow();
		});
	}
	
	var onClickFunctions = function()
	{
		//onClickTableRow();
		onClickTableButtons();
	}
	
	var onClickTableRow = function()
	{
		goldenUnitsTable.find("tbody").on('click', 'tr', function()
		{
			var data = goldenUnitsTable.DataTable().row(this).data();
			
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
		   		disableButton(endButton, false);
		   	}
		   	else if (numberOfSelectedRows == 2)
		   	{
				optionButtonsEnabled(false);
				disableButton(nextButton, true);
				disableButton(endButton, true);
			   	sessionStorage.removeItem("idGu");
		   	}
		   	else if (numberOfSelectedRows == 1)
		   	{
		   		optionButtonsEnabled(true);
				
				if (sessionStorage.getItem("type") == "Development")
				{
					disableButton(nextButton, true);
					disableButton(endButton, true);
				}
				
			   	sessionStorage.setItem("idGu", data.id);
		   	}
		   	else if (numberOfSelectedRows == 0)
		   	{
		   		if (sessionStorage.getItem("type") == "Development")
		   		{
		   			disableButton(nextButton, false);
		   			disableButton(endButton, false);
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
		
		newGoldenUnitForm.submit(function(e)
		{
			e.preventDefault();
			  
	    	$.ajax({
                url: newGoldenUnitForm.attr('action'),
                beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
                type: 'POST',
                data: newGoldenUnitForm.serialize(),
                success: function(goldenUnit)
                {
                	goldenUnitsTable.DataTable().row.add(goldenUnit).draw();
                	$('#newGuModal').modal('hide');	
                }
            });
		})
	  	confirmCreationButton.on('click', function(e)
	  	{
	    	if (newGoldenUnitForm.valid())
	    	{
	    		newGoldenUnitForm.submit();
	    	}
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
		
		editGoldenUnitForm.submit(function(e)
		{
			e.preventDefault();
			
			$.ajax({
                url: editGoldenUnitForm.attr('action'),
                beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
                type: 'POST',
                data: editGoldenUnitForm.serialize(),
                success: function(goldenUnit)
                {
                	var selectedRow = goldenUnitsTable.find('tbody').find('tr.selected');
                	goldenUnit.created = goldenUnitsTable.DataTable().row('.selected').data().created;
                	goldenUnitsTable.dataTable().fnUpdate(goldenUnit, selectedRow, undefined, false);
                	$('#editGuModal').modal('hide');		
                }
            });
		})
		
		confirmEditionButton.on('click', function(e)
		{
			if (editGoldenUnitForm.valid())
			{
				editGoldenUnitForm.submit();
			}
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
					goldenUnitsTable.DataTable().row('.selected').remove().draw();
					
					sessionStorage.removeItem("idGu");
					numberOfSelectedRows = 0;
					
					disableButton(nextButton, true);
					optionButtonsEnabled(false);
				}
			});
		});
	}
	
	$('#nextForm').submit(function(e)
	{
	    $.ajax({
	           type: "POST",
	           url: $('#nextForm').attr('action'),
	           beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
	           data: $("#nextForm").serialize(),
	           success: function(response)
	           {
	        	   $('#dynamic').fadeOut('fast', function() {
	            		$("#dynamic").html( response );
	            	});
	                
	        	   $('#dynamic').fadeIn('fast', function() {
	                	var table = $.fn.dataTable.fnTables(true);
	                	if ( table.length > 0 ) {
	                	    $(table).dataTable().fnAdjustColumnSizing();
	                	}
	                });
	           }
	         });

	    e.preventDefault(); // avoid to execute the actual submit of the form.
	})
	
	var onClickNextButtons = function()
	{
		nextButton.off('click').on('click', function()
		{
			$('#idProject').val(sessionStorage.getItem('idProject'));
			
			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedGus", true);
			}
			
			var text = "";
			var textNames = "";
			
			goldenUnitsTable.find("tbody tr").each(function(index)
			{
				if ($(this).hasClass("selected"))
				{
					text += goldenUnitsTable.DataTable().row($(this)).data().id + ".";
					textNames += goldenUnitsTable.DataTable().row($(this)).data().name + ", ";
				}
    		});
			
			$('#gUnits').val(text);
			sessionStorage.setItem("guNames", textNames.substring(0, textNames.length - 2));
			$('#nextForm').submit();
			
			$('#gu-breadcrumb').text(sessionStorage.getItem("guNames"));
            $('#gu-breadcrumb').removeClass('hidden');
		});
		
		continueButton.on('click', function()
		{
			$('#idProject').val(sessionStorage.getItem('idProject'));
			$('#idGolden').val(sessionStorage.getItem('idGu'));
			sessionStorage.setItem("associatedGu", sessionStorage.getItem("idGu"));
			$('#nextForm').submit();
		});
		
		$('#endButton').off('click').on('click', function(e) {
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedGus", true);
  			
  			$('#idProject').val(sessionStorage.getItem('idProject'));
			
			var text = "";
			var textNames = "";
			
			goldenUnitsTable.find("tbody tr").each(function(index)
			{
				if ($(this).hasClass("selected"))
				{
					text += goldenUnitsTable.DataTable().row($(this)).data().id + ".";
					textNames += goldenUnitsTable.DataTable().row($(this)).data().name + ", ";
				}
    		});
			
			$('#gUnits').val(text);
  			
  			$.ajax({
 	           type: "POST",
 	           url: $('#nextForm').attr('action'),
 	           beforeSend: function(xhr) {
 		            xhr.setRequestHeader(header, token);
 		        },
 	           data: $("#nextForm").serialize(),
 	           success: function(response)
 	           {
 	        	   	var data = {};
	 	 			for (var j = 0; j < sessionStorage.length; j++) {
	 	 				var key = sessionStorage.key(j);
	 	 				data[key] = sessionStorage.getItem(key);
	 	 			}
 	 			
	 	 			var token = $("meta[name='_csrf']").attr("content");
	 	 			var header = $("meta[name='_csrf_header']").attr("content");
	 	 			
	 	 			$.ajax({
	 	 				   	url: 'end/save',
	 	 				   	type: 'POST',
	 	 				   	beforeSend: function(xhr)
	 	 				   	{
	 	 			        	xhr.setRequestHeader(header, token);
	 	 			        },
	 	 				   	data:
	 	 					{
	 	 					   	data : data	
	 	 					},
	 	 			        success: function(response)
	 	 			        {
	 	 	                	$('#dynamic').fadeOut('fast', function()
	 	 	                	{
	 	 	                		$("#dynamic").html( response );
	 	 	                	});
	 	 	                    $('#dynamic').fadeIn('fast');
	 	 	                    
	 	 	                    $('.nav-stacked li').addClass('disabled');
						   		$('.nav-stacked li a').attr('disabled', true);
						   		$('#prevButton').addClass('disabled');
			            		$('#prevButton').attr('disabled', true);
			            		$('#nextButton').addClass('disabled');
			            		$('#nextButton').attr('disabled', true);
			            		$('#endButton').addClass('disabled');
			            		$('#endButton').attr('disabled', true);
	 	 			        }
	 	 			});	
 	           }
 	         });	
  		});
		
		$('#prevButton').off('click').on('click', function(e) {
			e.preventDefault();
			
			$.ajax({
		           type: "GET",
		           url: 'dut',
		           success: function(response) {
		           	$('#dynamic').fadeOut('fast', function() {
		           		$("#dynamic").html( response );
		           	});
		               $('#dynamic').fadeIn('fast');
		               
		               $('#dut-breadcrumb').text('DUT');
		               $('#dut-breadcrumb').addClass('hidden');
		           }
		       });
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