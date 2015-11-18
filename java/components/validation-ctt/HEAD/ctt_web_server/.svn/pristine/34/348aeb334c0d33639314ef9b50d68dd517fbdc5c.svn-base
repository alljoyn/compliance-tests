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
var results = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var title = jQuery('#title');
	var titleText = "Test Cases results<small> for \"" + sessionStorage.getItem("projectName") + "\"</small>";
	
	var init = function()
	{
		title.html(titleText);
		
		initDataTable();
		
		$.fn.multiline = function(text)
		{
		    this.text(text);
		    this.html(this.html().replace(/\n/g,'<br/>'));
		    return this;
		};
		
		onClickFunctions();
		
		$.ajax({
			type: 'GET',
			url: 'results/tr/ranAll',
			data: {
				idProject : sessionStorage.getItem("idProject")
			},
			success: function(ranAll) {
				if(ranAll) {
					$('#createTR').removeClass('disabled');
					$('#createTR').prop("disabled", false);
					$('#notRanAll').prop("hidden",true);
				}
			}
		});
	}
	
	var initDataTable = function()
	{
		$('#table').dataTable({
			pagingType: 'full_numbers',
			scrollY: ($(window).height()/2),
			order: [0, 'asc']
		});
	}
	
	var onClickFunctions = function()
	{
		$('#prevButton').off('click').on('click', function(e)
		{
			e.preventDefault();
			
			$.ajax({
	            type: "GET",
	            url: 'project',
	            success: function(response)
	            {
	            	$('#dynamic').fadeOut('fast', function()
	            	{
	            		$("#dynamic").html( response );
	            	});
	                $('#dynamic').fadeIn('fast');
	                
	                $('#prevButton').addClass('disabled');
	                $('#prevButton').attr('disabled', true);
	                $('.nav-stacked li').addClass('disabled');
			   		$('.nav-stacked li a').attr('disabled', true);
	            }
	        });
		});
				
		$('#createTR').on('click', function(e){
			var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
			
			$.ajax({
				type : 'POST',
				url : 'results/tr/create',
				beforeSend: function(xhr) {
		            xhr.setRequestHeader(header, token);
		        },
				data : {
					idProject : sessionStorage.getItem("idProject")
				},
				success: function(created) {
					if(created) {
						$('#viewTR').removeClass('disabled');
						$('#viewTR').prop("disabled", false);
						$('#sendTR').removeClass('disabled');
						$('#sendTR').prop("disabled", false);
					}
				}
			});
		});

		$('#viewTR').on('click', function()
		{
			this.href= 'results/tr/view?idProject='+sessionStorage.getItem("idProject");
		});

		$('#sendTR').on('click', function()
		{
			this.href= 'results/tr/send?idProject='+sessionStorage.getItem("idProject");
		});

		$('#table tbody tr').on('click', function() {
			$.ajax({
				cache: false,
				type : 'GET',
				url : 'results/fullLog',
				contentType: "text/plain; charset=UTF-8",
				data : {
					id : sessionStorage.getItem("idProject"),
					file : $(this).find('td:last').html()
				},
				success: function (data)
				{
					var w = window.open();

					$(w.document.head).html('<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />');    
					$(w.document.body).multiline(data);
			   }
			});
		});
	}
	
	return {
		init: init
	}
})(results);	