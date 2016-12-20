/*******************************************************************************
 *      Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *      Source Project (AJOSP) Contributors and others.
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
var results = (function()
{
	//-------------------------------------------------
	// TITLE
	//-------------------------------------------------
	var _titleText = "Test Cases results<small> for \"" + sessionStorage.getItem("projectName") + "\"</small>";
	
	//-------------------------------------------------
	// TABLE
	//-------------------------------------------------
	var _$resultsTable = jQuery('#table');
	
	//-------------------------------------------------
	// BUTTONS
	//-------------------------------------------------
	var _$createTRButton = jQuery('#createTR');
	var _$viewTRButton = jQuery('#viewTR');
	var _$sendTRPackageButton = jQuery('#sendTR');
	
	//-------------------------------------------------
	// TOOLTIPS
	//-------------------------------------------------
	var _$createTRTooltip = jQuery('#createTRTooltip');
	var _$viewTRTooltip = jQuery('#viewTRTooltip');
	var _$sendTRPackageTooltip = jQuery('#sendTRTooltip');
	
	var init = function()
	{
		common.$title.html(_titleText);

		_initTooltips();		
		_initDataTable();
		
		$.fn.multiline = function(text)
		{
		    this.text(text);
		    this.html(this.html().replace(/\n/g,'<br/>'));
		    return this;
		};
		
		_onClickFunctions();
		_allTestCasesRun();
	}
	
	var _initTooltips = function()
	{
		_$createTRTooltip.tooltip({'placement': 'top'});
		_$viewTRTooltip.tooltip({'placement': 'top'});
		_$sendTRPackageTooltip.tooltip({'placement': 'top'});
	}
	
	var _initDataTable = function()
	{
		_$resultsTable.DataTable({
			pagingType: 'full_numbers',
			scrollY: common.$dynamicSection.height() - 164,
			order: [0, 'asc']
		});
	}
	
	var _onClickFunctions = function()
	{
		common.$previousStepButton.off('click').on('click', function(e)
		{
			e.preventDefault();
			
			$.ajax({
	            type: "GET",
	            url: 'project',
	            success: function(response)
	            {
	            	common.$dynamicSection.fadeOut('fast', function()
	            	{
	            		common.$dynamicSection.html( response );
	            	});
	            	common.$dynamicSection.fadeIn('fast');
	                
	            	common.disableButtons(common.$previousStepButton);
	            	common.disableNavigationBar();
	            }
	        });
		});
				
		_$createTRButton.on('click', function(e)
		{		
			$.ajax({
				type : 'POST',
				url : 'results/tr/create',
				beforeSend: function(xhr)
				{
					xhr.setRequestHeader(common.csrfHeader, common.csrfToken);
		        },
				data :
				{
					idProject : sessionStorage.getItem("idProject")
				},
				success: function(created)
				{
					if (created)
					{
						common.enableButtons(_$viewTRButton, _$sendTRPackageButton);
						_$viewTRTooltip.tooltip('disable');
						_$sendTRPackageTooltip.tooltip('disable');
					}
					else
					{
						common.disableButtons(_$viewTRButton, _$sendTRPackageButton);
						_$viewTRTooltip.tooltip('enable');
						_$sendTRPackageTooltip.tooltip('enable');
					}
				}
			});
		});

		_$viewTRButton.on('click', function(e)
		{
			e.preventDefault();

			var w = window.open('results/tr/view?idProject='+sessionStorage.getItem("idProject"));
		});

		_$sendTRPackageButton.on('click', function(e)
		{
			e.preventDefault();

			waitingDialog.show('Uploading test results to CAWT');
			
			$.ajax({
				cache: false,
				type: 'GET',
				url: 'results/tr/send?idProject='+sessionStorage.getItem("idProject")
			}).done(function(data) {
				
				waitingDialog.hide();
				
				var message = 'Test results have been successfully uploaded. Do you want to download them?';
				var titleLabel = 'Upload succeeded';
				
				if (data.resultCode === 3)
				{
					message = 'A development project is not allowed to upload test results. Do you want to download them?';
					titleLabel = 'Upload not allowed';
				}
				else if (data.resultCode === 2)
				{
					message = 'An error has occurred while creating the Zip file because some logs have not been found. Please run again the following test cases: ';
					titleLabel = 'Zip creation failed';
					
					message += '<ol style="margin-top: 10px">';
					$.each(data.resultMessages, function(i, logNotFound)
					{
						message += '<li>' + logNotFound + '</li>';
					});
					message += '</ol>';
					message += 'and create test results after that.';
					
					yesNoModal.show(message, {title: titleLabel, yesButton: false, noLabel: 'OK'});
					
					return;
				}
				else if (data.resultCode === 1)
				{
					message = data.resultMessages[0] + '. Do you want to download test results?';
					titleLabel = 'Upload failed';
					
				}
				
				yesNoModal.show(message, {title: titleLabel, yesOnClick: function()
				{
					window.open('results/tr/download?idProject='+sessionStorage.getItem("idProject"), "_self");
				}});
			});
		});

		$(_$resultsTable.dataTable().fnGetNodes()).on('click', function()
		{	
			var testCaseName = $(this).find('td:first').html();
			
			var w = window.open('results/fullLog?id=' + sessionStorage.getItem("idProject")
					+ '&file=' + $(this).find('td:last').html());
			
			var isIE = /*@cc_on!@*/false || !!document.documentMode;
		    // Edge 20+
			var isEdge = !isIE && !!window.StyleMedia;
			
			setTimeout('', 500); // [JTF] Bad solution
			
			if (isIE || isEdge)
			{
				console.log("is IE");
				
				if (w.addEventListener)
				{ 
					w.addEventListener("load", _formatLog(w, testCaseName), false);
				}
				else if (w.attachEvent)
				{ 
					w.attachEvent("onload", _formatLog(w, testCaseName));
				}
				else if (w.onLoad)
				{
					w.onload = _formatLog(w, testCaseName);
				}
			}
			else
			{
				w.onload = function()
				{
					_formatLog(w, testCaseName);
				};
			}
		});
	}
	
	var _formatLog = function(w, testCaseName)
	{
		$(w.document.head).html('<link rel="shortcut icon" href="' + window.location.href +'resources/img/favicon.ico" type="image/vnd.microsoft.icon">');
		w.document.title = testCaseName;
		var newText = $(w.document.body).find('pre').text()
			.replace(/PASS/g, '<span style="color: #99CC32; font-weight: bold; font-size: 12pt;">PASS</span>')
			.replace(/FAIL/g, '<span style="color: #D43D1A; font-weight: bold; font-size: 12pt;">FAIL</span>')
			.replace(/INCONC/g, '<span style="color: #EEC900; font-weight: bold; font-size: 12pt;">INCONC</span>');
		$(w.document.body).find('pre').html(newText);
	}
	
	var _allTestCasesRun = function()
	{
		$.ajax({
			type: 'GET',
			url: 'results/tr/allRun',
			data:
			{
				idProject : sessionStorage.getItem("idProject")
			},
			success: function(ranAll)
			{
				if (ranAll)
				{
					common.enableButtons(_$createTRButton);
					_$createTRTooltip.tooltip('disable');
				}
			}
		});
	}
	
	return {
		init: init
	}
})(results);	