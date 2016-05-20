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
	var _titleText = (sessionStorage.getItem("type") == "Conformance") ? 'Step 5<small> Configure your General Parameters</small>' : 'Step 6<small> Configure your General Parameters</small>';
	//-------------------------------------------------
	// GENERAL PARAMETERS TABLE
	//-------------------------------------------------
	var _$generalParametersTable = jQuery('#table');
	var _generalParametersTableRows;
	
	var init = function()
	{	
		common.$title.html(_titleText);
		
		initDataTable();
		onClickFunctions();
	};
	
	var initDataTable = function()
	{
		_$generalParametersTable.dataTable({
			paging: false,
			searching: false,
			info: false,
			scrollY: common.$dynamicSection.height() - 86,
			columnDefs: [        
				{ orderable: false, targets: 3},
			],
			order: [0, 'asc']
		});
		
		_generalParametersTableRows = _$generalParametersTable.find('tbody').find('tr');
		for (var i = 0; i < _generalParametersTableRows.length; i++)
		{
			var id = $(_generalParametersTableRows[i]).find('td:eq(1)').html();
			
			$(_generalParametersTableRows[i]).find('.form-control').keypress(isNumberKey);
			
			if (sessionStorage.hasOwnProperty(id))
			{
				$(_generalParametersTableRows[i]).find('.form-control').val(sessionStorage.getItem(id));
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
		common.$nextStepButton.off('click').on('click', function(e){
  			e.preventDefault();
  			
  			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedParameters", true);
			}
  			
			for (var i = 0; i < _generalParametersTableRows.length; i++)
			{
				var id = $(_generalParametersTableRows[i]).find('td:eq(1)').html();
				var value = $(_generalParametersTableRows[i]).find('.form-control').val();
				
				sessionStorage.setItem(id, value);
			}
			
			$.ajax({
				type: 'GET',
				url: 'testcase',
				success: function(response)
				{
					common.$dynamicSection.fadeOut('fast', function()
					{
						common.$dynamicSection.html(response);
	            	});
	                
					common.$dynamicSection.fadeIn('fast', function()
					{
						common.adjustDataTablesHeader();
	                });
	                
					common.enableButtons(common.$saveProjectButton);
					common.disableButtons(common.$nextStepButton);
	                
	                common.selectNavigationElement($('#tc-nav'));
				}
			});
		});
  		
  		common.$previousStepButton.off('click').on('click', function(e)
  		{
			e.preventDefault();
			
			$('#idProject').val(sessionStorage.getItem('idProject'));
			
			$.ajax({
				type: 'GET',
				url: $('#prevForm').attr('action'),
				data: $('#prevForm').serialize(),
				success: function(response)
				{
					common.$dynamicSection.fadeOut('fast', function()
					{
						common.$dynamicSection.html(response);
	            	});
	                
					common.$dynamicSection.fadeIn('fast', function()
					{
						common.adjustDataTablesHeader();
	                });
					
					common.selectNavigationElement($('#ixit-nav'));
				}
			});
		});
  		
  		common.$saveProjectButton.off('click').on('click', function(e)
  		{
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedParameters", true);
  			
  			for (var i = 0; i < _generalParametersTableRows.length; i++)
  			{
				var id = $(_generalParametersTableRows[i]).find('td:eq(1)').html();
				var value = $(_generalParametersTableRows[i]).find('.form-control').val();
				
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
			        	xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
			        },
				   	data:
					{
					   	data : data	
					},
			        success: function(response)
			        {
			        	common.$dynamicSection.fadeOut('fast', function()
	                	{
	                		common.$dynamicSection.html( response );
	                	});
	                    common.$dynamicSection.fadeIn('fast');
	                    
	                    common.disableNavigationBar();
	                    common.disableButtons(common.$previousStepButton, common.$nextStepButton, common.$saveProjectButton);
			        }
			});	
  		});
	}
	
	return {
		init: init
	}
})(parameters);