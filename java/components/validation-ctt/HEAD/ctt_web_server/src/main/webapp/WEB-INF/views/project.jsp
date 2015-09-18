<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html lang="en">
    <head>
    	<jsp:include page="/WEB-INF/views/page_head.jsp"/>
    	
    	<!-- Selectpicker -->
		<link rel="stylesheet" type="text/css" href="resources/bootstrap-select/css/bootstrap-select.min.css"/>
    </head>
    <body>
    
  		<!-- CSRT for logout -->
    	<c:url value="/j_spring_security_logout" var="logoutUrl" />
 
		<form action="${logoutUrl}" method="post" id="logoutForm">
		  <input type="hidden" 
			name="${_csrf.parameterName}"
			value="${_csrf.token}" />
		</form>
		
		<!-- Main -->
    	<div class="container">
    		<jsp:include page="/WEB-INF/views/header.jsp"/>
		    
		    <div class="row" align="right">
		    	<button id="apiKey" type="button" class="btn btn-default pull-left">Generate CTT Local Agent Password</button>
		    	<a id="downloadLocalAgent" type="button" class="btn btn-default pull-left" href="end/download">Download CTT Local Agent</a>	
		    	<c:if test="${pageContext.request.userPrincipal.name != null}">
					<h4>
						Welcome : ${pageContext.request.userPrincipal.name} | <a
						href="javascript:formSubmit()"> Logout</a>
					</h4>
				</c:if>
		    </div>
		    
		    <!-- Projects table -->
	        <div class="row">
	        	<div>       
			       	<table id="table" class="table table-hover hide dt-responsive">
			       		<thead>
			       			<tr>
					        	<th class="hide">Project ID</th>
					        	<th></th>
					        	<th>Project Name</th>
					        	<th>Modified</th>
					        	<th>Type</th>
					        	<th>Certification Release</th>
					        	<th>Supported Services</th>
					        	<th>Configured</th>
					        	<th>Results</th>
					        	<th>Created</th>
					        	<th>TCCL</th>
					        	<th>DUT</th>
					        	<th>GU</th>
					        	<th>CRI</th>	 	
					        </tr>
					    </thead>
			        	<tbody>
							<c:forEach var="project" items="${projectList}" varStatus="status">
							<tr>
				        		<td class="hide">${project.idProject}</td>
				        		<td></td>
				        		<td>${project.name}</td>
				        		<td><fmt:formatDate value="${project.modifiedDate}"
									pattern="yyyy-MM-dd HH:mm:ss"/></td>
				        		<td>${project.type}</td>
				        		<c:forEach var="certrel" items="${certrelList}" varStatus="status">
				        			<c:if test="${certrel.idCertrel==project.idCertrel}">
				        				<td>${certrel.name}</td>
				        			</c:if>
				        		</c:forEach>
								<td>${project.supportedServices}</td>
								<c:choose>
									<c:when test="${project.isConfigured}">
										<td>Yes</td>
									</c:when>
									<c:otherwise>
										<td>No</td>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${project.hasResults}">
										<td><a href="#" class="result-link">Link to results</a></td>
									</c:when>
									<c:otherwise>
										<td>No results</td>
									</c:otherwise>
								</c:choose>	
				        		<td><fmt:formatDate value="${project.createdDate}"
									pattern="yyyy-MM-dd HH:mm:ss"/></td>
								<c:choose>
									<c:when test="${project.type == 'Development'}">
										<td>Not needed</td>
									</c:when>
									<c:otherwise>
										<c:forEach var="tccl" items="${tcclList}" varStatus="status">
											<c:if test="${tccl.idTccl==project.idTccl}">
												<td>${tccl.name}</td>
											</c:if>
										</c:forEach>
									</c:otherwise>
								</c:choose>
								<c:choose>
									<c:when test="${project.idDut!=0}">
										<c:forEach var="dut" items="${dutList}" varStatus="status">
											<c:if test="${dut.idDut==project.idDut}">
												<td>${dut.name}</td>
											</c:if>
										</c:forEach>
									</c:when>
									<c:otherwise>
										<td>Not selected</td>
									</c:otherwise>
								</c:choose>
								<td>${project.gUnits}</td>
								<c:choose>
									<c:when test="${project.type == 'Development'}">
										<td>Not needed</td>
									</c:when>
									<c:otherwise>
										<td>${project.carId}</td>
									</c:otherwise>
								</c:choose>								
				        	</tr>
							</c:forEach>
						</tbody>        	
			       	</table>
			    </div>
		     </div>
		
			<!-- Action buttons -->
			<div class="row" align="left">
	        	<button id="createButton" type="button" class="btn btn-default btn-lg" data-toggle="modal" data-target="#newProjectModal">New Project</button>
	        	<button id="editButton" type="button" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#editProjectModal">Edit Project</button>
	        	<button id="deleteButton" type="button" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#delProjectModal">Delete Project</button>
	        </div>
	        
	        <!-- Navigation buttons -->
	        <div class="row" align="left">
	        	<a id="nextButton" type="button" disabled class="btn btn-custom btn-lg disabled pull-right" href="dut">Next Step »</a>
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
	        						<form:select path="idCertrel" class="form-control" id="project-release">
	        							<!--<c:forEach var="certrel" items="${certrelList}" varStatus="status">
											<c:choose>
												<c:when test="${not status.last}">
													<option value="${certrel.idCertrel}">${certrel.name}</option>
												</c:when>
												<c:otherwise>
													<option value="${certrel.idCertrel}" selected>${certrel.name}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach> -->
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label for="project-tccl" class="control-label">Test Case Control List</label>
	        						<form:select path="idTccl" class="form-control" id="project-tccl">
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label for="project-car-id" class="control-label">Certification Application Request ID (*)</label>
	        						<form:input path="carId" type="text" class="form-control" id="project-car-id"/>
	        					</div>
	        					<form:input type="hidden" id="supportedServices" name="supportedServices" path="supportedServices" value=""/>
	        					<div class="form-group">
	        						<label class="control-label">Supported Services</label>
	        						<select id="new-project-services" class="form-control selectpicker" multiple>
		        						<c:forEach var="service" items="${serviceList}" varStatus="status">
											<c:choose>
												<c:when test="${not status.first}">
													<option value="${service.idService}">${service.name}</option>
												</c:when>
												<c:otherwise>
													<option value="${service.idService}" selected disabled>${service.name}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select>
	        					</div>
	        					<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
	        				</form:form>
	        				<p>(*) This is a mandatory field</p>
	        			</div>
	        			<div class="modal-footer">
	        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        				<button id="createConfirm" type="submit" class="btn btn-custom">Create project</button>
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
	        						<form:select path="idCertrel" class="form-control" id="edit-release">
	        							<!--<c:forEach var="certrel" items="${certrelList}" varStatus="status">
											<option value="${certrel.idCertrel}">${certrel.name}</option>
										</c:forEach> -->
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label for="edit-tccl" class="control-label">Test Case Control List</label>
	        						<form:select path="idTccl" class="form-control" id="edit-tccl">
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label for="edit-car-id" class="control-label">Certification Application Request ID (*)</label>
	        						<form:input path="carId" type="text" class="form-control" id="edit-car-id"/>
	        					</div>
	        					<form:input type="hidden" id="edit-services" name="supportedServices" path="supportedServices" value=""/>
	        					<div class="form-group">
	        						<label class="control-label">Supported Services</label>
	        						<select class="form-control selectpicker" id="scroll-services" multiple>
		        						<c:forEach var="service" items="${serviceList}" varStatus="status">
		        							<c:choose>
												<c:when test="${not status.first}">
													<option value="${service.idService}">${service.name}</option>
												</c:when>
												<c:otherwise>
													<option value="${service.idService}" disabled>${service.name}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</select>
	        					</div>
	        					<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
	        				</form:form>
	        				<p>(*) This is a mandatory field</p>
	        			</div>
	        			<div class="modal-footer">
	        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        				<button id="editConfirm" type="submit" class="btn btn-custom">Save changes</button>
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
	        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        				<button id="deleteConfirm" type="button" class="btn btn-custom" data-dismiss="modal">Delete project</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
	        
	        <!-- Generated password modal -->
	        <div id="generatedKey" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	        	<div class="modal-dialog">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<h4>Your CTT Local Agent Password is:</h4>
	        				<h4 id="key"></h4>
	        				<h4>Please copy it because it cannot be retrieved</h4>
	        			</div>
	        			<div class="modal-footer">
	        				<button id="closeApiKey" type="button" class="btn btn-default" data-dismiss="modal">Close</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
 
        <script src="resources/bootstrap-select/js/bootstrap-select.min.js"></script>
		
		<script src="resources/jquery-validation/1.13.1/js/jquery.validate.min.js"></script>
		<script src="resources/jquery-validation/1.13.1/js/additional-methods.min.js"></script>
		
		<!-- Document ready script -->
		<script>
			var table;
			
			$(document).ready(function() {
				$('#title').text("STEP 1: SELECT/EDIT/CREATE A PROJECT OR VIEW RESULTS");
				$('.selectpicker').selectpicker();
				sessionStorage.clear();
						
				$('#table').removeClass('hide');
				
				table = $('#table').dataTable({
					//autoWidth: false,
					pagingType: 'full_numbers',
					scrollY: ($(window).height()/2),
					responsive: {
						details: {
							type: 'column',
							target: 1
						}
					},
					columnDefs: [
					    { className: 'none', searchable: false, targets: [9,10,11,12,13]},         
						{ className: 'control', orderable: false, targets: 1},
					],
					order: [2, 'asc']		
				});
			});
			
			$(window).resize(function() {
				table.fnAdjustColumnSizing();
			});
			
			function compare(a,b) {
				if (a.name < b.name)
					return -1;
				if (a.name > b.name)
					return 1;
				return 0;
			}
		</script>
		
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				$('#logoutForm').submit();
			}
		</script>
		
		<!-- Selector scripts -->
		<script>
			$('#project-type').change(function() {
				var selected = $(this).find("option:selected").val();
				
				$.ajax({
					cache: false,
					type: 'GET',
					url: 'project/loadCertRel',
					data : {
						pjType : selected
					},
					success: function(data) {
						var selectedCR;
						
						data.sort(compare);
						
						$('#project-release').empty();
						$.each(data, function(i, release) {
							if (i == data.length - 1) {
								$('#project-release').append("<option selected value=\""+release.idCertrel+"\">"+release.name+": ("+release.description+")</option>");
								selectedCR = release.idCertrel;
							} else {
								$('#project-release').append("<option value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
							}
						});
						
						if (selected != "Development")
						{
							$.ajax({
								cache: false,
								type: 'GET',
								url: 'project/loadTccl',
								data : {
									idCertRel : selectedCR
								},
								success: function(data) {
									$('#project-tccl').empty();
									$('#project-tccl').prop('disabled', false);
									document.getElementById('project-car-id').value="";
									$('#project-car-id').prop('disabled', false);
									$.each(data, function(i, tccl) {
										$('#project-tccl').append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
									});
								}
							});
						} else {
							$('#project-tccl').empty();
							$('#project-tccl').prop('disabled', true);
							document.getElementById('project-car-id').value="";
							$('#project-car-id').prop('disabled', true);
						}
					}
				});
			});
			
			$('#project-release').change(function() {
				var projectSelected = $('#project-type').find("option:selected").val();
				
				if (projectSelected != "Development")
				{
					var selected = $(this).find("option:selected").val();
					
					$.ajax({
						cache: false,
						type: 'GET',
						url: 'project/loadTccl',
						data : {
							idCertRel : selected
						},
						success: function(data) {
							$('#project-tccl').empty();
							$.each(data, function(i, tccl) {
								$('#project-tccl').append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
							});
						}
					});
				}
			});
			
			$('#edit-type').change(function() {
				var selected = $(this).find("option:selected").val();
				
				$.ajax({
					cache: false,
					type: 'GET',
					url: 'project/loadCertRel',
					data : {
						pjType : selected
					},
					success: function(data) {
						var selectedCR;
						
						data.sort(compare);
						
						$('#edit-release').empty();
						$.each(data, function(i, release) {
							if (i == data.length -1) {
								$('#edit-release').append("<option selected value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
								selectedCR = release.idCertrel;
							} else {
								$('#edit-release').append("<option value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
							}
						});
						
						if (selected != "Development")
						{
							$.ajax({
								cache: false,
								type: 'GET',
								url: 'project/loadTccl',
								data : {
									idCertRel : selectedCR
								},
								success: function(data) {
									$('#edit-tccl').empty();
									$('#edit-tccl').prop('disabled', false);
									document.getElementById('edit-car-id').value="";
									$('#edit-car-id').prop('disabled', false);
									$.each(data, function(i, tccl) {
										$('#edit-tccl').append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
									});
								}
							});
						} 
						else
						{
							$('#edit-tccl').empty();
							$('#edit-tccl').prop('disabled', true);
							document.getElementById('edit-car-id').value="";
							$('#edit-car-id').prop('disabled', true);
						}
					}
				});
			});
			
			$('#edit-release').change(function() {
				var projectSelected = $('#edit-type').find("option:selected").val();
				
				if (projectSelected != "Development")
				{
					var selected = $(this).find("option:selected").val();
					
					$.ajax({
						cache: false,
						type: 'GET',
						url: 'project/loadTccl',
						data : {
							idCertRel : selected
						},
						success: function(data) {
							$('#edit-tccl').empty();
							$.each(data, function(i, tccl) {
								$('#edit-tccl').append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
							});
						}
					});
				}
			});
		</script>
		
		<!-- Button scripts -->
		<script>
			function enableButtons(enableNext) {	
				$('#deleteButton').removeClass('disabled');
				$('#deleteButton').prop("disabled", false);
				$('#editButton').removeClass('disabled');
				$('#editButton').prop("disabled", false);
				if(enableNext) {
					$('#nextButton').removeClass('disabled');
				   	$('#nextButton').removeAttr("disabled", false);
				}
			}
			
			function disableButtons() {
				$('#nextButton').addClass('disabled');
			   	$('#nextButton').attr("disabled", true);
				$('#deleteButton').addClass('disabled');
				$('#deleteButton').prop("disabled", true);
				$('#editButton').addClass('disabled');
				$('#editButton').prop("disabled", true);
			}
			
			$('#apiKey').on('click', function() {
				
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
				
				$.ajax({
					type : 'POST',
					url : 'project/generateKey',
					beforeSend: function(xhr) {
			            xhr.setRequestHeader(header, token);
			        },
					success : function (data) {
						$('#key').empty();
						$('#key').append(data);
						$('#generatedKey').modal('show');
					}
				});	
			});
			
			$('#closeApiKey').on('click', function() {
				$('#key').empty();
			});
			
		  	$('#createConfirm').on('click', function(){
		  		
				var text="";
		    	
		    	$('#new-project-services option:selected').each(function(index){
		    		text+=$(this).val()+".";
		    	});
		    	
		    	$('#supportedServices').val(text);
		    	$('#newProjectForm').submit();
		  	});

			$('#deleteConfirm').on('click', function(e){
				
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
			    
			    $.ajax({
						type : 'POST',
						url : 'project/delete',
						beforeSend: function(xhr) {
				            xhr.setRequestHeader(header, token);
				        },
						data : {
							idProject : sessionStorage.getItem("idProject")
						},
						success: function () {
							var MyRows = $('.table').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idProject")) {
									var table = $('#table').DataTable();
									table.row($(MyRows[i])).remove().draw();
									/*$(MyRows[i]).fadeOut(400, function() {
										$(MyRows[i]).remove();
									});*/
								}
							}
							
							sessionStorage.removeItem("idProject");
							disableButtons();
					   }
				});
			});
			
			$('#editConfirm').on('click', function(e) {
				e.preventDefault();
				
				var text="";
		    	
		    	$('#scroll-services option:selected').each(function(index){
		    		text+=$(this).val()+".";
		    	});
		    	
		    	$('#edit-services').val(text);
				$('#editProjectForm').submit();
			});
			
			$('#createButton').on('click', function(){	
				$.ajax({
					cache: false,
					type: 'GET',
					url: 'project/loadCertRel',
					data : {
						pjType : "Conformance"
					},
					success: function(data) {
						var selectedCR;
						
						data.sort(compare);
						
						$('#project-release').empty();
						$.each(data, function(i, release) {
							if (i == data.length -1) {
								$('#project-release').append("<option selected value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
								selectedCR = release.idCertrel;
							} else {
								$('#project-release').append("<option value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
							}
						});
						
						$.ajax({
							cache: false,
							type: 'GET',
							url: 'project/loadTccl',
							data : {
								idCertRel : selectedCR
							},
							success: function(data) {
								$('#project-tccl').empty();
								$.each(data, function(i, tccl) {
									$('#project-tccl').append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
								});
							}
						});
					}
				});
			});
			
			$('#editButton').on('click', function(){
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'project/edit',
					data : {
						idProject : sessionStorage.getItem("idProject")
					},
					success: function (data) {
						
						$('#edit_id').val(data.idProject);
						$('#edit_name').val(data.name);
						$('#edit-type').val(data.type);
						$('#edit-car-id').val(data.carId);
						
						$.ajax({
							cache: false,
							type: 'GET',
							url: 'project/loadCertRel',
							data : {
								pjType : data.type
							},
							success: function(releases) {
								releases.sort(compare);
								
								$('#edit-release').empty();
								$.each(releases, function(i, release) {
									$('#edit-release').append("<option value=\""+release.idCertrel+"\">"+release.name+" ("+release.description+")</option>");
								});
								
								$('#edit-release').val(data.idCertrel);
								
								if (data.type != "Development")
								{
									$.ajax({
										cache: false,
										type: 'GET',
										url: 'project/loadTccl',
										data : {
											idCertRel : data.idCertrel
										},
										success: function(tccls) {
											$('#edit-tccl').empty();
											$('#edit-tccl').prop('disabled', false);
											$('#edit-car-id').empty();
											$('#edit-car-id').prop('disabled', false);
											$.each(tccls, function(i, tccl) {
												$('#edit-tccl').append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
											});
										}
									});
								}
								else
								{
									$('#edit-tccl').empty();
									$('#edit-tccl').prop('disabled', true);
									$('#edit-car-id').empty();
									$('#edit-car-id').prop('disabled', true);
								}
							}
						});
						
						
						$('#edit-tccl').val(data.idTccl);
						
						var services = data.supportedServices.split(".");		
						$('#scroll-services').selectpicker('val',services);
				   }
				});
			});
			
			$('.result-link').on('click', function(e){		
				e.preventDefault();
				var id2 = $(this).parent().parent().find('td:first').html();
				$('#idProject').val(id2);
				
				localStorage.setItem("idProject",id2);
				
				$('#resultForm').submit();
			});
		</script>
		
		<!-- Row selector script -->
		<script>
			$("#table tbody").on('click', 'tr', function(){
			//$("#table tbody tr").click(function(){
				
				var table = $('#table').DataTable();
				var data = table.row(this).data();
				
				if($(this).hasClass("selected")) {
					$(this).removeClass('selected');
					sessionStorage.removeItem("idProject");
					disableButtons();
				} else {
					$('#table').DataTable().$('tr.selected').removeClass('selected');
					$(this).addClass('selected');
					//$(this).addClass('selected').siblings().removeClass('selected');    

				   	var dut = "N/A";
				   	var gu = "N/A";
				   	if(data[7]=="Yes")
				   	{
				   		dut = data[11];
				   		gu = data[12];
				   	}
				   		   	
				   	sessionStorage.setItem("idProject",data[0]);
				   	sessionStorage.setItem("projectName",data[2]);
				   	sessionStorage.setItem("type",data[4]);
				   	sessionStorage.setItem("associatedDut",dut);
				   	sessionStorage.setItem("associatedGu",gu);

					enableButtons(true);
				}
			   	
			});
		</script>
		
		<!-- Validation scripts -->
		<script>
			$('#newProjectForm').validate({
				rules: {
					name: {
						required: true,
						minlength: 2,
						maxlength: 255,
						remote: {
							url: "project/validateName",
							type: "get",
							data: {
								id: 0,
								name: function() {
									return $('#projectname').val();
								}
							}
						}
					},
					carId: {
						required: true,
						minlength: 2,
						maxlength: 60,
					}
				},
				messages: {
					name: {
						required: "Please enter project name!",
						maxlength: "Project name must have a max of 255 characters!",
						remote: "Project already exists!"
					},
					carId: {
						required: "Please enter CRI!",
						maxlength: "CRI must have a max of 60 characters!"
					}
				}
			});
			
			$('#editProjectForm').validate({
				rules: {
					name: {
						required: true,
						minlength: 2,
						maxlength: 255,
						remote: {
							url: "project/validateName",
							type: "get",
							data: {
								id: function() {
									return $('#edit_id').val();
								},
								name: function() {
									return $('#edit_name').val();
								}
							}
						}
					},
					carId: {
						required: true,
						minlength: 2,
						maxlength: 60,
					}
				},
				messages: {
					name: {
						required: "Please enter project name!",
						maxlength: "Project name must have a max of 255 characters!",
						remote: "Project already exists!"
					},
					carId: {
						required: "Please enter CRI!",
						maxlength: "CRI must have a max of 60 characters!"
					}
				}
			});
		</script>
    </body>
</html>