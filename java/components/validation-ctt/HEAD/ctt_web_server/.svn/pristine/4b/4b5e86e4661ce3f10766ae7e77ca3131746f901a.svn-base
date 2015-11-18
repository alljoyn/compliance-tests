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
var ics = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var title = jQuery('#title');
	//-------------------------------------------------
	// POST REQUEST TOKEN AND HEADER
	//-------------------------------------------------
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	
	var init = function()
	{
		title.html("Step 4<small> Configure your ICS</small>");
		
		$('#nextButton').addClass('disabled');
		$('#nextButton').attr('disabled', true);
		
		if(sessionStorage.getItem("type")=="Conformance") {
			title.html(function(i, oldText) {
				return oldText === 'Step 4<small> Configure your ICS</small>' ? 'Step 3<small> Configure your ICS</small>' : oldText;
			});
			$('#prevButton').attr("href","dut");
		}
		
		if (sessionStorage.getItem("isConfigured") == "true")
		{
			$('#nextButton').removeClass('disabled');
			$('#nextButton').removeAttr("disabled", false);
			$('#scrNeed').prop("hidden", true);
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
			"sDom": '<"top">rt<"bottom"flp><"clear">',
			scrollY: ($(window).height()*0.52),
			columnDefs: [       
			    { width: "3%", targets: 0},
			    { width: "30%", targets: 1},
			    { width: "57%", targets: 2},
				{ width: "10%", orderable: false, targets: 3}
			],
			order: [0, 'asc']
		});
		
		var MyRows = $('.table').find('tbody').find('tr');
		for (var i = 0; i < MyRows.length; i++)
		{
			var id = $(MyRows[i]).find('td:eq(1)').html();
			
			if (sessionStorage.hasOwnProperty(id))
			{
				$(MyRows[i]).find('.form-control').val(sessionStorage.getItem(id));
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
					"sDom": '<"top">rt<"bottom"flp><"clear">',
					scrollY: ($(window).height()*0.52),
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
		$('tbody').find('tr').dblclick(function()
		{
			var val = $(this).find('.form-control').val();
			$(this).find('.form-control').val((val == "false").toString());
			$('#nextButton').addClass('disabled');
    		$('#nextButton').attr("disabled", true);
    		
    		if (sessionStorage.getItem("isConfigured") == "true")
    		{
    			$('#endButton').addClass('disabled');
        		$('#endButton').attr("disabled", true);
    		}
    		
    		$('#scrNeed').prop("hidden", false);
    		$('#fixErrors').prop("hidden",true);
		});
		
		$('#prevButton').off('click').on('click', function(e)
		{
			e.preventDefault();
			
			$('#prevIdProject').val(sessionStorage.getItem('idProject'));
			
			$.ajax({
				type: 'GET',
				url: $('#prevForm').attr('action'),
				data: $('#prevForm').serialize(),
				success: function(response)
				{
					$('#dynamic').fadeOut('fast', function()
					{
	            		$("#dynamic").html(response);
	            	});
	                $('#dynamic').fadeIn('fast', function()
					{
	                	if (sessionStorage.getItem("isConfigured") == "true")
	            		{
	            			$('#endButton').removeClass('disabled');
	                		$('#endButton').removeAttr("disabled", false);
	            		}
	                	
	                	if (sessionStorage.getItem("type") != "Conformance")
	                	{
	                		$('#gu-breadcrumb').text('GU');
			                $('#gu-breadcrumb').addClass('hidden');
	                	}
	                	else
	                	{
	                		$('#dut-breadcrumb').text('DUT');
			                $('#dut-breadcrumb').addClass('hidden');
	                	}
	                	
	                	var table = $.fn.dataTable.fnTables(true);
	                	if (table.length > 0)
	                	{
	                	    $(table).dataTable().fnAdjustColumnSizing();
	                	}
	                });
				}
			});
			
		});
  		
		$('#nextButton').off('click').on('click', function(e)
		{
			e.preventDefault();
			$('#idProject').val(sessionStorage.getItem('idProject'));
			$('#idDut').val(sessionStorage.getItem('idDut'));
			
			sessionStorage.setItem('setIcs', true);
			
			var MyRows = $('.table').find('tbody').find('tr');
			for (var i = 0; i < MyRows.length; i++)
			{
				var id = $(MyRows[i]).find('td:eq(1)').html();
				var value = $(MyRows[i]).find('.form-control option:selected').html();

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
					$('#dynamic').fadeOut('fast', function()
					{
	            		$("#dynamic").html(response);
	            	});
	                
					$('#dynamic').fadeIn('fast', function()
					{
	                	var table = $.fn.dataTable.fnTables(true);
	                	if (table.length > 0)
	                	{
	                	    $(table).dataTable().fnAdjustColumnSizing();
	                	}
	                });
				}
			});	
		});
  		
  		$('#scrButton').on('click', function()
  		{	
  			$('.badge').text("");
  			
			var MyRows = $('.table').find('tbody').find('tr');
			for (var i = 0; i < MyRows.length; i++)
			{
				var id = $(MyRows[i]).find('td:eq(1)').html();
				var value = $(MyRows[i]).find('.form-control option:selected').html();

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
								for (var i = 0; i < MyRows.length; i++)
								{
									if($(MyRows[i]).find('td:eq(0)').html() == result.id)
									{
										$(MyRows[i]).addClass('danger');
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
								for (var i = 0; i < MyRows.length; i++)
								{
									if ($(MyRows[i]).find('td:eq(0)').html() == result.id)
									{
										$(MyRows[i]).removeClass('danger');
									}
								}
							}
							$("#pleaseWaitDialog").modal('hide');
						});
						
						if (wrong == 0)
						{
							$('#nextButton').removeClass('disabled');
							$('#nextButton').removeAttr("disabled", false);
							
							if (sessionStorage.getItem("isConfigured") == "true")
							{
								$('#endButton').removeClass('disabled');
								$('#endButton').removeAttr('disabled', false);
							}
							
							$('#scrNeed').prop("hidden", true);
						}
						else
						{
							$('#scrNeed').prop("hidden", true);
							$('#fixErrors').prop("hidden",false);
						}
				   }
			});
  		});
  		
  		$('#changeButton').on('click', function()
  		{
  			$(".tab-pane.active").find('table tbody tr').dblclick();
  		});
  		
  		$('#endButton').off('click').on('click', function(e)
  		{
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedIcs", true);
  			
  			var MyRows = $('.table').find('tbody').find('tr');
			for (var i = 0; i < MyRows.length; i++)
			{
				var id = $(MyRows[i]).find('td:eq(1)').html();
				var value = $(MyRows[i]).find('.form-control option:selected').html();

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
                		$("#dynamic").html(response);
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
  		});
	}
	
	var onChangeFunctions = function()
	{
		$('.form-control').on('change', function()
		{
    		$('#nextButton').addClass('disabled');
    		$('#nextButton').prop("disabled", true);
    		$('#endButton').addClass('disabled');
    		$('#endButton').attr('disabled', true);
    		$('#scrNeed').prop("hidden", false);
    		$('#fixErrors').prop("hidden",true);
    	});
	}
	
	return {
		init: init
	}
})(ics);