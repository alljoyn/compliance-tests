<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html lang="en">
    <head>
    	<jsp:include page="/WEB-INF/views/page_head.jsp"/>
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
				<h4 id="selectedProject" class="pull-left"></h4>
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
			       	<table id="dutTable" class="table table-hover hide">
			       		<!-- <thead class="scroll-thead">
			       			<tr class="scroll-tr">  -->
			       		<thead>
			       			<tr>
			       				<th class="hide">DUT ID</th>
					        	<th>DUT Name</th>
					        	<th>Created</th>
					        	<th>Modified</th>
					        	<th>OEM</th>
					        	<th>Model</th>
					        	<th>Description</th>
					        	<th>Samples</th>
					        </tr>
					    </thead>
			        	<!-- <tbody class="scroll-tbody">  -->
			        	<tbody>
							<c:forEach var="dut" items="${dutList}" varStatus="status">
								<tr>
					        	<!-- <tr class="scroll-tr">  -->
					        		<td class="hide">${dut.idDut}</td>
					        		<td>${dut.name}</td>
					        		<td><fmt:formatDate value="${dut.createdDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td><fmt:formatDate value="${dut.modifiedDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
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
			<div class="row" align="left">
	        	<button type="button" class="btn btn-default btn-lg" data-toggle="modal" data-target="#newDutModal">New DUT</button>
	        	<button id="editButton" type="button" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#editDutModal">Edit DUT</button>   
	        	<button id="deleteButton" type="button" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#delDut">Delete DUT</button>
	        </div>
	        
	        <!-- Navigation buttons -->
	        <div class="row" align="left">
	        	<a id="errorButton" type="hidden" href="project?error=empty"></a>
	        	<a id="prevButton" type="button" class="btn btn-custom btn-lg" href="project">« Previous Step</a>
	        	<button id="nextButton" disabled class="btn btn-custom btn-lg disabled pull-right">Next Step »</button>
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
	        						<label path="name" for="dut-name" class="control-label">Name (*)</label>
	        						<form:input path="name" type="text" class="form-control" id="dut-name"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="manufacturer" for="dut-manufacturer" class="control-label">OEM (*)</label>
	        						<form:input path="manufacturer" type="text" class="form-control" id="dut-manufacturer"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="model" for="dut-model" class="control-label">Model (*)</label>
	        						<form:input path="model" type="text" class="form-control" id="dut-model"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="description" for="dut-description" class="control-label">Description</label>
	        						<form:input path="description" type="text" class="form-control" id="dut-description"/>
	        					</div>
	        					<h4 align="center">Insert a first sample</h4>
	        					<div class="form-group">
	        						<label path="deviceId" for="dut-device-id" class="control-label">Device ID (*)</label>
	        						<form:input path="deviceId" type="text" class="form-control" id="dut-device-id"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="appId" for="dut-app-id" class="control-label">App ID (*)</label>
	        						<form:input path="appId" type="text" class="form-control" id="dut-app-id"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="swVer" for="dut-sw-ver" class="control-label">Software Version (*)</label>
	        						<form:input path="swVer" type="text" class="form-control" id="dut-sw-ver"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="hwVer" for="dut-hw-ver" class="control-label">Hardware Version (*)</label>
	        						<form:input path="hwVer" type="text" class="form-control" id="dut-hw-ver"/>
	        					</div>
	        					
	        					<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
	        				</form:form>
	        				<p>(*) This is a mandatory field</p>
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
	        						<label path="name" for="edit-name" class="control-label">Name(*)</label>
	        						<form:input path="name" type="text" class="form-control" id="edit-name"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="manufacturer" for="edit-manufacturer" class="control-label">OEM(*)</label>
	        						<form:input path="manufacturer" type="text" class="form-control" id="edit-manufacturer"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="model" for="edit-model" class="control-label">Model(*)</label>
	        						<form:input path="model" type="text" class="form-control" id="edit-model"/>
	        					</div>
	        					<div class="form-group">
	        						<label path="description" for="edit-description" class="control-label">Description</label>
	        						<form:input path="description" type="text" class="form-control" id="edit-description"/>
	        					</div>
	        					<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
	        				</form:form>
	        				<p>(*) This is a mandatory field</p>
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
	        						<label path="hwVer" for="sample-hw-ver" class="control-label">Hardware Version (*)</label>
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
	        						<label path="hwVer" for="edit-hw-ver" class="control-label">Hardware Version (*)</label>
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
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
		<script src="resources/jquery-validation/1.13.1/js/jquery.validate.min.js"></script>
		<script src="resources/jquery-validation/1.13.1/js/additional-methods.min.js"></script>
		
		<script>
			$(document).ready(function() {
				$('#title').text("STEP 2: SELECT/EDIT/CREATE A DEVICE UNDER TEST (DUT)");
				if(sessionStorage.getItem("idProject")===null) {
					document.getElementById("errorButton").click();
				}
				$('#selectedProject').append("Project: "+sessionStorage.getItem("projectName"));
				
				$('#dutTable').removeClass('hide');
				
				$('#dutTable').dataTable({
					//autoWidth: false,
					pagingType: 'full_numbers',
					scrollY: ($(window).height()/2),
					order: [0, 'asc'],
					columnDefs: [
						{orderable: false, targets: 7}             
					]
				});
				
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
		
		<!-- Validation scripts -->
		<script>
			$('#newDutForm').validate({
				rules: {
					name: {
						required: true,
						maxlength: 255,
						remote: {
							url: "dut/validateName",
							type: "get",
							data: {
								id: 0,
								name: function() {
									return $('#dut-name').val();
								}
							}
						}
					},
					manufacturer: {
						required: true,
						maxlength: 60,
					},
					model: {
						required: true,
						maxlength: 60,
					},
					description: {
						maxlength: 255,
					},
					deviceId: {
						required: true,
						maxlength: 60,
					},
					appId: {
						required: true,
						maxlength: 36,
						pattern: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
					},
					swVer: {
						required: true,
						maxlength: 60,
					},
					hwVer: {
						required: true,
						maxlength: 60,
					}
				},
				messages: {
					name: {
						required: "Please enter DUT name!",
						maxlength: "DUT name must have a max of 255 characters!",
						remote: "DUT already exists!"
					},
					manufacturer: {
						required: "Please enter manufacturer!",
						maxlength: "Manufacturer must have a max of 60 characters!"
					},
					model: {
						required: "Please enter model!",
						maxlength: "Model must have a max of 60 characters!"
					},
					description: {
						maxlength: "Description must have a max of 60 characters!"
					},
					deviceId: {
						required: "Please enter device ID of the sample!",
						maxlength: "Device ID must have a max of 60 characters!"
					},
					appId: {
						required: "Please enter app ID of the sample!",
						maxlength: "App ID must have a max of 60 characters!",
						pattern: "Please enter a valid UUID! (Example: 12345678-9abc-1def-8012-000000000000)"
					},
					swVer: {
						required: "Please enter software version of the sample!",
						maxlength: "Software version must have a max of 60 characters!"
					},
					hwVer: {
						required: "Please enter hardware version of the sample!",
						maxlength: "Hardware version must have a max of 60 characters!"
					}
				}
			});
			
			$('#editDutForm').validate({
				rules: {
					name: {
						required: true,
						maxlength: 255,
						remote: {
							url: "dut/validateName",
							type: "get",
							data: {
								id: function() {
									return $('#edit-id').val();
								},
								name: function() {
									return $('#edit-name').val();
								}
							}
						}
					},
					manufacturer: {
						required: true,
						maxlength: 60,
					},
					model: {
						required: true,
						maxlength: 60,
					},
					description: {
						maxlength: 255,
					}
				},
				messages: {
					name: {
						required: "Please enter DUT name!",
						maxlength: "DUT name must have a max of 255 characters!",
						remote: "DUT already exists!"
					},
					manufacturer: {
						required: "Please enter manufacturer!",
						maxlength: "Manufacturer must have a max of 60 characters!"
					},
					model: {
						required: "Please enter model!",
						maxlength: "Model must have a max of 60 characters!"
					},
					description: {
						maxlength: "Description must have a max of 60 characters!"
					}
				}
			});
			
			$('#newSampleForm').validate({
				rules: {
					deviceId: {
						required: true,
						maxlength: 60,
					},
					appId: {
						required: true,
						maxlength: 36,
						pattern: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
					},
					swVer: {
						required: true,
						maxlength: 60,
					},
					hwVer: {
						required: true,
						maxlength: 60,
					}
				},
				messages: {
					deviceId: {
						required: "Please enter device ID of the sample!",
						maxlength: "Device ID must have a max of 60 characters!"
					},
					appId: {
						required: "Please enter app ID of the sample!",
						maxlength: "App ID must have a max of 60 characters!",
						pattern: "Please enter a valid UUID! (Example: 12345678-9abc-1def-8012-000000000000)"
					},
					swVer: {
						required: "Please enter software version of the sample!",
						maxlength: "Software version must have a max of 60 characters!"
					},
					hwVer: {
						required: "Please enter hardware version of the sample!",
						maxlength: "Hardware version must have a max of 60 characters!"
					}
				}
			});
			
			$('#editSampleForm').validate({
				rules: {
					deviceId: {
						required: true,
						maxlength: 60,
					},
					appId: {
						required: true,
						maxlength: 36,
						pattern: /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i,
					},
					swVer: {
						required: true,
						maxlength: 60,
					},
					hwVer: {
						required: true,
						maxlength: 60,
					}
				},
				messages: {
					deviceId: {
						required: "Please enter device ID of the sample!",
						maxlength: "Device ID must have a max of 60 characters!"
					},
					appId: {
						required: "Please enter app ID of the sample!",
						maxlength: "App ID must have a max of 60 characters!",
						pattern: "Please enter a valid UUID! (Example: 12345678-9abc-1def-8012-000000000000)"
					},
					swVer: {
						required: "Please enter software version of the sample!",
						maxlength: "Software version must have a max of 60 characters!"
					},
					hwVer: {
						required: "Please enter hardware version of the sample!",
						maxlength: "Hardware version must have a max of 60 characters!"
					}
				}
			});
		</script>
		
		<!-- Function scripts -->
		<script>
			function formSubmit()
			{
				$("#logoutForm").submit();
			}
			
			function enableDutButtons(enableNext)
			{	
				$('#deleteButton').removeClass('disabled');
				$('#deleteButton').prop("disabled", false);
				$('#editButton').removeClass('disabled');
				$('#editButton').prop("disabled", false);
				$('#nextButton').removeClass('disabled');
			   	$('#nextButton').prop("disabled", false);
			}
			
			function disableDutButtons()
			{
				$('#nextButton').addClass('disabled');
			   	$('#nextButton').prop("disabled", true);
				$('#deleteButton').addClass('disabled');
				$('#deleteButton').prop("disabled", true);
				$('#editButton').addClass('disabled');
				$('#editButton').prop("disabled", true);
			}
			
			function enableSampleButtons()
			{
				$('#deleteSample').removeClass('disabled');
				$('#deleteSample').prop("disabled", false);
				$('#editSample').removeClass('disabled');
				$('#editSample').prop("disabled", false);
			}
			
			function disableSampleButtons()
			{
				$('#deleteSample').addClass('disabled');
				$('#deleteSample').prop("disabled", true);
				$('#editSample').addClass('disabled');
				$('#editSample').prop("disabled", true);
			}
		</script>
		
		<!-- Button scripts -->
		<script>
		
			$('.sample-link').on('click', function(e){
				
				e.preventDefault();
				
				$('#sample-dut').val($(this).parent().parent().find('td:first').html());
				
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'dut/samples',
					data : {
						idDut : $(this).parent().parent().find('td:first').html()
					},
					success: function (samples) {

						$('#sampleBody').empty();
						$.each(samples, function(i, sample) {
							$('#sampleBody').append("<tr><td class=\"hide\">"+sample.idSample+"</td><td>"
									+sample.deviceId+"</td><td>"+sample.appId+"</td><td>"
									+sample.swVer+"</td><td>"+sample.hwVer+"</td></tr>");
						});
						
						$('#samplesDut').modal({
							show: true
						});
						
						/*$('#sampleTable').dataTable({
							paging: false,
							scrollY: 500,
							order: [0, 'asc'],
						});*/
						
						$("#sampleTable tbody tr").click(function(){
						   	$(this).addClass('selected').siblings().removeClass('selected');    
						   	var id=$(this).find('td:first').html();
						   	sessionStorage.setItem("idSample",id);

							enableSampleButtons();
						});
				   }
				});
			});
		
			$('#nextButton').on('click', function(){
			
				if(sessionStorage.getItem("associatedDut")!="N/A") {
					if(sessionStorage.getItem("aIdDut")==sessionStorage.getItem("idDut")) {
						$('#idProject').val(sessionStorage.getItem("idProject"));
						$('#idDut').val(sessionStorage.getItem("idDut"));
						sessionStorage.setItem("associatedDut",sessionStorage.getItem("dutName"));			
						$('#nextForm').submit();
					} else {
						$('#diffDut').modal({
							show: true
						});
					}
				} else {
					$('#idProject').val(sessionStorage.getItem("idProject"));
					$('#idDut').val(sessionStorage.getItem("idDut"));
					sessionStorage.setItem("associatedDut",sessionStorage.getItem("dutName"));	
					
					$('#nextForm').submit();
				}
			});
			
			$('#continueButton').on('click', function() {
				$('#idProject').val(sessionStorage.getItem("idProject"));
				$('#idDut').val(sessionStorage.getItem("idDut"));
				sessionStorage.setItem("associatedDut",sessionStorage.getItem("dutName"));	
				$('#nextForm').submit();
			});
		
		  	$('#createDut').on('click', function(e){
		    	e.preventDefault();
		    	
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
							idDut : sessionStorage.getItem("idDut")
						},
						success: function() {
						    var MyRows = $('#dutTable').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idDut")) {
									var table = $('#dutTable').DataTable();
									table.row($(MyRows[i])).remove().draw();
								}
							}
							
							sessionStorage.removeItem("idDut");
							
							disableDutButtons();
						}
				});
			});
			
			$('#editConfirm').on('click', function(e) {
				e.preventDefault();
				
				$('#editDutForm').submit();
			});
			
			$('#editButton').on('click', function(){
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'dut/edit',
					data : {
						idDut : sessionStorage.getItem("idDut")
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
		    	e.preventDefault();
		    	$('#newSampleForm').submit();
		  	});
		  	
		  	$('#sampleBack').on('click', disableSampleButtons);
		  	
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
							idSample : sessionStorage.getItem("idSample")
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
							
							disableSampleButtons();
						}
				});
			});
			
		    $('#editSampleConfirm').on('click', function(e) {
				e.preventDefault();
				
				$('#editSampleForm').submit();
			});
			
			$('#editSample').on('click', function(){
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'dut/samples/edit',
					data : {
						idSample : sessionStorage.getItem("idSample")
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
			$("#dutTable tbody").on('click', 'tr', function(){
			//$("#dutTable tbody tr").click(function(){
				if($(this).hasClass('selected')) {
					$(this).removeClass('selected');
					sessionStorage.removeItem("idDut");

					disableDutButtons();
				} else {
					$('#dutTable').DataTable().$('tr.selected').removeClass('selected');
					$(this).addClass('selected');
					//$(this).addClass('selected').siblings().removeClass('selected');    
				   	var id=$(this).find('td:first').html();
				   	var name=$(this).find('td:eq(1)').html();
				   	sessionStorage.setItem("idDut",id);
				   	sessionStorage.setItem("dutName", name);

					enableDutButtons();
				}
			});
		</script>
    </body>
</html>