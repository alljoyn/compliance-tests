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
var parameters = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var title = jQuery('#title');
	
	var gpTable = jQuery('#table');
	
	var init = function()
	{
		title.html("Step 6<small> Configure your General Parameters</small>");
		if (sessionStorage.getItem("type") == "Conformance")
		{
			title.html(function(i, oldText)
			{
				return oldText === 'Step 6<small> Configure your General Parameters</small>' ? 'Step 5<small> Configure your General Parameters</small>' : oldText;
			});
		}
		
		initDataTable();
		onClickFunctions();
	};
	
	var initDataTable = function()
	{
		$('#table').dataTable({
			paging: false,
			searching: false,
			"sDom": '<"top">rt<"bottom"flp><"clear">',
			scrollY: ($(window).height()*9/15),
			columnDefs: [        
				{ orderable: false, targets: 3},
			],
			order: [0, 'asc']
		});
		
		var MyRows = $('#table').find('tbody').find('tr');
		for (var i = 0; i < MyRows.length; i++)
		{
			var id = $(MyRows[i]).find('td:eq(1)').html();
			
			$(MyRows[i]).find('.form-control').keypress(isNumberKey);
			
			if (sessionStorage.hasOwnProperty(id))
			{
				$(MyRows[i]).find('.form-control').val(sessionStorage.getItem(id));
			}
		}
	}
	
	var isNumberKey = function(evt)
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
		$('#nextButton').off('click').on('click', function(e){
  			e.preventDefault();
  			
  			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedParameters", true);
			}
  			
			var MyRows = $('#table').find('tbody').find('tr');
			for (var i = 0; i < MyRows.length; i++) {
				var id = $(MyRows[i]).find('td:eq(1)').html();
				var value = $(MyRows[i]).find('.form-control').val();
				
				sessionStorage.setItem(id, value);
			}
			
			$.ajax({
				type: 'GET',
				url: 'testcase',
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
	                $('#endButton').removeClass('disabled');
	                $('#endButton').removeAttr('disabled', false);
				}
			});
		});
  		
  		$('#prevButton').off('click').on('click', function(e){
			e.preventDefault();
			
			$('#idProject').val(sessionStorage.getItem('idProject'));
			$.ajax({
				type: 'GET',
				url: $('#prevForm').attr('action'),
				data: $('#prevForm').serialize(),
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
				}
			});
			//$('#prevForm').submit();
		});
  		
  		$('#endButton').off('click').on('click', function(e) {
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedParameters", true);
  			
  			var MyRows = $('#table').find('tbody').find('tr');
			for (var i = 0; i < MyRows.length; i++) {
				var id = $(MyRows[i]).find('td:eq(1)').html();
				var value = $(MyRows[i]).find('.form-control').val();
				
				sessionStorage.setItem(id, value);
			}
			
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
  		});
	}
	
	return {
		init: init
	}
})(parameters);