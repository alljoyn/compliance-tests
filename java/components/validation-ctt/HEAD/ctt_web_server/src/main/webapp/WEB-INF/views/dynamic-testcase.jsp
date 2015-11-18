<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
				
<!-- Main -->
<div class="col-xs-12">
	<!-- Test Cases table -->
    <div class="row">
    	<div class="col-xs-12">
	    	<table id="tcTable" class="table table-hover">
	     		<thead>
	     			<tr>
	     				<th>TC ID</th>
			        	<th>Test Case</th>
			        	<th>Description</th>
			        	<th>Select</th>
	        		</tr>
	    		</thead>
	      		<tbody id="tcBody">
				</tbody>        	
	      	</table>
      	</div>
	</div>

	<!-- Action buttons -->
	<div class="row" align="left" style="margin-top: 10px">
		<div class="col-xs-12">
      		<button id="selectAll" type="button" class="btn btn-default"><span class="glyphicon glyphicon-check"></span> Select All (*)</button>
      		<button id="deselectAll" type="button" class="btn btn-default"><span class="glyphicon glyphicon-unchecked"></span> Deselect All</button>
      	</div>
	</div>
      
	<div class="row">
		<div class="col-xs-12">
    		<p>(*) Mandatory for Certification purpose</p>
    	</div>
	</div>
</div>
    
<!-- Hidden form to previous view -->
<div>
	<form:form method="GET" id="prevForm" action="parameter" modelAttribute="newProject">
		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
	</form:form>
</div>
    
<!-- Processing... modal -->	
<div class="modal" tabindex="-1" role="dialog" aria-hidden="true" id="pleaseWaitDialog" data-backdrop="static" data-keyboard="false">
	<div class="modal-dialog">
    	<div class="modal-content">
			<div class="modal-header">
       			<h1>Processing...</h1>
       		</div>
       		<div class="modal-body">
       			<div class="progress progress-striped active">
       				<div class="progress-bar" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width:100%"></div>
       			</div>
       		</div>
		</div>
	</div>
</div>
		
<script>	
	$(document).ready(function()
	{
		jQuery.getScript('resources/js/dynamic-testcase.js', function() {
			testcases.init();
		});
	});
</script>