/*******************************************************************************
 *      Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *      Project (AJOSP) Contributors and others.
 *      
 *      SPDX-License-Identifier: Apache-2.0
 *      
 *      All rights reserved. This program and the accompanying materials are
 *      made available under the terms of the Apache License, Version 2.0
 *      which accompanies this distribution, and is available at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 *      Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *      Alliance. All rights reserved.
 *      
 *      Permission to use, copy, modify, and/or distribute this software for
 *      any purpose with or without fee is hereby granted, provided that the
 *      above copyright notice and this permission notice appear in all
 *      copies.
 *      
 *      THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *      WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *      AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *      DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *      PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *      TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *      PERFORMANCE OF THIS SOFTWARE.
*******************************************************************************/
var ixits = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var _titleText = (sessionStorage.getItem("type") == "Conformance") ? 'Step 4<small> Configure your IXIT</small>' : 'Step 5<small> Configure your IXIT</small>';
	//-------------------------------------------------
	// IXIT TABLES
	//-------------------------------------------------
	var _$ixitsTables = jQuery('.table');
	var _ixitsTablesRows;
	
	var init = function()
	{	
		common.$title.html(_titleText);
		
		initDataTable();
		onClickFunctions();
	};
	
	var initDataTable = function()
	{
		// core tab will be the active one at load, as it is always loaded
		$('#1').addClass('in active');
		
		// init dataTables of the active tab
		$("#1").find('.table').dataTable({
			paging: false,
			searching: false,
			info: false,
			scrollY: common.$dynamicSection.height() - 128,
			columnDefs: [        
				{ orderable: false, targets: 3},
			],
			order: [0, 'asc']
		});
		
		// tables rows are stored into a var for efficiency
		_ixitsTablesRows = _$ixitsTables.find('tbody').find('tr');
		
		for (var i = 0; i < _ixitsTablesRows.length; i++)
		{
			var id = $(_ixitsTablesRows[i]).find('td:eq(0)').html();
			
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
				// number format
				$(_ixitsTablesRows[i]).find('.form-control').prop('type', 'number');
				$(_ixitsTablesRows[i]).find('.form-control').prop('min', '1');
				$(_ixitsTablesRows[i]).find('.form-control').keypress(isNumberKey);
			}
			else if (id == "79")
			{
				// date format
				$(_ixitsTablesRows[i]).find('.form-control').datepicker({
					format: 'yyyy-mm-dd',
				    maxViewMode: 'years',
				    startView: 'decade'
				});
			}
			else if (id == "80")
			{
				// url format
				$(_ixitsTablesRows[i]).find('.form-control').prop('type', 'url');
			}
			else if ((id == "19") || (id == "22"))
			{
				// selector format
				var value = $(_ixitsTablesRows[i]).find('.form-control').val();
				var selector = "<select class=\"form-control\" style=\"width:100%\">"
				+"<option disabled value=\"-3\">WPA2_AUTO</option>"
				+"<option disabled value=\"-2\">WPA_AUTO</option>"
				+"<option disabled value=\"-1\">Any</option>"
				+"<option value=\"0\">Open</option>"
				+"<option value=\"1\">WEP</option>"
				+"<option value=\"2\">WPA_TKIP</option>"
				+"<option value=\"3\">WPA_CCMP</option>"
				+"<option value=\"4\">WPA2_TKIP</option>"
				+"<option value=\"5\">WPA2_CCMP</option>"
				+"<option disabled value=\"6\">WPS</option></select>";
				$(_ixitsTablesRows[i]).find('td:last').empty();
				$(_ixitsTablesRows[i]).find('td:last').append(selector);
				$(_ixitsTablesRows[i]).find('.form-control').val(value != "" ? value : 0);
			}
			else
			{
				// string format
			}
			
			// disable IXIT that are loaded from DUT and sample info
			if ((id == "2") || (id == "4") || (id == "5") || (id == "7") || (id == "8") || (id == "9") || (id == "11") || (id == "78"))
			{
				//
				$(_ixitsTablesRows[i]).find('.form-control').addClass('disabled');
				$(_ixitsTablesRows[i]).find('.form-control').prop('disabled', true);
				$(_ixitsTablesRows[i]).addClass('text-muted');
			}
			else
			{
				var name = $(_ixitsTablesRows[i]).find('td:eq(1)').html();

				// if it was previously configured, load its last value
				if (sessionStorage.hasOwnProperty(name))
				{
					$(_ixitsTablesRows[i]).find('.form-control').val(sessionStorage.getItem(name));
				}
			}
		}
		
		// init dataTables of non-shown tabs when they are going to be shown
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
					info: false,
					scrollY: common.$dynamicSection.height() - 128,
					columnDefs: [        
						{ orderable: false, targets: 3},
					],
					order: [0, 'asc']
				});
			}
		});
	};
	
	// checks if the pressed key is a number
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
		common.$nextStepButton.off('click').on('click', function(e)
		{
  			e.preventDefault();
  			
  			if (sessionStorage.getItem('isConfigured'))
			{
				sessionStorage.setItem("modifiedIxit", true);
			}
  			
			for (var i = 0; i < _ixitsTablesRows.length; i++)
			{
				var id = $(_ixitsTablesRows[i]).find('td:eq(1)').html();
				var value = $(_ixitsTablesRows[i]).find('.form-control').val();
				
				sessionStorage.setItem(id, value);
			}
			
			$('#idProjectNext').val(sessionStorage.getItem('idProject'));
			$.ajax({
				type: 'GET',
				url: $('#nextForm').attr('action'),
				data: $('#nextForm').serialize(),
				success: function(response)
				{
					common.$dynamicSection.fadeOut('fast', function()
					{
						common.$dynamicSection.html( response );
	            	});
	                
					common.$dynamicSection.fadeIn('fast', function()
					{
						common.adjustDataTablesHeader();
	                });
					
					common.selectNavigationElement($('#gp-nav'));
				}
			});		
		});
  		
  		common.$previousStepButton.off('click').on('click', function(e)
  		{
			e.preventDefault();
			
			$('#idProjectPrev').val(sessionStorage.getItem('idProject'));
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
					
					common.selectNavigationElement($('#ics-nav'));
				}
			});
		});
  		
  		common.$saveProjectButton.off('click').on('click', function(e)
  		{
  			e.preventDefault();
  			
  			sessionStorage.setItem("modifiedIxit", true);
  			
			for (var i = 0; i < _ixitsTablesRows.length; i++)
			{
				var id = $(_ixitsTablesRows[i]).find('td:eq(1)').html();
				var value = $(_ixitsTablesRows[i]).find('.form-control').val();
				
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
                		common.$dynamicSection.html(response);
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
})(ixits);