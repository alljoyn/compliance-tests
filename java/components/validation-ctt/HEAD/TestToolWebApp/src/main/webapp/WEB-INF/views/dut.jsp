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
		    
		    <!-- DUTs table -->
	        <div class="row">
	        	<div>       
			       	<table id="dutTable" class="table table-hover">
			       		<thead class="scroll-thead">
			       			<tr class="scroll-tr">
					        	<th width="10%">DUT Name</th>
					        	<th width="8%">Created</th>
					        	<th width="8%">Modified</th>
					        	<th width="10%">OEM</th>
					        	<th width="10%">Model</th>
					        	<th width="46%">Description</th>
					        	<th width="8%">Samples</th>
					        </tr>
					    </thead>
			        	<tbody class="scroll-tbody">
							<c:forEach var="dut" items="${dutList}" varStatus="status">
					        	<tr class="scroll-tr">
					        		<td class="hide">${dut.idDut}</td>
					        		<td width="10%">${dut.name}</td>
					        		<td width="8%"><fmt:formatDate value="${dut.createdDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td width="8%"><fmt:formatDate value="${dut.modifiedDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td width="10%">${dut.manufacturer}</td>
									<td width="10%">${dut.model}</td>
									<td width="46%">${dut.description}</td>	
									<td width="8%"><a href="#" class="sample-link">Samples</a></td>				
					        	</tr>
							</c:forEach>
						</tbody>        	
			       	</table>
			    </div>
		     </div>
		
			<!-- Action buttons -->
			<div class="row" align="left">
	        	<button type="button" class="btn btn-default btn-lg" data-toggle="modal" data-target="#newDutModal">New DUT</button>
	        	<button id="editButton" type="button" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#editDutModal">Edit DUT</button>   
	        	<button id="deleteButton" type="button" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#delDut">Delete DUT</button>
	        </div>
	        
	        <!-- Navigation buttons -->
	        <div class="row" align="left">
	        	<a id="errorButton" type="hidden" href="project?error=empty"></a>
	        	<a id="prevButton" type="button" class="btn btn-custom btn-lg" href="project">« Back</a>
	        	<button id="nextButton" disabled class="btn btn-custom btn-lg disabled pull-right">Next »</button>
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
        		<!-- <form:input type="hidden" id="isConfigured" name="isConfigured" path="isConfigured" value=""/> -->
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
	        					<div class="form-group">
	        						<label path="name" for="dut-name" class="control-label">Name</label>
	        						<form:input path="name" type="text" class="form-control" id="dut-name"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="manufacturer" for="dut-manufacturer" class="control-label">OEM</label>
	        						<form:input path="manufacturer" type="text" class="form-control" id="dut-manufacturer"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="model" for="dut-model" class="control-label">Model</label>
	        						<form:input path="model" type="text" class="form-control" id="dut-model"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="description" for="dut-description" class="control-label">Description</label>
	        						<form:input path="description" type="text" class="form-control" id="dut-description"/>
	        					</div>
	        					<h4 align="center">Insert a first sample</h4>
	        					<div class="form-group">
	        						<label path="deviceId" for="dut-device-id" class="control-label">Device ID</label>
	        						<form:input path="deviceId" type="text" class="form-control" id="dut-device-id"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="appId" for="dut-app-id" class="control-label">App ID</label>
	        						<form:input path="appId" type="text" class="form-control" id="dut-app-id"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="swVer" for="dut-sw-ver" class="control-label">Software Version</label>
	        						<form:input path="swVer" type="text" class="form-control" id="dut-sw-ver"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="hwVer" for="dut-hw-ver" class="control-label">Hardware Version</label>
	        						<form:input path="hwVer" type="text" class="form-control" id="dut-hw-ver"/>
	        					</div>
	        					
	        					<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
	        				</form:form>
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
	        					<form:input type="hidden" id="edit-id" path="idDut"/>
	        					<div class="form-group">
	        						<label path="name" for="edit-name" class="control-label">Name</label>
	        						<form:input path="name" type="text" class="form-control" id="edit-name"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="manufacturer" for="edit-manufacturer" class="control-label">OEM</label>
	        						<form:input path="manufacturer" type="text" class="form-control" id="edit-manufacturer"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="model" for="edit-model" class="control-label">Model</label>
	        						<form:input path="model" type="text" class="form-control" id="edit-model"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="description" for="edit-description" class="control-label">Description</label>
	        						<form:input path="description" type="text" class="form-control" id="edit-description"/>
	        					</div>
	        					<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
	        				</form:form>
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
	        	<div class="modal-dialog">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<table id="sampleTable" class="table table-hover">
	        					<thead>
	        						<tr>
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
	        						<label path="deviceId" for="sample-device-id" class="control-label">Device ID</label>
	        						<form:input path="deviceId" type="text" class="form-control" id="sample-device-id"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="appId" for="sample-app-id" class="control-label">App ID</label>
	        						<form:input path="appId" type="text" class="form-control" id="sample-app-id"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="swVer" for="sample-sw-ver" class="control-label">Software Version</label>
	        						<form:input path="swVer" type="text" class="form-control" id="sample-sw-ver"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="hwVer" for="sample-hw-ver" class="control-label">Hardware Version</label>
	        						<form:input path="hwVer" type="text" class="form-control" id="sample-hw-ver"/>
	        					</div>
	        					<form:input id="sample-dut" type="hidden" name="associatedDut" path="associatedDut" value=""/>			
	        				</form:form>
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
	        						<label path="deviceId" for="edit-device-id" class="control-label">Device ID</label>
	        						<form:input path="deviceId" type="text" class="form-control" id="edit-device-id"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="appId" for="edit-app-id" class="control-label">App ID</label>
	        						<form:input path="appId" type="text" class="form-control" id="edit-app-id"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="swVer" for="edit-sw-ver" class="control-label">Software Version</label>
	        						<form:input path="swVer" type="text" class="form-control" id="edit-sw-ver"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="hwVer" for="edit-hw-ver" class="control-label">Hardware Version</label>
	        						<form:input path="hwVer" type="text" class="form-control" id="edit-hw-ver"/>
	        					</div>
	        					<form:input id="edit-sample-id" type="hidden" name="idSample" path="idSample" value=""/>			
	        				</form:form>
	        			</div>
	        			<div class="modal-footer">
	        				<button type="button" class="btn btn-default" data-dismiss="modal" data-toggle="modal" data-target="#samplesDut">Cancel</button>
	        				<button id="editSampleConfirm" type="submit" class="btn btn-custom">Save Changes</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
		
		<script>
			$(document).ready(function() {
				$('#title').text("STEP 2: SELECT/EDIT/CREATE A DEVICE UNDER TEST (DUT)");
				if(sessionStorage.getItem("idProject")===null) {
					//document.getElementById("prevButton").click();
					document.getElementById("errorButton").click();
				}
				
				var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);
				
				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					if(($(MyRows[i]).find('td:eq(1)').html())==sessionStorage.getItem("associatedDut")) {
						$(MyRows[i]).addClass('selected');
						
						sessionStorage.setItem("idDut",($(MyRows[i]).find('td:eq(0)').html()));
						sessionStorage.setItem("aIdDut",($(MyRows[i]).find('td:eq(0)').html()));
						sessionStorage.setItem("dutName",($(MyRows[i]).find('td:eq(1)').html()));

					   	$('#nextButton').removeClass('disabled');
					   	$('#nextButton').prop("disabled", false);
						$('#deleteButton').removeClass('disabled');
						$('#deleteButton').prop("disabled", false);
						$('#editButton').removeClass('disabled');
						$('#editButton').prop("disabled", false);
					}
				}
			});
		</script>
		
		<script>
			function formSubmit() {
				document.getElementById("logoutForm").submit();
			}
		</script>
		
		<!-- Button scripts -->
		<script>
		
			$('.sample-link').on('click', function(e){
				
				e.preventDefault();
				
				$('#sample-dut').val($(this).parent().parent().find('td:first').html());
				
				$.ajax({
					type : 'GET',
					url : 'dut/samples',
					data : {
						idDut : $(this).parent().parent().find('td:first').html()
					},
					success: function (data) {

						$('#sampleBody').empty();
						$.each(data, function(i, sample) {
							$('#sampleBody').append("<tr><td class=\"hide\">"+sample.idSample+"</td><td>"
									+sample.deviceId+"</td><td>"+sample.appId+"</td><td>"
									+sample.swVer+"</td><td>"+sample.hwVer+"</td></tr>");
						});
						
						$('#samplesDut').modal({
							show: true
						});
						
						$("#sampleTable tbody tr").click(function(){
						   	$(this).addClass('selected').siblings().removeClass('selected');    
						   	var id=$(this).find('td:first').html();
						   	sessionStorage.setItem("idSample",id);

							$('#deleteSample').removeClass('disabled');
							$('#deleteSample').prop("disabled", false);
							$('#editSample').removeClass('disabled');
							$('#editSample').prop("disabled", false);
						});
				   }
				});
			});
		
			$('#nextButton').on('click', function(){
			
				if(sessionStorage.getItem("associatedDut")!="N/A") {
					if(sessionStorage.getItem("aIdDut")==sessionStorage.getItem("idDut")) {
						document.getElementById('idProject').value = sessionStorage.getItem("idProject");
						//document.getElementById('isConfigured').value = sessionStorage.getItem("isConfigured");
						document.getElementById('idDut').value = sessionStorage.getItem("idDut");
						sessionStorage.setItem("associatedDut",sessionStorage.getItem("dutName"));			
						$('#nextForm').submit();
					} else {
						$('#diffDut').modal({
							show: true
						});
					}
				} else {
					document.getElementById('idProject').value = sessionStorage.getItem("idProject");
					document.getElementById('idDut').value = sessionStorage.getItem("idDut");
					sessionStorage.setItem("associatedDut",sessionStorage.getItem("dutName"));	
					
					$('#nextForm').submit();
				}
			});
			
			$('#continueButton').on('click', function() {
				document.getElementById('idProject').value = sessionStorage.getItem("idProject");
				document.getElementById('idDut').value = sessionStorage.getItem("idDut");
				//sessionStorage.setItem("associatedDut",sessionStorage.getItem("idDut"));
				sessionStorage.setItem("associatedDut",sessionStorage.getItem("dutName"));	
				$('#nextForm').submit();
			});
		
		  	$('#createDut').on('click', function(e){
		    	// We don't want this to act as a link so cancel the link action
		    	e.preventDefault();
		    	
		    	// Find form and submit it
		    	$('#newDutForm').submit();
		  	});

			$('#deleteConfirm').on('click', function(){
				
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
			    
			    $.ajax({
						type : 'POST',
						url : 'dut/delete',
						beforeSend: function(xhr) {
				            xhr.setRequestHeader(header, token);
				        },
						data : {
							data : sessionStorage.getItem("idDut")
						},
						success: function() {
						    var MyRows = $('#dutTable').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idDut")) {
									$(MyRows[i]).fadeOut(400, function() {
										$(MyRows[i]).remove();
									});
								}
							}
							
							sessionStorage.removeItem("idDut");
							
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
				
				$('#editDutForm').submit();
			});
			
			$('#editButton').on('click', function(){
				$.ajax({
					type : 'GET',
					url : 'dut/edit',
					data : {
						data : sessionStorage.getItem("idDut")
					},
					success: function (data) {
						
						$('#edit-id').val(data.idDut);
						$('#edit-name').val(data.name);
						$('#edit-manufacturer').val(data.manufacturer);
						$('#edit-model').val(data.model);
						$('#edit-description').val(data.description);
				   }
				});
			});
		  	
		  	$('#createSample').on('click', function(e){
		    	// We don't want this to act as a link so cancel the link action
		    	e.preventDefault();
		    	
		    	// Find form and submit it
		    	$('#newSampleForm').submit();
		  	});
		  	
		  	$('#sampleBack').on('click', function() {
		  		$('#deleteSample').addClass('disabled');
				$('#deleteSample').prop("disabled", true);
				$('#editSample').addClass('disabled');
				$('#editSample').prop("disabled", true);
		  	});
		  	
			$('#deleteSample').on('click', function(){
				
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
			    
			    $.ajax({
						type : 'POST',
						url : 'dut/samples/delete',
						beforeSend: function(xhr) {
				            xhr.setRequestHeader(header, token);
				        },
						data : {
							data : sessionStorage.getItem("idSample")
						},
						success: function() {
						    var MyRows = $('#sampleTable').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idSample")) {
									$(MyRows[i]).fadeOut(400, function() {
										$(MyRows[i]).remove();
									});
								}
							}
							
							sessionStorage.removeItem("idSample");
							
							$('#deleteSample').addClass('disabled');
							$('#deleteSample').prop("disabled", true);
							$('#editSample').addClass('disabled');
							$('#editSample').prop("disabled", true);
						}
				});
			});
			
		    $('#editSampleConfirm').on('click', function(e) {
				e.preventDefault();
				
				$('#editSampleForm').submit();
			});
			
			$('#editSample').on('click', function(){
				$.ajax({
					type : 'GET',
					url : 'dut/samples/edit',
					data : {
						data : sessionStorage.getItem("idSample")
					},
					success: function (data) {
						
						$('#edit-device-id').val(data.deviceId);
						$('#edit-app-id').val(data.appId);
						$('#edit-sw-ver').val(data.swVer);
						$('#edit-hw-ver').val(data.hwVer);
						$('#edit-sample-id').val(data.idSample);
				   }
				});
			});
			 	
		</script>
		
		<!-- Row selector script -->
		<script>
			$("#dutTable tbody tr").click(function(){
				if($(this).hasClass('selected')) {
					$(this).removeClass('selected');
					sessionStorage.removeItem("idDut");
					$('#nextButton').addClass('disabled');
				   	$('#nextButton').prop("disabled", true);
					$('#deleteButton').addClass('disabled');
					$('#deleteButton').prop("disabled", true);
					$('#editButton').addClass('disabled');
					$('#editButton').prop("disabled", true);
				} else {
					$(this).addClass('selected').siblings().removeClass('selected');    
				   	var id=$(this).find('td:first').html();
				   	var name=$(this).find('td:eq(1)').html();
				   	sessionStorage.setItem("idDut",id);
				   	sessionStorage.setItem("dutName", name);

				   	$('#nextButton').removeClass('disabled');
				   	$('#nextButton').prop("disabled", false);
					$('#deleteButton').removeClass('disabled');
					$('#deleteButton').prop("disabled", false);
					$('#editButton').removeClass('disabled');
					$('#editButton').prop("disabled", false);
				}
			});
		</script>
    </body>
</html>