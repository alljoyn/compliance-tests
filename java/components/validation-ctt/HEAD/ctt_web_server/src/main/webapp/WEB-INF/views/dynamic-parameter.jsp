<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!-- Main -->
<div class="col-xs-12">
   	<!-- Parameters Table -->
   	<div class="row">
   		<div class="col-xs-12">
    		<!-- Service tables -->
			<table id ="table" class="table table-hover">
	    		<thead>
	    			<tr>
				       	<th width="3%">Id</th>
				       	<th width="30%">Name</th>
				       	<th width="57%">Description</th>
				       	<th width="10%">Value</th>
					</tr>
				</thead>
	     		<tbody>
					<c:forEach var="parameter" items="${listParameter}" varStatus="status">
						<tr>
			        		<td width="3%">${parameter.idParam}</td>
			        		<td width="30%">${parameter.name}</td>
							<td width="57%">${parameter.description}</td>
							<td width="10%">
								<input style="width: 100%" type="number" min="1" class="form-control" value="${parameter.value}"/>
							</td>								
				        </tr>
					</c:forEach>
				</tbody>        	
	   		</table>
	   	</div>
	</div>
</div>
    	
<!-- Hidden form to previous view -->
<div>
	<form:form method="GET" id="prevForm" action="ixit" modelAttribute="newProject">
   		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
   	</form:form>
</div>

<script>
	$(document).ready(function()
	{
		jQuery.getScript('resources/js/dynamic-parameter.js', function() {
			parameters.init();
		});
	});
</script>