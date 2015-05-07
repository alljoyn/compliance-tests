<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html lang="en">
    <head>
    	<title>AllSeen</title>
    	<meta charset="utf-8">
    	
    	<!-- Add the next line to ensure proper rendering and touch zooming -->
    	<meta name="viewport" content="width=device-width, initial-scale=1">
    	
    	<meta name="_csrf" content="${_csrf.token}"/>
		<meta name="_csrf_header" content="${_csrf.headerName}"/>
        
        <!-- Web icon -->
        <link rel="shortcut icon" href="resources/img/favicon.ico" type="image/vnd.microsoft.icon" />
        
        <!-- Bootstrap -->
		<link rel="stylesheet" type="text/css" href="resources/bootstrap/css/bootstrap.min.css"/>		    	
    	<link rel="stylesheet" type="text/css" href="resources/bootstrap/css/custom.css">
		
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
			       	<table id="table" class="table table-hover">
			       		<thead class="scroll-thead">
			       			<tr class="scroll-tr">
					        	<th width="10%">Project Name</th>
					        	<th width="8%">Created</th>
					        	<th width="8%">Modified</th>
					        	<th width="12%">Type</th>
					        	<th width="9%">Certification Release</th>
					        	<th width="7%">TCCL</th>
					        	<th width="13%">Supported Services</th>
					        	<th width="7%">DUT</th>
					        	<th width="7%">GU</th>
					        	<th width="4%">CRI</th>
					        	<th width="9%">Configured</th>
					        	<th width="6%">Results</th>
					        </tr>
					    </thead>
			        	<tbody class="scroll-tbody">
							<c:forEach var="project" items="${projectList}" varStatus="status">
					        	<tr class="scroll-tr">
					        		<td class="hide">${project.idProject}</td>
					        		<td width="10%">${project.name}</td>
					        		<td width="8%"><fmt:formatDate value="${project.createdDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td width="8%"><fmt:formatDate value="${project.modifiedDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td width="12%">${project.type}</td>
									<c:forEach var="certrel" items="${certrelList}" varStatus="status">
										<c:if test="${certrel.idCertrel==project.idCertrel}">
											<td width="9%">${certrel.name}</td>
										</c:if>
									</c:forEach>
									<c:forEach var="tccl" items="${tcclList}" varStatus="status">
										<c:if test="${tccl.idTccl==project.idTccl}">
											<td width="7%">${tccl.name}</td>
										</c:if>
									</c:forEach>
									<td width="13%">${project.supportedServices}</td>
									<c:choose>
										<c:when test="${project.idDut!=0}">
											<c:forEach var="dut" items="${dutList}" varStatus="status">
												<c:if test="${dut.idDut==project.idDut}">
													<td width="7%">${dut.name}</td>
												</c:if>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<td width="7%">Not selected</td>
										</c:otherwise>
									</c:choose>
									<td width="7%">${project.gUnits}</td>
									<td width="4%">${project.carId}</td>
									<c:choose>
										<c:when test="${project.isConfigured}">
											<td width="9%">Yes</td>
										</c:when>
										<c:otherwise>
											<td width="9%">No</td>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${project.hasResults}">
											<td width="6%"><a href="#" class="result-link">Link to results</a></td>
										</c:when>
										<c:otherwise>
											<td width="6%">No results</td>
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
	        	<!-- <a id="prevButton" type="button" class="btn btn-custom btn-lg" href="dut">« Back</a>  -->
	        	<a id="nextButton" type="button" disabled class="btn btn-custom btn-lg disabled pull-right" href="dut">Next »</a>
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
	        						<label path="name" for="project-name" class="control-label">Name</label>
	        						<form:input path="name" type="text" class="form-control" id="project-name"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="type" for="message-text" class="control-label">Type</label>
	        						<form:select path="type" class="form-control" id="message-text">
	        							<option value="Conformance">Conformance</option>
	        							<option value="Interoperability">Interoperability</option>
	        							<option value="Conformance and Interoperability">Conformance and Interoperability</option>
	        							<option value="Pre-certification">Pre-certification</option>
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label path="idCertrel" for="project-release" class="control-label">Certification Release</label>
	        						<form:select path="idCertrel" class="form-control" id="project-release">
	        							<c:forEach var="certrel" items="${certrelList}" varStatus="status">
											<c:choose>
												<c:when test="${not status.last}">
													<option value="${certrel.idCertrel}">${certrel.name}</option>
												</c:when>
												<c:otherwise>
													<option value="${certrel.idCertrel}" selected>${certrel.name}</option>
												</c:otherwise>
											</c:choose>
										</c:forEach>
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label path="idTccl" for="project-tccl" class="control-label">Test Case Control List</label>
	        						<form:select path="idTccl" class="form-control" id="project-tccl">
	        							<!-- <c:forEach var="tccl" items="${tcclList}" varStatus="status">
											<option value="${tccl.idTccl}">${tccl.name}</option>
										</c:forEach> -->
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label path="carId" for="project-car-id" class="control-label">Certification Application Request ID</label>
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
	        					<form:input type="hidden" id="edit-id" path="idProject"/>
	        					<div class="form-group">
	        						<label path="name" for="edit-name" class="control-label">Name</label>
	        						<form:input path="name" type="text" class="form-control" id="edit-name"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="type" for="edit-type" class="control-label">Type</label>
	        						<form:select path="type" class="form-control" id="edit-type">
	        							<option value="Conformance">Conformance</option>
	        							<option value="Interoperability">Interoperability</option>
	        							<option value="Conformance and Interoperability">Conformance and Interoperability</option>
	        							<option value="Pre-certification">Pre-certification</option>
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label path="idCertrel" for="edit-release" class="control-label">Certification Release</label>
	        						<form:select path="idCertrel" class="form-control" id="edit-release">
	        							<c:forEach var="certrel" items="${certrelList}" varStatus="status">
											<option value="${certrel.idCertrel}">${certrel.name}</option>
										</c:forEach>
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label path="idTccl" for="edit-tccl" class="control-label">Test Case Control List</label>
	        						<form:select path="idTccl" class="form-control" id="edit-tccl">
	        						</form:select>
	        					</div>
	        					<div class="form-group">
	        						<label path="carId" for="edit-car-id" class="control-label">Certification Application Request ID</label>
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
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
		
		<script src="resources/bootstrap-select/js/bootstrap-select.min.js"></script>
	
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				document.getElementById("logoutForm").submit();
			}
		</script>
		
		<script>
			$(document).ready(function() {
				$('#title').text("STEP 1: SELECT/EDIT/CREATE A PROJECT OR VIEW RESULTS");
				$('.selectpicker').selectpicker();
				sessionStorage.clear();
				
				var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);
			});
		</script>
		
		<!-- Selector scripts -->
		<script>
			$('#project-release').change(function() {
				var selected = $(this).find("option:selected").val();
				
				$.ajax({
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
			});
			
			$('#edit-release').change(function() {
				var selected = $(this).find("option:selected").val();
				
				$.ajax({
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
			});
		</script>
		
		<!-- Button scripts -->
		<script>
		  	$('#createConfirm').on('click', function(){
		  		
				var text="";
		    	
		    	$('#new-project-services option:selected').each(function(index){
		    		text+=$(this).val()+".";
		    	});
		    	
		    	document.getElementById('supportedServices').value = text;
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
							data : sessionStorage.getItem("idProject")
						},
						success: function () {
							var MyRows = $('.table').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idProject")) {
									//alert($(MyRows[i]).find('td:eq(1)').html());
									$(MyRows[i]).fadeOut(400, function() {
										$(MyRows[i]).remove();
									});
								}
							}
							
							sessionStorage.removeItem("idProject");
							
							$('#nextButton').addClass('disabled');
							$('#nextButton').prop("disabled", true);
							$('#deleteButton').addClass('disabled');
							$('#deleteButton').prop("disabled", true);
							$('#editButton').addClass('disabled');
							$('#editButton').prop("disabled", true);
					   }
				});
			});
			
			$('#editConfirm').on('click', function(e) {
				e.preventDefault();
				
				var text="";
		    	
		    	$('#scroll-services option:selected').each(function(index){
		    		text+=$(this).val()+".";
		    	});
		    	
		    	document.getElementById('edit-services').value = text;
				$('#editProjectForm').submit();
			});
			
			$('#createButton').on('click', function(){
					$.ajax({
						type: 'GET',
						url: 'project/loadTccl',
						data : {
							idCertRel : $('#project-release').val()
						},
						success: function(data) {
							$('#project-tccl').empty();
							$.each(data, function(i, tccl) {
								$('#project-tccl').append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
							});
						}
					});
					
					var services = data.supportedServices.split(".");		
					$('#scroll-services').selectpicker('val',services);

			});
			
			$('#editButton').on('click', function(){
				$.ajax({
					type : 'GET',
					url : 'project/edit',
					data : {
						data : sessionStorage.getItem("idProject")
					},
					success: function (data) {
						
						$('#edit-id').val(data.idProject);
						$('#edit-name').val(data.name);
						$('#edit-type').val(data.type);
						$('#edit-release').val(data.idCertrel);
						//$('#edit-tccl').val(data.idTccl);
						$('#edit-car-id').val(data.carId);
						
						$.ajax({
							type: 'GET',
							url: 'project/loadTccl',
							data : {
								idCertRel : data.idCertrel
							},
							success: function(data) {
								$('#edit-tccl').empty();
								$.each(data, function(i, tccl) {
									$('#edit-tccl').append("<option value=\""+tccl.idTccl+"\">"+tccl.name+"</option>");
								});
							}
						});
						
						var services = data.supportedServices.split(".");		
						$('#scroll-services').selectpicker('val',services);
				   }
				});
			});
			
			$('.result-link').on('click', function(e){		
				e.preventDefault();
				var id2 = $(this).parent().parent().find('td:first').html();
				document.getElementById('idProject').value = id2;
				
				localStorage.setItem("idProject",id2);
				
				$('#resultForm').submit();
			});
		</script>
		
		<!-- Row selector script -->
		<script>
			$("#table tbody tr").click(function(){
				
				if($(this).hasClass("selected")) {
					$(this).removeClass('selected');
					sessionStorage.removeItem("idProject");
					$('#nextButton').addClass('disabled');
				   	$('#nextButton').attr("disabled");
					$('#deleteButton').addClass('disabled');
					$('#deleteButton').prop("disabled", true);
					$('#editButton').addClass('disabled');
					$('#editButton').prop("disabled", true);
				} else {
					$(this).addClass('selected').siblings().removeClass('selected');    
				   	var id=$(this).find('td:first').html();			
				   	
				   	if(($(this).find('td:eq(11)').html()=="Yes")) {
					   	var dut=$(this).find('td:eq(8)').html();
					   	var gu=$(this).find('td:eq(9)').html();
				   		sessionStorage.setItem("associatedDut", dut);
				   		sessionStorage.setItem("associatedGu",gu);
				   	} else {
				   		sessionStorage.setItem("associatedDut", "N/A");
				   		sessionStorage.setItem("associatedGu", "N/A");
				   	}
				   	var type=$(this).find('td:eq(4)').html();
				   	
				   	sessionStorage.setItem("idProject",id);
				   	
				   	//sessionStorage.setItem("isConfigured",configured);
				   	sessionStorage.setItem("type",type);

				   	$('#nextButton').removeClass('disabled');
				   	$('#nextButton').removeAttr("disabled");
					$('#deleteButton').removeClass('disabled');
					$('#deleteButton').prop("disabled", false);
					$('#editButton').removeClass('disabled');
					$('#editButton').prop("disabled", false);
				}
			   	
			});
		</script>
    </body>
</html>