/*******************************************************************************
 * Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
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
 *       THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *       WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *       WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *       AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *       DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *       PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *       TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *       PERFORMANCE OF THIS SOFTWARE.
 *******************************************************************************/
var yesNoModal = yesNoModal || (function ($) {
    'use strict';

	// Creating modal dialog's DOM
	var $dialog = $(
		'<div class="modal fade" data-backdrop="static" data-keyboard="false" tabindex="-1" role="dialog" aria-hidden="true" style="padding-top:15%; overflow-y:visible;">' +
		'<div class="modal-dialog modal-m">' +
		'<div class="modal-content">' +
			'<div class="modal-header"><h3 style="margin:0;"></h3></div>' +
			'<div class="modal-body">' +
				'<h4></h4>' +
			'</div>' +
			'<div class="modal-footer">' +
				'<button type="button" class="btn btn-default" data-dismiss="modal"></button>' + 
				'<button type="button" class="btn btn-custom" data-dismiss="modal"></button>' +
			'</div>' +
		'</div></div></div>');

	return {
		/**
		 * Opens our dialog
		 * @param message 	Custom message
		 * @param yesOption Label for yes button
		 * @param noOption	Label for no button
		 * @param options 	Custom options:
		 * 				  	options.dialogSize - bootstrap postfix for dialog size, e.g. "sm", "m";
		 * 					options.yesLabel   - label associated to the confirm button
		 * 					options.yesOnClik  - onClick function associated to the confirm button
		 * 					options.noLabel    - label associated to the cancel button
		 * 					options.noOnClick  - onClick function associated to the cancel button
		 * 					options.onHide     - onHide function associated to the modal
		 */
		show: function (message, options) {
			// Assigning defaults
			if (typeof options === 'undefined') {
				options = {};
			}
			
			if (typeof message === 'undefined') {
				message = 'Loading';
			}
			
			var settings = $.extend({
				dialogSize: 'm',
				title: '',
				yesButton: true,
				yesLabel: 'Yes',
				yesOnClick: null,
				noButton: true,
				noLabel: 'No',
				noOnClick: null,
				onHide: null // This callback runs after the dialog was hidden
			}, options);

			// Configuring dialog
			$dialog.find('.modal-dialog').attr('class', 'modal-dialog').addClass('modal-' + settings.dialogSize);

			$dialog.find('h3').text(settings.title);
			$dialog.find('h4').html(message);
			
			
			
			// Adding callbacks
			if (typeof settings.onHide === 'function') {
				$dialog.off('hidden.bs.modal').on('hidden.bs.modal', function (e) {
					settings.onHide.call($dialog);
				});
			}
			
			if (settings.yesButton === true)
			{
				$dialog.find('.btn-custom').removeClass('hidden');
				$dialog.find('.btn-custom').html(settings.yesLabel);
				
				if (typeof settings.yesOnClick === 'function')
				{
					$dialog.find('.btn-custom').off('click').on('click', function (e) {
						settings.yesOnClick.call($dialog.find('.btn-custom'))
					})
				}
			}
			else
			{
				$dialog.find('.btn-custom').addClass('hidden');
			}
			
			if (settings.noButton === true)
			{
				$dialog.find('.btn-default').removeClass('hidden');
				$dialog.find('.btn-default').html(settings.noLabel);
				
				if (typeof settings.noOnClick === 'function')
				{
					$dialog.find('.btn-default').off('click').on('click', function (e) {
						settings.noOnClick.call($dialog.find('.btn-default'))
					})
				}
			}
			else
			{
				$dialog.find('.btn-default').addClass('hidden');
			}

			// Opening dialog
			$dialog.modal();
		},
		/**
		 * Closes dialog
		 */
		hide: function () {
			$dialog.modal('hide');
		}
	};

})(jQuery);