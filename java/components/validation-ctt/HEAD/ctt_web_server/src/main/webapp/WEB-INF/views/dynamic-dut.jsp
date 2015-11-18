<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!-- Main -->	
<div class="col-xs-12">	    
    <!-- DUTs table -->
	<div class="row">
		<div class="col-xs-12">       
    		<table id="dutTable" class="table table-hover">
 				<thead>
 					<tr>
 						<th>DUT ID</th>
				    	<th>DUT Name</th>
				    	<th>Created</th>
				    	<th>Modified</th>
				    	<th>OEM</th>
				    	<th>Model</th>
				    	<th>Description</th>
				    	<th>Samples</th>
    				</tr>
				</thead>
				<tbody>
					<c:forEach var="dut" items="${dutList}" varStatus="status">
						<tr>
							<td>${dut.idDut}</td>
							<td>${dut.name}</td>
							<td><fmt:formatDate value="${dut.createdDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
							<td><fmt:formatDate value="${dut.modifiedDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
							<td>${dut.manufacturer}</td>
							<td>${dut.model}</td>
							<td>${dut.description}</td>	
							<td><a href="#" class="sample-link">Samples</a></td>				
					   	</tr>
					</c:forEach>
				</tbody>        	
    		</table>
 		</div>
 	</div>

	<!-- Action buttons -->
	<div class="row">
		<div class="col-xs-12">
	   		<button type="button" class="btn btn-default" data-toggle="modal" data-target="#newDutModal"><span class="glyphicon glyphicon-plus"></span> New DUT</button>
	   		<button id="editButton" type="button" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#editDutModal"><span class="glyphicon glyphicon-pencil"></span> Edit DUT</button>   
	   		<button id="deleteButton" type="button" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#delDut"><span class="glyphicon glyphicon-trash"></span> Delete DUT</button>
		</div>
	</div>
      
	<!-- DUT error message -->
	<c:if test="${not empty error}">
		<div class="row" align="center">
			<div class="col-sm-4 col-sm-offset-4">
				<div class="alert alert-danger alert-dismissable" role="alert">
					<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
					<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
					<span class="sr-only">Error:</span>
					${error}
				</div>
			</div>
		</div>
	</c:if>
</div>
 
<!-- Hidden form to next view -->
<div>
	<form:form method="POST" id="nextForm" action="dut/save" modelAttribute="newProject">
		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
		<form:input type="hidden" id="idDut" name="idDut" path="idDut" value=""/>
	</form:form>
</div>

<!-- Modal forms -->
<div>
	<!-- New DUT form -->
	<div id ="newDutModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<form:form method="POST" id="newDutForm" action="dut/add" modelAttribute="newDut">
						<div class="row">
							<div class="col-md-12">
								<div class="form-group">
									<label path="name" for="dut-name" class="control-label">Name (*)</label>
									<form:input path="name" type="text" class="form-control" id="dut-name"/>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label path="manufacturer" for="dut-manufacturer" class="control-label">OEM (*)</label>
									<form:input path="manufacturer" type="text" class="form-control" id="dut-manufacturer"/>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label path="model" for="dut-model" class="control-label">Model (*)</label>
									<form:input path="model" type="text" class="form-control" id="dut-model"/>
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label path="description" for="dut-description" class="control-label">Description</label>
									<form:input path="description" type="text" class="form-control" id="dut-description"/>
								</div>
							</div>
							<div class="col-md-12">
								<h4 align="center">Insert a first sample</h4>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label path="deviceId" for="dut-device-id" class="control-label">Device ID (*)</label>
									<form:input path="deviceId" type="text" class="form-control" id="dut-device-id"/>
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label path="appId" for="dut-app-id" class="control-label">App ID (*)</label>
									<form:input path="appId" type="text" class="form-control" id="dut-app-id"/>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label path="swVer" for="dut-sw-ver" class="control-label">Software Version (*)</label>
									<form:input path="swVer" type="text" class="form-control" id="dut-sw-ver"/>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label path="hwVer" for="dut-hw-ver" class="control-label">Hardware Version</label>
									<form:input path="hwVer" type="text" class="form-control" id="dut-hw-ver"/>
								</div>
							</div>
						</div>
						<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
					</form:form>
					<div class="row">
						<div class="col-md-12">
							<p>(*) This is a mandatory field</p>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button id="createDut" type="submit" class="btn btn-custom">Create DUT</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Edit DUT form -->
	<div id ="editDutModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<form:form method="POST" id="editDutForm" action="dut/edit" modelAttribute="newDut">
						<div class="row">
							<div class="col-md-12">
								<div class="form-group">
									<label path="name" for="edit-name" class="control-label">Name(*)</label>
									<form:input path="name" type="text" class="form-control" id="edit-name"/>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label path="manufacturer" for="edit-manufacturer" class="control-label">OEM(*)</label>
									<form:input path="manufacturer" type="text" class="form-control" id="edit-manufacturer"/>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label path="model" for="edit-model" class="control-label">Model(*)</label>
									<form:input path="model" type="text" class="form-control" id="edit-model"/>
								</div>
							</div>
							<div class="col-md-12">
								<div class="form-group">
									<label path="description" for="edit-description" class="control-label">Description</label>
									<form:input path="description" type="text" class="form-control" id="edit-description"/>
								</div>
							</div>
						</div>
						<form:input type="hidden" id="edit-id" path="idDut"/>
						<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
					</form:form>
					<div class="row">
						<div class="col-md-12">
							<p>(*) This is a mandatory field</p>
						</div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button id="editConfirm" type="submit" class="btn btn-custom">Save Changes</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Delete DUT modal -->
	<div id ="delDut" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<h4>This DUT might have associated projects. Are you sure you want to delete it?</h4>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button id="deleteConfirm" type="button" class="btn btn-custom" data-dismiss="modal">Delete DUT</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Different DUT modal -->
	<div id ="diffDut" class="modal fade" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<h4>This project was configured with a different DUT than selected. If you continue, the project
					configuration will be deleted.</h4>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
					<button id="continueButton" class="btn btn-custom" data-dismiss="modal">Continue</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Samples modal -->
	<div id="samplesDut" class="modal fade" tabindex="-1" role="dialog" data-backdrop="static">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-body">
					<table id="sampleTable" class="table table-hover">
						<thead>
							<tr>
								<th class="hide">Sample ID</th>
								<th>Device ID</th>
								<th>App ID</th>
								<th>Sw Ver</th>
								<th>Hw Ver</th>
						</thead>
						<tbody id="sampleBody">
						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button id="sampleBack" class="btn btn-custom" data-dismiss="modal">Back</button>
					<button id="addSample" class="btn btn-default" data-dismiss="modal" data-toggle="modal" data-target="#newSampleModal">Add Sample</button>
					<button id="editSample" disabled class="btn btn-default disabled" data-dismiss="modal" data-toggle="modal" data-target="#editSampleModal">Edit Sample</button>
					<button id="deleteSample" disabled class="btn btn-default disabled">Delete Sample</button>
				</div>
			</div>
		</div>
	</div>

	<!-- New Sample form -->
	<div id ="newSampleModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<form:form method="POST" id="newSampleForm" action="dut/samples/add" modelAttribute="newSample">
						<div class="form-group">
							<label path="deviceId" for="sample-device-id" class="control-label">Device ID (*)</label>
							<form:input path="deviceId" type="text" class="form-control" id="sample-device-id"/>
						</div>
						<div class="form-group">
							<label path="appId" for="sample-app-id" class="control-label">App ID (*)</label>
							<form:input path="appId" type="text" class="form-control" id="sample-app-id"/>
						</div>
						<div class="form-group">
							<label path="swVer" for="sample-sw-ver" class="control-label">Software Version (*)</label>
							<form:input path="swVer" type="text" class="form-control" id="sample-sw-ver"/>
						</div>
						<div class="form-group">
							<label path="hwVer" for="sample-hw-ver" class="control-label">Hardware Version</label>
							<form:input path="hwVer" type="text" class="form-control" id="sample-hw-ver"/>
						</div>
						<form:input id="sample-dut" type="hidden" name="associatedDut" path="associatedDut" value=""/>			
					</form:form>
					<p>(*) This is a mandatory field</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal" data-toggle="modal" data-target="#samplesDut">Cancel</button>
					<button id="createSample" type="submit" class="btn btn-custom">Add Sample</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Edit Sample form -->
	<div id ="editSampleModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<form:form method="POST" id="editSampleForm" action="dut/samples/edit" modelAttribute="newSample">
						<div class="form-group">
							<label path="deviceId" for="edit-device-id" class="control-label">Device ID (*)</label>
							<form:input path="deviceId" type="text" class="form-control" id="edit-device-id"/>
						</div>
						<div class="form-group">
							<label path="appId" for="edit-app-id" class="control-label">App ID (*)</label>
							<form:input path="appId" type="text" class="form-control" id="edit-app-id"/>
						</div>
						<div class="form-group">
							<label path="swVer" for="edit-sw-ver" class="control-label">Software Version (*)</label>
							<form:input path="swVer" type="text" class="form-control" id="edit-sw-ver"/>
						</div>
						<div class="form-group">
							<label path="hwVer" for="edit-hw-ver" class="control-label">Hardware Version</label>
							<form:input path="hwVer" type="text" class="form-control" id="edit-hw-ver"/>
						</div>
						<form:input id="edit-sample-id" type="hidden" name="idSample" path="idSample" value=""/>			
					</form:form>
	 				<p>(*) This is a mandatory field</p>
	 			</div>
	 			<div class="modal-footer">
	 				<button type="button" class="btn btn-default" data-dismiss="modal" data-toggle="modal" data-target="#samplesDut">Cancel</button>
	 				<button id="editSampleConfirm" type="submit" class="btn btn-custom">Save Changes</button>
	 			</div>
	 		</div>
	 	</div>
	</div>
</div>
		
<script>
	$(document).ready(function()
	{
		jQuery.getScript('resources/js/dynamic-dut.js', function() {
			duts.init();
		});
	});
</script>