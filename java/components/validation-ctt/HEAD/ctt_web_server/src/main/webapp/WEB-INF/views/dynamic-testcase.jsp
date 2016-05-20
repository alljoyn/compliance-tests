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
	      		<tbody>
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
		
<script>	
	$(document).ready(function()
	{
		jQuery.getScript('resources/js/dynamic-testcase.js', function()
		{
			testcases.init();
		});
	});
</script>