/**
 * Creates `rowspan` cells in all a entire column. 
 * 
 * **Note** - this plug-in currently only operates correctly with 
 * **server-side processing**.
 *
 *  @name fnFakeRowspanAll
 *  @summary Create a rowspan for cells which share data
 *  @author Fredrik Wendel
 *  @author Modify for Cristian Mena
 *
 *  @param {interger} iColumn Column index to have row span
 *  @param {boolean} [bCaseSensitive=true] If the data check should be case
 *    sensitive or not.
 *  @returns {jQuery} jQuery instance
 *
 *  @example
 *    $('#example').dataTable().fnFakeRowspan();
 */

jQuery.fn.dataTableExt.oApi.fnFakeRowspanAll = function ( oSettings, iColumn,key, bCaseSensitive ) {

	/* Fail silently on missing/errorenous parameter data. */
	if (isNaN(iColumn)) {
		return false;
	}

	if (iColumn < 0 || iColumn > oSettings.aoColumns.length-1) {
		alert ('Invalid column number choosen, must be between 0 and ' + (oSettings.aoColumns.length-1));
		return false;
	}

	bCaseSensitive = (typeof(bCaseSensitive) != 'boolean' ? true : bCaseSensitive);
	
	function fakeRowspanAll () {
		var firstOccurance = null,
			value = null,
			rowspan = 0;
		
		jQuery.each(oSettings.aoData, function (i, oData) {
			 var val = null;
			 
			 if( oData._aData instanceof Array)
			 {
			 	 val = oData._aData[iColumn],
			 	 	cell = oData.nTr.childNodes[iColumn];
			 }
			 else
			 {			
					
				  val = oData._aData[oSettings.aoColumns[iColumn].mData],
					 	cell = oData.nTr.childNodes[iColumn];
			 }
			
						
		
			/* Use lowercase comparison if not case-sensitive. */
			if (!bCaseSensitive) {
				val = val.toLowerCase();
			}
			/* Reset values on new cell data. */
			if (val != value) {
				value = val;
				firstOccurance = cell;
				rowspan = 0;
			}

			if (val == value) {
				rowspan++;
			}

			if (firstOccurance !== null && val == value && rowspan > 1) {
				oData.nTr.removeChild(cell);
				firstOccurance.rowSpan = rowspan;
			}
		});
	}

	oSettings.aoDrawCallback.push({ "fn": fakeRowspanAll, "sName": "fnFakeRowspanAll" });

	return this;
};
