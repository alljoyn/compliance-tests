<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<!-- Main -->
<div class="col-xs-12">
	<!-- ICS table -->
	<div class="row">
		<div class="col-xs-12">
			<!-- Service tabs -->
			<ul class="nav nav-tabs">
				<c:forEach var="service" items="${serviceList}" varStatus="status">
					<c:choose>
						<c:when test="${service.idService==1}">
							<li class="active">
								<a href="#${service.idService}" data-toggle="tab">${service.name}  <span id="badge${service.idService}" style="color:#000000; background-color:#F2DEDE;" class="badge"></span></a>
							</li>
						</c:when>
						<c:otherwise>
							<li>
								<a href="#${service.idService}" data-toggle="tab">${service.name}  <span id="badge${service.idService}"style="color:#000000; background-color:#F2DEDE;" class="badge"></span></a>
							</li>
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
							    	<th>Id</th>
							    	<th>Name</th>
							    	<th>Description</th>
							    	<th>Value</th>
			    				</tr>
							</thead>
			  				<tbody>
								<c:forEach var="ics" items="${icsList}" varStatus="status">
									<c:if test="${ics.serviceGroup==service.idService}">
										<tr>
								     		<td>${ics.id}</td>
								     		<td>${ics.name}</td>
											<td>${ics.description}</td>
											<td>
												<select class="form-control">
													<option value="${ics.value}">${ics.value}</option>
													<option value="${ics.value==false}">${ics.value==false}</option>
												</select>
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

	<div class="row asa-right-label-row">
		<div class="col-xs-12">
			<p id="scrNeed">You need to perform SCR verification</p>
			<p hidden="true" id="fixErrors">You need to correct invalid ICS values</p>
		</div>
	</div>

	<!-- Navigation and SCR buttons -->
	<div class="row">
		<div class="col-xs-12">
			<button id="changeButton" type="button" class="btn btn-default">Change All Service ICS</button>
			<button id="scrButton" type="button" class="btn btn-default" data-toggle="modal" data-target="#pleaseWaitDialog">SCR</button>
		</div>
	</div>
</div>

<!-- Hidden form to next view -->
<div>
	<form:form method="GET" id="nextForm" action="ixit" modelAttribute="newProject">
		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
		<form:input type="hidden" id="idDut" name="idDut" path="idDut" value=""/>
	</form:form>
</div>

<!-- Hidden form to previous view -->
<div>
	<form:form method="GET" id="prevForm" action="ics/decide" modelAttribute="newProject">
		<form:input type="hidden" id="prevIdProject" name="idProject" path="idProject" value=""/>
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
		jQuery.getScript('resources/js/dynamic-ics.js', function() {
			ics.init();
		});
	});
</script>