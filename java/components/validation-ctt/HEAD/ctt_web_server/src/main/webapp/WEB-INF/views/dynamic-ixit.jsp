<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!-- Main -->
<div class="col-xs-12">
	<!-- IXIT Table -->
	<div class="row">
		<div class="col-xs-12">
	 		<!-- Service tabs -->
	  		<ul class="nav nav-tabs">
	  			<c:forEach var="service" items="${serviceList}" varStatus="status">
	  				<c:choose>
						<c:when test="${service.idService==1}">
							<li class="active"><a href="#${service.idService}" data-toggle="tab">${service.name}</a></li>
						</c:when>
						<c:otherwise>
							<li><a href="#${service.idService}" data-toggle="tab">${service.name}</a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
	  		</ul>
	  
			<!-- Service tables -->
			<div class="tab-content">
				<c:forEach var="service" items="${serviceList}" varStatus="status">
			   		<div class="tab-pane" id="${service.idService}">
			    		<table class="table table-hover">
			   				<thead>
			   					<tr>
							      	<th width="3%">Id</th>
							      	<th width="25%">Name</th>
							      	<th width="55%">Description</th>
							      	<th width="17%">Value</th>
			      				</tr>
			  				</thead>
			    			<tbody>
								<c:forEach var="ixit" items="${ixitList}" varStatus="status">
									<c:if test="${ixit.serviceGroup == service.idService}">
										<tr>
								       		<td width="3%">${ixit.idIxit}</td>
								       		<td width="25%">${ixit.name}</td>
											<td width="55%">${ixit.description}</td>
											<td width="17%">
												<input style="width: 100%" type="text" class="form-control" maxlength="255" value="${ixit.value}"/>
											</td>								
								       	</tr>
									</c:if>
								</c:forEach>
							</tbody>        	
			    		</table>
			  		</div>
			    </c:forEach>
			</div>
		</div>
	</div>
</div>
    	
<!-- Hidden form to previous view -->
<div>
	<form:form method="GET" id="prevForm" action="ics" modelAttribute="newProject">
		<form:input type="hidden" id="idProjectPrev" name="idProject" path="idProject" value=""/>
	</form:form>
</div>

<!-- Hidden form to next view -->
<div>
	<form:form method="GET" id="nextForm" action="parameter" modelAttribute="newProject">
		<form:input type="hidden" id="idProjectNext" name="idProject" path="idProject" value=""/>
	</form:form>
</div>

<script>
	$(document).ready(function()
	{
		jQuery.getScript('resources/js/dynamic-ixit.js', function() {
			ixits.init();
		})
	});
</script>