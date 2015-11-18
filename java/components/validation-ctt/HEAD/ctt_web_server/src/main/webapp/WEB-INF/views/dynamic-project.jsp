<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!-- Main -->
<div class="col-xs-12">   
	<!-- Projects table -->
    <div class="row">
       <div class="col-xs-12">      
	       	<table id="table" class="table table-hover">
	       		<thead>
	       			<tr>
	       				<th></th>
			        	<th class="hide">Project ID</th>
			        	<th>Project Name</th>
			        	<th>Modified</th>
			        	<th>Type</th>
			        	<th>Certification Release</th>
			        	<th>Supported Services</th>
			        	<th>Configured</th>
			        	<th class="all">Results</th>
			        	<th>Created</th>
			        	<th>TCCL</th>
			        	<th>DUT</th>
			        	<th>GU</th>
			        	<th>CRI</th>	 	
			        </tr>
			    </thead>
	        	<tbody>
				</tbody>        	
	       	</table>
		</div>
   	</div>

	<!-- Action buttons -->
	<div class="row" align="left">
		<div class="col-xs-12">
			<button id="createButton" type="button" class="btn btn-default" data-toggle="modal" data-target="#newProjectModal"><span class="glyphicon glyphicon-plus"></span> New Project</button>
       		<button id="editButton" type="button" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#editProjectModal"><span class="glyphicon glyphicon-pencil"></span> Edit Project</button>
       		<button id="deleteButton" type="button" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#delProjectModal"><span class="glyphicon glyphicon-trash"></span> Delete Project</button>
		</div>
    </div>
      
	<!-- Error message -->
	<c:if test="${not empty error}">
	<div class="row" align="center">
		<div class="col-sm-4 col-sm-offset-4">
			<div class="alert alert-danger alert-dismissable" role="alert">
				<button type="button" class="close" data-dismiss="alert">&times;</button>
				<span class="glyphicon glyphicon-exclamation-sign"></span>
				<span class="sr-only">Error:</span>
				${error}
			</div>
		</div>
	</div>
	</c:if>
</div>
     
<!-- Hidden form to results view -->
<div>
	<form:form method="GET" id="resultForm" action="results" modelAttribute="newProject">
		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
	</form:form>
</div>
       
<!-- Modal forms -->
<div>
	<!-- New project form -->
	<div id ="newProjectModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<form:form method="POST" id="newProjectForm" action="project/add" modelAttribute="newProject">
						<div class="form-group">
							<label for="projectname" class="control-label">Name (*)</label>
							<form:input path="name" type="text" class="form-control" id="projectname" name="projectname"/>
						</div>
						<div class="form-group">
							<label for="message-text" class="control-label">Type</label>
							<form:select path="type" class="form-control" id="project-type">
								<option value="Conformance">Conformance</option>
								<option value="Interoperability">Interoperability</option>
								<option value="Conformance and Interoperability">Conformance and Interoperability</option>
								<option value="Development">Development</option>
							</form:select>
						</div>
						<div class="form-group">
							<label for="project-release" class="control-label">Certification Release Core Version</label>
							<form:select path="idCertrel" class="form-control" id="project-release"></form:select>
						</div>
						<div class="form-group">
							<label for="project-tccl" class="control-label">Test Case Control List</label>
							<form:select path="idTccl" class="form-control" id="project-tccl"></form:select>
						</div>
						<div class="form-group">
							<label for="project-car-id" class="control-label">Certification Application Request ID (*)</label>
							<form:input path="carId" type="text" class="form-control" id="project-car-id" name="project-car-id"/>
						</div>
						<form:input type="hidden" id="supportedServices" name="supportedServices" path="supportedServices" value=""/>
						<div class="form-group">
							<label class="control-label">Supported Services</label>
							<select id="new-project-services" class="form-control selectpicker" multiple>
							</select>
						</div>
						<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
					</form:form>
					<p>(*) This is a mandatory field</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
					<button id="createConfirm" type="submit" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Create project</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Edit project form -->
	<div id ="editProjectModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<form:form method="POST" id="editProjectForm" action="project/save" modelAttribute="newProject">
						<form:input type="hidden" id="edit_id" path="idProject"/>
						<div class="form-group">
							<label for="edit_name" class="control-label">Name (*)</label>
							<form:input path="name" type="text" class="form-control" id="edit_name"/>
						</div>
						<div class="form-group">
							<label for="edit-type" class="control-label">Type</label>
							<form:select path="type" class="form-control" id="edit-type">
								<option value="Conformance">Conformance</option>
								<option value="Interoperability">Interoperability</option>
								<option value="Conformance and Interoperability">Conformance and Interoperability</option>
								<option value="Development">Development</option>
							</form:select>
						</div>
						<div class="form-group">
							<label for="edit-release" class="control-label">Certification Release Core Version</label>
							<form:select path="idCertrel" class="form-control" id="edit-release"></form:select>
						</div>
						<div class="form-group">
							<label for="edit-tccl" class="control-label">Test Case Control List</label>
							<form:select path="idTccl" class="form-control" id="edit-tccl"></form:select>
						</div>
						<div class="form-group">
							<label for="edit-car-id" class="control-label">Certification Application Request ID (*)</label>
							<form:input path="carId" type="text" class="form-control" id="edit-car-id"/>
						</div>
						<form:input type="hidden" id="edit-services" name="supportedServices" path="supportedServices" value=""/>
						<div class="form-group">
							<label class="control-label">Supported Services</label>
							<select class="form-control selectpicker" id="scroll-services" multiple>
							</select>
						</div>
						<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
					</form:form>
					<p>(*) This is a mandatory field</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
					<button id="editConfirm" type="submit" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Save changes</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Delete project form -->
	<div id ="delProjectModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<h4>Are you sure you want to delete this project?</h4>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
					<button id="deleteConfirm" type="button" class="btn btn-custom" data-dismiss="modal"><span class="glyphicon glyphicon-ok"></span> Delete project</button>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- Document ready script -->
<script>
	$(document).ready(function()
	{
		jQuery.getScript('resources/js/dynamic-project.js', function() {
			projects.init();
		});
	});
</script>