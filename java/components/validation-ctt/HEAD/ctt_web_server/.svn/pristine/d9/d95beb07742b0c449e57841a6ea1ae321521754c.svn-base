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
var ixits = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var title = jQuery('#title');
	//-------------------------------------------------
	// NAVIGATION BUTTONS
	//-------------------------------------------------
	var prevButton = jQuery('#prevButton');
	var endButton = jQuery('#endButton');
	var nextButton = jQuery('#nextButton');
	//-------------------------------------------------
	// POST REQUEST TOKEN AND HEADER
	//-------------------------------------------------
	var token = jQuery("meta[name='_csrf']").attr("content");
	var header = jQuery("meta[name='_csrf_header']").attr("content");
	
	var init = function()
	{
		title.html("Step 5<small> Configure your IXIT</small>");
		
		if (sessionStorage.getItem("type") == "Conformance")
		{
			title.html(function(i, oldText)
			{
				return oldText === 'Step 5<small> Configure your IXIT</small>' ? 'Step 4<small> Configure your IXIT</small>' : oldText;
			});
		}
		
		initDataTable();
		onClickFunctions();
	};
	
	var initDataTable = function()
	{
		$('#1').addClass('in active');
		
		$("#1").find('.table').dataTable({
			paging: false,
			searching: false,
			"sDom": '<"top">rt<"bottom"flp><"clear">',
			scrollY: ($(window).height()/2),
			columnDefs: [        
				{ orderable: false, targets: 3},
			],
			order: [0, 'asc']
		});
		
		var MyRows = $('.table').find('tbody').find('tr');
		for (var i = 0; i < MyRows.length; i++)
		{
			var id = $(MyRows[i]).find('td:eq(0)').html();
			
			if ((id == "1") || (id == "12") || (id == "13") || (id == "14") || (id == "15") || (id == "16")
					|| (id == "17") || (id == "24") || (id == "25") || (id == "26") || (id == "27")
					|| (id == "28") || (id == "29") || (id == "30") || (id == "31") || (id == "32")
					|| (id == "33") || (id == "35") || (id == "36") || (id == "37") || (id == "38")
					|| (id == "39") || (id == "41") || (id == "42") || (id == "43") || (id == "44")
					|| (id == "45") || (id == "46") || (id == "47") || (id == "48") || (id == "49")
					|| (id == "50") || (id == "52") || (id == "53") || (id == "54") || (id == "55")
					|| (id == "56") || (id == "57") || (id == "58") || (id == "59") || (id == "63")
					|| (id == "64") || (id == "65") || (id == "66") || (id == "67") || (id == "68")
					|| (id == "69") || (id == "71") || (id == "72") || (id == "73") || (id == "74")
					|| (id == "75") || (id == "76") || (id == "77"))
			{
				$(MyRows[i]).find('.form-control').prop('type', 'number');
				$(MyRows[i]).find('.form-control').prop('min', '1');
				$(MyRows[i]).find('.form-control').keypress(isNumberKey);
			}
			else if (id == "79")
			{
				//$(MyRows[i]).find('.form-control').prop('type', 'date');
				$(MyRows[i]).find('.form-control').datepicker({
					format: 'yyyy-mm-dd',
				    maxViewMode: 'years',
				    startView: 'decade'
				});
			}
			else if (id == "80")
			{
				$(MyRows[i]).find('.form-control').prop('type', 'url');
			}
			else if ((id == "19") || (id == "22"))
			{
				var value = $(MyRows[i]).find('.form-control').val();
				var selector = "<select class=\"form-control\" style=\"width:100%\">"
				+"<option value=\"-3\">WPA2_AUTO</option>"
				+"<option value=\"-2\">WPA_AUTO</option>"
				+"<option value=\"-1\">Any</option>"
				+"<option value=\"0\">Open</option>"
				+"<option value=\"1\">WEP</option>"
				+"<option value=\"2\">WPA_TKIP</option>"
				+"<option value=\"3\">WPA_CCMP</option>"
				+"<option value=\"4\">WPA2_TKIP</option>"
				+"<option value=\"5\">WPA2_CCMP</option>"
				+"<option value=\"6\">WPS</option></select>";
				$(MyRows[i]).find('td:last').empty();
				$(MyRows[i]).find('td:last').append(selector);
				$(MyRows[i]).find('.form-control').val(value);
			}
			
			if ((id == "2") || (id == "4") || (id == "5") || (id == "7") || (id == "8") || (id == "9") || (id == "11"))
			{
				$(MyRows[i]).find('.form-control').addClass('disabled');
				$(MyRows[i]).find('.form-control').prop('disabled', true);
				$(MyRows[i]).addClass('text-muted');
			}
			else
			{
				var name = $(MyRows[i]).find('td:eq(1)').html();

				if (sessionStorage.hasOwnProperty(name))
				{
					$(MyRows[i]).find('.form-control').val(sessionStorage.getItem(name));
				}
			}
		}
		
		$('a[data-toggle="tab"]').on('shown.bs.tab', function (e)
		{
			e.target; // active tab
			e.relatedTarget; // previous tab
			var tab = $(e.target).attr('href');
			   
			if (!$("" + tab).find('.table').hasClass('dataTable'))
			{
				$("" + tab).find('.table').dataTable({
					paging: false,
					searching: false,
					"sDom": '<"top">rt<"bottom"flp><"clear">',
					scrollY: 500,
					columnDefs: [        
						{ orderable: false, targets: 3},
					],
					order: [0, 'asc']
				});
			}
		});
	};
	
	var isNumberKey = function (evt)
	{
	    var charCode = (evt.which) ? evt.which : event.keyCode;
	    if (charCode > 31 && (charCode < 48 || charCode > 57))
	    {
	        return false;
	    }
	    return true;
	}   
	
	var onClickFunctions = function()
	{
		nextButton.off('click').on('click', function(e)
		{
  			e.preventDefault();
  			
  			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedIxit", true);
			}
  			
			var MyRows = $('.table').find('tbody').find('tr');
			for (var i = 0; i < MyRows.length; i++)
			{
				var id = $(MyRows[i]).find('td:eq(1)').html();
				var value = $(MyRows[i]).find('.form-control').val();
				
				sessionStorage.setItem(id, value);
			}
			
			$('#idProjectNext').val(sessionStorage.getItem('idProject'));
			$.ajax({
				type: 'GET',
				url: $('#nextForm').attr('action'),
				data: $('#nextForm').serialize(),
				success: function(response)
				{
					$('#dynamic').fadeOut('fast', function()
					{
	            		$("#dynamic").html( response );
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
  		
  		prevButton.off('click').on('click', function(e)
  		{
			e.preventDefault();
			
			$('#idProjectPrev').val(sessionStorage.getItem('idProject'));
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
	                	var table = $.fn.dataTable.fnTables(true);
	                	if (table.length > 0)
	                	{
	                	    $(table).dataTable().fnAdjustColumnSizing();
	                	}
	                });
				}
			});
		});
  		
  		endButton.off('click').on('click', function(e)
  		{
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedIxit", true);
  			
  			var MyRows = $('.table').find('tbody').find('tr');
			for (var i = 0; i < MyRows.length; i++)
			{
				var id = $(MyRows[i]).find('td:eq(1)').html();
				var value = $(MyRows[i]).find('.form-control').val();
				
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
	
	return {
		init: init
	}
})(ixits);