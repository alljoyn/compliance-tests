<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!-- Main -->	
<div class="col-xs-12">			    
	<!-- Results table -->
	<div class="row"> 
		<div class="col-xs-12">     
	 		<table id="table" class="table table-hover">
		 		<thead>
	 				<tr>
	    				<th width="20%">Name</th>
				    	<th width="50%">Description</th>
				    	<th width="10%">Date and Time Execution</th>
				    	<th width="10%">Certification Release</th>
				    	<th width="10%">Final Verdict</th>
				    	<th class="hide">Full Log</th>
	    			</tr>
				</thead>
				<tbody>
					<c:forEach var="result" items="${listTCResult}" varStatus="status">
						<tr>
							<td width="20%">${result.name}</td>
							<td width="50%">${result.description}</td>
							<td width="10%">${result.execTimestamp}</td>
							<td width="10%">${result.version}</td>
							<c:if test="${result.verdict=='PASS'}">
								<td style="font-weight: bold; color: #99CC32"width="10%">${result.verdict}</td>
							</c:if>
							<c:if test="${result.verdict=='FAIL' || result.verdict=='NO SUCH TESTCASE'}">
								<td style="font-weight: bold; color: #D43D1A"width="10%">${result.verdict}</td>
							</c:if>
							<c:if test="${result.verdict=='INCONC'}">
								<td style="font-weight: bold; color: #EEC900" width="10%">${result.verdict}</td>
							</c:if>
							<td class="hide">${result.log}</td>
						</tr>
					</c:forEach>
				</tbody>        	
	    	</table>
    	</div>
    </div>

	<!-- Action buttons -->
	<div class="row" align="left">
		<div class="col-xs-12">
    		<span id="createTRTooltip" class="helper" data-toggle="tooltip" title="Please execute all applicable Test Cases to be able to create the Test Results document">
    			<button class="btn btn-default disabled" id="createTR"><span class="glyphicon glyphicon-file"></span> Create Test Results</button>
    		</span>
    		<span id="sendTRTooltip" class="helper" data-toggle="tooltip" title="Test Results document is not created, please create it">
      			<button class="btn btn-default disabled" id="sendTR"><span class="glyphicon glyphicon-cloud-upload"></span> Send Test Results Package</button>
      		</span>
      		<span id="viewTRTooltip" class="helper" data-toggle="tooltip" title="Test Results document is not created, please create it">
      			<button class="btn btn-default disabled" id="viewTR"><span class="glyphicon glyphicon-eye-open"></span> View Test Results</button>
      		</span>
		</div>
	</div>
</div>

<script>
	$(document).ready(function()
	{
		jQuery.getScript('resources/js/dynamic-results.js', function()
		{
			results.init();
		});
	});
</script>