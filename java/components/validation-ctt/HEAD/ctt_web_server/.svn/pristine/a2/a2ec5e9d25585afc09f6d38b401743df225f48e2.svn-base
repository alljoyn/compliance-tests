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
var common = (function()
{
	//-------------------------------------------------
	// LOCAL AGENT KEY BUTTONS
	//-------------------------------------------------	
	var apiKeyButton = jQuery('.api-key');
	var closeApiKeyButton = jQuery('#closeApiKey');
	var apiKeyField = jQuery('#key');
	var apiKeyModal = jQuery('#generatedKey');
	//-------------------------------------------------
	// NAVIGATION BAR
	//-------------------------------------------------	
	var navigationElements = jQuery('.asa-left-nav li');
	//-------------------------------------------------
	// BREADCRUMBS
	//-------------------------------------------------	
	var projectBreadcrumb = jQuery('#project-breadcrumb');
	var dutBreadcrumb = jQuery('#dut-breadcrumb');
	var guBreadcrumb = jQuery('#gu-breadcrumb');
	//-------------------------------------------------
	// POST REQUEST TOKEN AND HEADER
	//-------------------------------------------------
	var token = jQuery("meta[name='_csrf']").attr("content");
	var header = jQuery("meta[name='_csrf_header']").attr("content");
	
	var init = function()
	{
		onClickFunctions();
		
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
            }
        });
	}
	
	var onClickFunctions = function()
	{
		apiKeyButton.on('click', function()
		{
			$.ajax({
				type : 'POST',
				url : 'project/generateKey',
				beforeSend: function(xhr)
				{
		            xhr.setRequestHeader(header, token);
		        },
				success : function (data)
				{
					apiKeyField.empty();
					apiKeyField.append(data);
					apiKeyModal.modal('show');
				}
			});	
		});
		
		closeApiKeyButton.on('click', function()
		{
			apiKeyField.empty();
		});
		
		navigationElements.on('click', function(e)
		{
			e.preventDefault();	
			
			var link = $(this).find('a').attr('href');
			
			if ((link == 'ics') || (link == 'parameter') || (link == 'testcase'))
			{
				link+='?idProject='+sessionStorage.getItem('idProject');
			}
			else if (link == 'ixit')
			{
				link+='?idProject='+sessionStorage.getItem('idProject')+'&idDut='+sessionStorage.getItem('idDut');
			}
			
			//jQuery.address.value(link);
			
			if (!$(this).hasClass('disabled'))
			{
				$.ajax({
		            type: "GET",
		            url: link,
		            success: function(response)
		            {	
		            	if (link == 'project')
	            		{
		            		navigationElementStatus(false);
					   		$('#prevButton').addClass('disabled');
		            		$('#prevButton').attr('disabled', true);
		            		$('#nextButton').addClass('disabled');
		            		$('#nextButton').attr('disabled', true);
		            		$('#endButton').addClass('disabled');
		            		$('#endButton').attr('disabled', true);
		            		
		            		sessionStorage.clear();
	            		}
		            	else
	            		{
		            		$('#prevButton').removeClass('disabled');
		            		$('#prevButton').removeAttr('disabled', false);
		            		
		            		if (link.indexOf('testcase') >= 0)
	            			{
		            			$('#nextButton').addClass('disabled');
			            		$('#nextButton').attr('disabled', true);
	            			}
		            		else
		            		{
		            			$('#nextButton').removeClass('disabled');
			            		$('#nextButton').removeAttr('disabled', false);
		            		}
		            		
		            		$('#endButton').removeClass('disabled');
		            		$('#endButton').removeAttr('disabled', false);
	            		}
		            	
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
		                
		                updateBreadcrumbs(link, sessionStorage.getItem('projectName'), sessionStorage.getItem('type'), 
		                		sessionStorage.getItem('associatedDut'), sessionStorage.getItem('associatedGu'));
		            }
		        });
			}
		});
	}
	
	var updateBreadcrumbs = function(url, projectName, projectType, dutName, guNames)
	{
		if (url.indexOf('project') < 0)
		{
			showBreadcrumb(projectBreadcrumb, projectName, true);
            
            if (url.indexOf('dut') < 0)
            {
            	showBreadcrumb(dutBreadcrumb, dutName, true);
                
                if ((projectType != "Conformance") && (url.indexOf('gu') < 0))
                {
                	showBreadcrumb(guBreadcrumb, guNames, true);
                }
                else
                {
                	showBreadcrumb(guBreadcrumb, 'GU', false);
                }
            }
            else
            {
            	showBreadcrumb(dutBreadcrumb, 'DUT', false);
            }
		}
	}
	
	var showBreadcrumb = function(breadcrumb, value, shown)
	{
		breadcrumb.text(value);
		
		if (shown)
		{
			breadcrumb.removeClass('hidden');
		}
		else
		{
			breadcrumb.addClass('hidden');
		}
	}
	
	var navigationElementStatus = function(enabled)
	{
		if (enabled)
		{
			navigationElements.removeClass('disabled');
	   		navigationElements.find('a').removeAttr('disabled', false);
		}
		else
		{
			navigationElements.addClass('disabled');
	   		navigationElements.find('a').attr('disabled', true);
		}
	}
	
	var logoutFormSubmit = function()
	{
		logoutForm.submit();
	};
	
	return {
		init: init,
		navigationElementStatus: navigationElementStatus,
		logout: logoutFormSubmit
	}
})(common);