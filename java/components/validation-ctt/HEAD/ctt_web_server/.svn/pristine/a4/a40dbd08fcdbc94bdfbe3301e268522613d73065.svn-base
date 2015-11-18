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
var testcases = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var title = jQuery('#title');
	//-------------------------------------------------
	// TEST CASES TABLE AND BUTTONS
	//-------------------------------------------------
	var table = jQuery('#tcTable');
	
	var selectAllButton = jQuery('#selectAll');
	var deselectAllButton = jQuery('#deselectAll');
	
	var prevButton = jQuery('#prevButton');
	var endButton = jQuery('#endButton');
	var nextButton = jQuery('#nextButton');
	//-------------------------------------------------
	// POST REQUEST TOKEN AND HEADER
	//-------------------------------------------------
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	
	var init = function()
	{
		title.html("Step 7<small> Test Plan generation</small>");
		if (sessionStorage.getItem("type") == "Conformance")
		{
			title.html(function(i, oldText)
			{
				return oldText === 'Step 7<small> Test Plan generation</small>' ? 'Step 6<small> Test Plan generation</small>' : oldText;
			});
		}
		
		initDataTable();
		onClickFunctions();
	};
	
	var initDataTable = function()
	{
		var data = {};
		for (var j = 0; j < sessionStorage.length; j++)
		{
			var key = sessionStorage.key(j);
			data[key] = sessionStorage.getItem(key);
		}
		
		table.DataTable(
		{
			"ajax": {
				"type": "GET",
				"url": "testcase/dataTable",
				"data": {
					data: data
				},
				"dataType": "json"
			},
			"columns": [
	            { "data": "id" },
	            { "data": "name", "width": "15%" },
	            { "data": "description", "width": "85%" },
	            { "data": "selected", "width": "5%",
	            	"render": function(data) {
	            		return data == true ? "<input class=\"is_checkbox\" type=\"checkbox\" checked/>" : "<input class=\"is_checkbox\" type=\"checkbox\"/>";
	            	}
	            }
			],
			autoWidth: false,
			paging: false,
			searching: false,
			"sDom": '<"top">rt<"bottom"flp><"clear">',
			scrollY: ($(window).height()*5/9),
			columnDefs: [        
				{ orderable: false, targets: 3},
				{ visible: false, targets: 0}
			],
			order: [0, 'asc']
		}).on('init.dt', function()
		{
			if (sessionStorage.getItem("type") != "Development")
			{
				$.ajax({
					url: "testcase/disabled",
					type: 'GET',
					data: {
						idProject : sessionStorage.getItem("idProject")
					},
					success: function(data)
					{
						var MyRows = $('#tcBody').find('tr');

						$.each(data, function(i, disabled)
						{
							for (var j = 0; j < MyRows.length; j++)
							{
								if (table.DataTable().row($(MyRows[j])).data().id == disabled)
								{
									$(MyRows[j]).find('.is_checkbox').prop('checked', false);
									$(MyRows[j]).find('.is_checkbox').prop('disabled', true);
									$(MyRows[j]).addClass('text-muted');
								}
							}
						});
					}
				});
			}
			else
			{
				var MyRows = $('#tcBody').find('tr');
				var noGUs = sessionStorage.getItem("guNames") == "";

				for (var j = 0; j < MyRows.length; j++)
				{
					if ((noGUs) && (table.DataTable().row($(MyRows[j])).data().name.indexOf("IOP") >= 0))
					{
						$(MyRows[j]).find('.is_checkbox').prop('checked', false);
						$(MyRows[j]).find('.is_checkbox').prop('disabled', true);
						$(MyRows[j]).addClass('text-muted');
					}
				}
			}
				
			$('tbody').find('tr').dblclick(function()
			{
				if (!($(this).hasClass("text-muted")))
				{
					var val = $(this).find('.is_checkbox').is(':checked');
					$(this).find('.is_checkbox').prop('checked', !(val));
				}
			});
		});
	};
	
	var onClickFunctions = function()
	{
		onClickEnd();
		onClickSelectors();
		onClickBack();
	}
	
	var onClickEnd = function()
	{
		endButton.off('click').on('click', function()
		{
			var MyRows = $('.table').find('tbody').find('tr');
			for (var i = 0; i < MyRows.length; i++)
			{
				var id = table.DataTable().row($(MyRows[i])).data().name;
				var value = $(MyRows[i]).find('.is_checkbox').is(':checked');
				
				if (value == true)
				{
					sessionStorage.setItem(id, value);
				}
			}
			
			sessionStorage.setItem("applyTC", ($('.is_checkbox').length) - ($('.text-muted').length));
			
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
	                    
	                    buttonState(endButton, false);
	                    buttonState(nextButton, false);
	                    buttonState(prevButton, false);
	                    $('.nav-stacked li').addClass('disabled');
				   		$('.nav-stacked li a').attr('disabled', true);
			        }
			});	
		});
	}
	
	var buttonState = function(button, state)
	{
		if (state)
		{
			button.removeClass('disabled');
            button.removeAttr('disabled', false);
		}
		else
		{
			button.addClass('disabled');
            button.attr('disabled', true);
		}
	}
	
	var onClickSelectors = function()
	{
		selectAllButton.on('click', function()
		{
			$('.is_checkbox').prop('checked', true);
			$('.text-muted').find('.is_checkbox').prop('checked', false);
			
		});

  		deselectAllButton.on('click', function()
  		{
			$('.is_checkbox').prop('checked', false);
		});
	}
	
	var onClickBack = function()
	{
		prevButton.off('click').on('click', function(e){
			e.preventDefault();
			
			$('#idProject').val(sessionStorage.getItem('idProject'));
			
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
	                
					buttonState(nextButton, true);
					
					if (sessionStorage.getItem("isConfigured") == "false")
					{
						buttonState(endButton, false);
					}
				}
			});
		});
	}
	
	return {
		init: init
	}
})(testcases);