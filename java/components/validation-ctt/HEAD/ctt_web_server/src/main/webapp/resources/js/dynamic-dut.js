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
var duts = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var title = jQuery('#title');
	var titleText = "Step 2<small> Select/Edit/Create a Device Under Test (DUT)</small>";
	
	var dutTable = jQuery('#dutTable');
	var newDutModal = jQuery('#newDutModal');
	
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	
	var init = function()
	{
		title.html(titleText);
		initDataTable();
		
		var MyRows = $('#dutTable').DataTable().rows().nodes().to$();
		
		for (var i = 0; i < MyRows.length; i++) {
			var data = dutTable.DataTable().row($(MyRows[i])).data();
			
			if((data[1])==sessionStorage.getItem("associatedDut")) {
				$(MyRows[i]).addClass('selected');
				
				sessionStorage.setItem("idDut", data[0]);
				sessionStorage.setItem("aIdDut", data[0]);
				sessionStorage.setItem("dutName", data[1]);

			   	$('#nextButton').removeClass('disabled');
			   	$('#nextButton').removeAttr("disabled", false);
				$('#deleteButton').removeClass('disabled');
				$('#deleteButton').prop("disabled", false);
				$('#editButton').removeClass('disabled');
				$('#editButton').prop("disabled", false);
			}
		}
		
		$("#dutTable tbody").on('click', 'tr', function(){
			var data = dutTable.DataTable().row(this).data();
				if($(this).hasClass('selected')) {
					$(this).removeClass('selected');
					sessionStorage.removeItem("idDut");

					disableDutButtons();
				} else {
					$('#dutTable').DataTable().$('tr.selected').removeClass('selected');
					$(this).addClass('selected');

				   	sessionStorage.setItem("idDut", data[0]);
				   	sessionStorage.setItem("dutName", data[1]);

					enableDutButtons();
				}
			});
		
		onClickFunctions();
		//resizeTable();
		validateForms();
	};
	
	var initDataTable = function()
	{
		$('#dutTable').dataTable({
			pagingType: 'full_numbers',
			scrollY: ($(window).height()/2),
			order: [0, 'asc'],
			columnDefs: [
			    { visible: false, searchable: false, targets: 0},
				{orderable: false, targets: 7}             
			]
		}).on('draw.dt', function() {
			$('.sample-link').on('click', function(e)
			{
				
				e.preventDefault();
				//e.stopPropagation();
				
				$('#sample-dut').val($('#dutTable').DataTable().row($(this).parent().parent()).data()[0]);
				
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'dut/samples',
					data : {
						//idDut : $(this).parent().parent().find('td:first').html()
						idDut: $('#dutTable').DataTable().row($(this).parent().parent()).data()[0]
					},
					success: function (samples) {

						$('#sampleBody').empty();
						$.each(samples, function(i, sample) {
							$('#sampleBody').append("<tr><td class=\"hide\">"+sample.idSample+"</td><td>"
									+sample.deviceId+"</td><td>"+sample.appId+"</td><td>"
									+sample.swVer+"</td><td>"+sample.hwVer+"</td></tr>");
						});
						
						$('#samplesDut').modal({
							show: true
						});
						
						/*$('#sampleTable').dataTable({
							paging: false,
							scrollY: 500,
							order: [0, 'asc'],
						});*/
						
						$("#sampleTable tbody tr").click(function(){
						   	$(this).addClass('selected').siblings().removeClass('selected');    
						   	var id=$(this).find('td:first').html();
						   	sessionStorage.setItem("idSample",id);

							enableSampleButtons();
						});
				   }
				});
			});
		});
	};
	
	var onClickFunctions = function()
	{
		$('#newDutForm').submit(function(e){
			e.preventDefault();
			
			$.ajax({
	    		type: 'post',
	    		url: $('#newDutForm').attr('action'),
	    		beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
	    		data: $('#newDutForm').serialize(),
	    		success: function(dut)
	    		{
	    			dutTable.DataTable().row.add([dut.idDut, dut.name, formatDateAndTime(dut.createdDate), formatDateAndTime(dut.modifiedDate), dut.manufacturer, dut.model, dut.description, "<a href=\"#\" class=\"sample-link\">Samples</a>"]).draw();
                	newDutModal.modal('hide');	
	    		}
	    	})
		});
		$('#createDut').on('click', function(e)
		{
	    	if ($('#newDutForm').valid())
	    	{
	    		$('#newDutForm').submit();
	    	}
	  	});
		
		$('.sample-link').on('click', function(e)
		{
			e.preventDefault();
			e.stopPropagation();
			
			//$('#sample-dut').val($(this).parent().parent().find('td:first').html());
			$('#sample-dut').val($('#dutTable').DataTable().row($(this).parent().parent()).data()[0]);
			
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'dut/samples',
				data : {
					idDut: $('#dutTable').DataTable().row($(this).parent().parent()).data()[0]
				},
				success: function (samples) {

					$('#sampleBody').empty();
					$.each(samples, function(i, sample) {
						$('#sampleBody').append("<tr><td class=\"hide\">"+sample.idSample+"</td><td>"
								+sample.deviceId+"</td><td>"+sample.appId+"</td><td>"
								+sample.swVer+"</td><td>"+sample.hwVer+"</td></tr>");
					});
					
					$('#samplesDut').modal({
						show: true
					});
					
					$("#sampleTable tbody tr").click(function(){
					   	$(this).addClass('selected').siblings().removeClass('selected');    
					   	var id=$(this).find('td:first').html();
					   	sessionStorage.setItem("idSample",id);

						enableSampleButtons();
					});
			   }
			});
		});
		
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

		$('#nextButton').off('click').on('click', function(e)
		{
			e.preventDefault();
			
			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedDut", true);
			}
			
			/*if (sessionStorage.getItem("associatedDut") != "N/A")
			{
				if (sessionStorage.getItem("aIdDut") == sessionStorage.getItem("idDut"))
				{
					$('#idProject').val(sessionStorage.getItem("idProject"));
					$('#idDut').val(sessionStorage.getItem("idDut"));
					sessionStorage.setItem("associatedDut",sessionStorage.getItem("dutName"));
					
					$('#nextForm').submit();
				}
				else
				{
					$('#diffDut').modal({
						show: true
					});
				}
			}
			else
			{*/
				$('#idProject').val(sessionStorage.getItem("idProject"));
				$('#idDut').val(sessionStorage.getItem("idDut"));
				sessionStorage.setItem("associatedDut",sessionStorage.getItem("dutName"));	
				
				$('#nextForm').submit();
				
				$('#dut-breadcrumb').text(sessionStorage.getItem("dutName"));
                $('#dut-breadcrumb').removeClass('hidden');
                
                if ((sessionStorage.getItem("type") != "Development") && (sessionStorage.getItem("type") != "Conformance"))
                {
                	$('#nextButton').addClass('disabled');
                	$('#nextButton').attr('disabled', true);
                }
			//}
		});
		
		$('#continueButton').on('click', function() {
			$('#idProject').val(sessionStorage.getItem("idProject"));
			$('#idDut').val(sessionStorage.getItem("idDut"));
			sessionStorage.setItem("associatedDut", sessionStorage.getItem("dutName"));	
			$('#nextForm').submit();
		});

	  	

		$('#deleteConfirm').on('click', function(){
		    
		    $.ajax({
					type : 'POST',
					url : 'dut/delete',
					beforeSend: function(xhr) {
			            xhr.setRequestHeader(header, token);
			        },
					data : {
						idDut : sessionStorage.getItem("idDut")
					},
					success: function() {						
						dutTable.DataTable().row('.selected').remove().draw();
						
						if (sessionStorage.getItem("idDut") == sessionStorage.getItem("aIdDut"))
						{
							$('.nav-stacked li').addClass('disabled');
					   		$('.nav-stacked li a').attr('disabled', true);
		            		$('#nextButton').addClass('disabled');
		            		$('#nextButton').attr('disabled', true);
		            		$('#endButton').addClass('disabled');
		            		$('#endButton').attr('disabled', true);
		            		
		            		sessionStorage.getItem("aIdDut");
		            		sessionStorage.setItem("isConfigured", false);
						}
						
						sessionStorage.removeItem("idDut");
						
						disableDutButtons();
					}
			});
		});
		
		$('#editDutForm').submit(function(e) {
			e.preventDefault();
			
			$.ajax({
                url: $('#editDutForm').attr('action'),
                type: 'POST',
                beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
                data: $('#editDutForm').serialize(),
                success: function(dut)
                {
                	var selectedRow = $('#dutTable').find('tbody').find('tr.selected');
                	$('#dutTable').dataTable().fnUpdate(dut.name, selectedRow, 1, false);
                	$('#dutTable').dataTable().fnUpdate(formatDateAndTime(dut.modifiedDate), selectedRow, 3, false);
                	$('#dutTable').dataTable().fnUpdate(dut.manufacturer, selectedRow, 4, false);
                	$('#dutTable').dataTable().fnUpdate(dut.model, selectedRow, 5, false);
                	$('#dutTable').dataTable().fnUpdate(dut.description, selectedRow, 6, false);
                	$('#editDutModal').modal('hide');	
                }
            });
		});
		
		$('#editConfirm').on('click', function(e)
		{
			if ($('#editDutForm').valid())
			{
				$('#editDutForm').submit();
			}
		});
		
		$('#editButton').on('click', function(){
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'dut/edit',
				data : {
					idDut : sessionStorage.getItem("idDut")
				},
				success: function (data) {
					
					$('#edit-id').val(data.idDut);
					$('#edit-name').val(data.name);
					$('#edit-manufacturer').val(data.manufacturer);
					$('#edit-model').val(data.model);
					$('#edit-description').val(data.description);
			   }
			});
		});
	  	
		$('#newSampleForm').submit(function(e)
		{
			e.preventDefault();
	    	
	    	$.ajax({
	    		type: 'post',
	    		url: $('#newSampleForm').attr('action'),
	    		beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
	    		data: $('#newSampleForm').serialize(),
	    		success: function(sample)
	    		{
	    			$('#sampleBody').append("<tr><td class=\"hide\">"+sample.idSample+"</td><td>"
							+sample.deviceId+"</td><td>"+sample.appId+"</td><td>"
							+sample.swVer+"</td><td>"+sample.hwVer+"</td></tr>");
	    			$('#newSampleModal').modal('hide');
	    			$('#samplesDut').modal('show');
	    		}
	    	})
		})
	  	$('#createSample').on('click', function(e){
	    	
	    	if ($('#newSampleForm').valid())
	    	{
	    		$('#newSampleForm').submit();
	    	}
	  	});
	  	
	  	$('#sampleBack').on('click', disableSampleButtons);
	  	
		$('#deleteSample').on('click', function(){
			
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
		    
		    $.ajax({
		    		type : 'POST',
					url : 'dut/samples/delete',
					beforeSend: function(xhr) {
			            xhr.setRequestHeader(header, token);
			        },
					data : {
						idSample : sessionStorage.getItem("idSample")
					},
					success: function() {
					    var MyRows = $('#sampleTable').find('tbody').find('tr');
						for (var i = 0; i < MyRows.length; i++) {
							if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idSample")) {
								$(MyRows[i]).fadeOut(400, function() {
									$(MyRows[i]).remove();
								});
							}
						}
						
						sessionStorage.removeItem("idSample");
						
						disableSampleButtons();
					}
			});
		});
		
		$('#editSampleForm').submit(function(e)
		{
			e.preventDefault();
			
			$.ajax({
	    		type: 'post',
	    		url: $('#editSampleForm').attr('action'),
	    		beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
	    		data: $('#editSampleForm').serialize(),
	    		success: function(sample)
	    		{
	    			var MyRows = $('#sampleTable').find('tbody').find('tr');
					for (var i = 0; i < MyRows.length; i++) {
						if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idSample")) {
							$(MyRows[i]).empty();
							$(MyRows[i]).append("<td class=\"hide\">"+sample.idSample+"</td><td>"
							+sample.deviceId+"</td><td>"+sample.appId+"</td><td>"
							+sample.swVer+"</td><td>"+sample.hwVer+"</td>");
						}
					}
	    			$('#editSampleModal').modal('hide');
	    			$('#samplesDut').modal('show');
	    		}
	    	})
		})
		
	    $('#editSampleConfirm').on('click', function(e)
	    {
			if ($('#editSampleForm').valid())
			{
				$('#editSampleForm').submit();
			}
		});
		
		$('#editSample').on('click', function(){
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'dut/samples/edit',
				data : {
					idSample : sessionStorage.getItem("idSample")
				},
				success: function (data) {
					
					$('#edit-device-id').val(data.deviceId);
					$('#edit-app-id').val(data.appId);
					$('#edit-sw-ver').val(data.swVer);
					$('#edit-hw-ver').val(data.hwVer);
					$('#edit-sample-id').val(data.idSample);
			   }
			});
		});
		
		$('#prevButton').off('click').on('click', function() {
			$.ajax({
	            type: "GET",
	            url: 'project',
	            success: function(response) {
	            	$('.nav-stacked li').addClass('disabled');
			   		$('.nav-stacked li a').attr('disabled', true);
	            	$('#dynamic').fadeOut('fast', function() {
	            		$("#dynamic").html( response );
	            	});
	                
	            	$('#dynamic').fadeIn('fast', function() {
	                	var table = $.fn.dataTable.fnTables(true);
	                	if ( table.length > 0 ) {
	                	    $(table).dataTable().fnAdjustColumnSizing();
	                	}
	                });
	                
	                $('#prevButton').addClass('disabled');
	                $('#prevButton').attr('disabled', true);
	                $('#nextButton').addClass('disabled');
	                $('#nextButton').attr('disabled', true);
	                $('#endButton').addClass('disabled');
	                $('#endButton').attr('disabled', true);
	                
	                $('#project-breadcrumb').text('Project');
	                $('#project-breadcrumb').addClass('hidden');
	            }
	        });
		});
		
		$('#endButton').off('click').on('click', function(e) {
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedDut", true);
  			
  			$('#idProject').val(sessionStorage.getItem("idProject"));
			$('#idDut').val(sessionStorage.getItem("idDut"));
  			
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
	}
	
	var formatDateAndTime = function(dateAndTime)
	{
		var cd = new Date(dateAndTime);
		return [cd.getFullYear(), (cd.getMonth()+1).padLeft(), cd.getDate().padLeft()].join('-')
               		+ ' ' + [cd.getHours().padLeft(), cd.getMinutes().padLeft(), cd.getSeconds().padLeft()].join(':');
	}
	
	var enableDutButtons = function(enableNext)
	{	
		$('#deleteButton').removeClass('disabled');
		$('#deleteButton').prop("disabled", false);
		$('#editButton').removeClass('disabled');
		$('#editButton').prop("disabled", false);
		$('#nextButton').removeClass('disabled');
	   	$('#nextButton').removeAttr("disabled", false);
	}
	
	var disableDutButtons = function()
	{
		$('#nextButton').addClass('disabled');
	   	$('#nextButton').prop("disabled", true);
		$('#deleteButton').addClass('disabled');
		$('#deleteButton').prop("disabled", true);
		$('#editButton').addClass('disabled');
		$('#editButton').attr("disabled", true);
	}
	
	var enableSampleButtons = function()
	{
		$('#deleteSample').removeClass('disabled');
		$('#deleteSample').prop("disabled", false);
		$('#editSample').removeClass('disabled');
		$('#editSample').prop("disabled", false);
	}
	
	var disableSampleButtons = function()
	{
		$('#deleteSample').addClass('disabled');
		$('#deleteSample').prop("disabled", true);
		$('#editSample').addClass('disabled');
		$('#editSample').prop("disabled", true);
	}
	
	var validateForms = function()
	{
		$('#newDutForm').validate({
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
		
		$('#editDutForm').validate({
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
		
		$('#newSampleForm').validate({
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
		
		$('#editSampleForm').validate({
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