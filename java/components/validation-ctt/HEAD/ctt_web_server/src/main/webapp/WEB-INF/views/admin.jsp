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
		    	<c:if test="${pageContext.request.userPrincipal.name != null}">
					<h4>
						Welcome : ${pageContext.request.userPrincipal.name} | <a
						href="javascript:formSubmit()"> Logout</a>
					</h4>
				</c:if>
		    </div>
		    
		    <!-- Test Cases Control List panel -->
		    <div class="row" align="left">
				<div class="panel panel-default">
		        	<div class="panel-heading">
		        		<h4 class="panel-title">
		        			<a data-toggle="collapse" data-target="#collapseTccl">Test Cases Control List</a>
		        		</h4>
		        	</div>
		        	<div id="collapseTccl" class="panel-collapse collapse in">
						<div class="panel-body">      
					       	<table id="table" class="table table-hover">
					       		<thead>
					       			<tr>
					       				<th>TCCL ID</th>
							        	<th width="25%">TCCL Name</th>
							        	<th width="25%">Created</th>
							        	<th width="25%">Modified</th>
							        	<th width="25%">Certification Release</th>
							        </tr>
							    </thead>
					        	<tbody>
									<c:forEach var="tccl" items="${tcclList}" varStatus="status">
							        	<tr>
							        		<td>${tccl.idTccl}</td>
							        		<td width="25%">${tccl.name}</td>
											<td width="25%"><fmt:formatDate value="${tccl.createdDate}"
												pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<td width="25%"><fmt:formatDate value="${tccl.modifiedDate}"
												pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<c:forEach var="certrel" items="${certrelList}" varStatus="status">
												<c:if test="${certrel.idCertrel==tccl.idCertrel}">
													<td width="25%">${certrel.name}</td>
												</c:if>
											</c:forEach>
										</tr>
									</c:forEach>
								</tbody>        	
					       	</table>
					       	<!-- Action buttons -->
							<div align="left">
					        	<button id="newTcclButton" class="btn btn-default btn-lg" data-toggle="modal" data-target="#newTcclModal">New TCCL</button>
					        	<button id="viewButton" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#viewTcclModal">View TCCL</button>
					        	<button id="editButton" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#editTcclModal">Edit TCCL</button>
					        	<button id="deleteButton" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#delTcclModal">Delete TCCL</button>
					        </div>
					    </div>
				    </div>
				</div>
			</div>
		
	        <!-- Test Cases Packages panel -->
	        <div class="row" align="left">
				<div class="panel panel-default">
					<div class="panel-heading">
		        		<h4 class="panel-title">
		        			<a data-toggle="collapse" class="collapsed" data-target="#collapseTCPackages">Test Cases Packages</a>
		        		</h4>
		        	</div>
					<!-- <div class="panel-heading"><strong>Test Cases Packages panel</strong>  </div>  -->
					<div id="collapseTCPackages" class="panel-collapse collapse">
						<div class="panel-body">
							<!-- Available packages -->
							<div>
								<h4>Available packages</h4>
								<div class="list-group" id="tcPackages">
								</div>
							</div>
							<!-- Standard Form -->
							<h4>Select file from your computer <small> Uploading an already existing package will cause overriding</small></h4>
							<form method="post" enctype="multipart/form-data" id="tcUploadForm">
								<div class="form-inline">
									<div class="form-group">
										<input type="file" name="file" id="file" accept=".jar">
									</div>
									<button type="submit" disabled class="btn btn-sm btn-custom disabled" id="tcUploadSubmit">Upload file</button>
								</div>
								<div class="form-group">
									<label for="tcDescription" class="control-label">Package description</label>
									<input type="text" class="form-control" id="tcDescription" name="tcDescription">
								</div>
							</form>
													
							<!-- Upload Finished -->
					        <div>
					        	<h4>Processed files</h4>
					        	<div class="list-group" id="tcUploadResults">
					         	</div>
				        </div>
					</div>
					</div>
				</div>
	        </div>
	        
	        <!-- Local Agent Installers panel -->
	        <div class="row" align="left">
				<div class="panel panel-default">
					<div class="panel-heading">
		        		<h4 class="panel-title">
		        			<a data-toggle="collapse" class="collapsed" data-target="#collapseLAInstallers">CTT Local Agent Installers</a>
		        		</h4>
		        	</div>
					<!-- <div class="panel-heading"><strong>Local Agent Installers panel</strong>  </div>  -->
					<div id="collapseLAInstallers" class="panel-collapse collapse">
						<div class="panel-body">
							<!-- Available packages -->
							<div>
								<h4>Available installers</h4>
								<div class="list-group" id="laInstallers">
								</div>
							</div>
							<!-- Standard Form -->
							<h4>Select file from your computer <small> Uploading an already existing installer will cause overriding</small></h4>
							<form method="post" enctype="multipart/form-data" id="laUploadForm">
								<div class="form-inline">
									<div class="form-group">
										<input type="file" name="laFile" id="laFile" accept=".exe">
									</div>
									<button type="submit" disabled class="btn btn-sm btn-custom disabled" id="laUploadSubmit">Upload file</button>
								</div>
							</form>
													
							<!-- Upload Finished -->
					        <div>
					        	<h4>Processed files</h4>
					        	<div class="list-group" id="laUploadResults">
					         	</div>
					        </div>
						</div>
					</div>
				</div>
	        </div>
        </div>
        
        <!-- Modal forms -->
        <div>
	        <!-- Select tccl certification release form -->
	        <div id ="newTcclModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	        	<div class="modal-dialog">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<form:form method="GET" id="newTcclForm" action="admin/add" modelAttribute="newTccl">
	        					<div class="form-group">
	        						<label path="idCertrel" for="tccl-certrel" class="control-label">Certification Release</label>
	        						<form:select path="idCertrel" class="form-control" id="tccl-certrel">
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
	        				</form:form>
	        			</div>
	        			<div class="modal-footer">
	        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        				<button id="createContinue" type="submit" class="btn btn-custom" data-dismiss="modal" data-toggle="modal" data-target="#testCasesModal">Continue</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
	        
	        <!-- TestCases Modal -->
	        <div id="testCasesModal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">
	        	<div class="modal-dialog modal-lg">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<table id="tcTable" class="table table-hover">
	        					<!-- <thead class="scroll-thead">
	        						<tr class="scroll-tr">  -->
	        					<thead>
	        						<tr>
	        							<th class="hide">Test Case ID</th>
	        							<th width="25%">Test Case</th>
	        							<th width="60%">Test Case Description</th>
	        							<th width="10%">Type</th>
	        							<th width="10%">Enable</th>
	        						</tr>
	        					</thead>
	        					<!-- <tbody id="tcBody" class="scroll-tbody">  -->
	        					<tbody id="tcBody">
	        					</tbody>
	        				</table>
	        			</div>
	        			<div class="modal-footer">
	        				<button id="tcBack" class="btn btn-default" data-dismiss="modal" data-toggle="modal" data-target="#newTcclModal">Back</button>
	        				<button id="createTccl" class="btn btn-custom">Create TCCL</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
	        
	         <!-- View TestCases Modal -->
	        <div id="viewTcclModal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">
	        	<div class="modal-dialog modal-lg">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<table id="viewTcTable" class="table table-hover">
	        					<!-- <thead class="scroll-thead">
	        						<tr class="scroll-tr">  -->
	        					<thead>
	        						<tr>
	        							<th class="hide">Test Case ID</th>
	        							<th width="20%">Test Case</th>
	        							<th width="60%">Test Case Description</th>
	        							<th width="10%">Type</th>
	        							<th width="10%">Enable</th>
	        						</tr>
	        					</thead>
	        					<!-- <tbody id="viewTcBody" class="scroll-tbody">  -->
	        					<tbody id="viewTcBody">
	        					</tbody>
	        				</table>
	        			</div>
	        			<div class="modal-footer">
	        				<button id="tcBack" class="btn btn-default" data-dismiss="modal">Back</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
	        
	        <!-- Edit TestCases Modal -->
	        <div id="editTcclModal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">
	        	<div class="modal-dialog modal-lg">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<table id="editTcTable" class="table table-hover">
	        					<!-- <thead class="scroll-thead">
	        						<tr class="scroll-tr">  -->
	        					<thead>
	        						<tr>
	        							<th class="hide">Test Case ID</th>
	        							<th width="20%">Test Case</th>
	        							<th width="60%">Test Case Description</th>
	        							<th width="10%">Type</th>
	        							<th width="10%">Enable</th>
	        						</tr>
	        					</thead>
	        					<!-- <tbody id="editTcBody" class="scroll-tbody">  -->
	        					<tbody id="editTcBody">
	        					</tbody>
	        				</table>
	        			</div>
	        			<div class="modal-footer">
	        				<button id="tcBack" class="btn btn-default" data-dismiss="modal">Back</button>
	        				<button id="editConfirm" class="btn btn-custom">Save Changes</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
	        
	         <!-- Delete TCCL modal -->
	        <div id ="delTcclModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	        	<div class="modal-dialog">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<h4>Are you sure you want to delete this Test Case Control List?</h4>
	        			</div>
	        			<div class="modal-footer">
	        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        				<button id="deleteConfirm" type="button" class="btn btn-custom" data-dismiss="modal">Delete TCCL</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
        </div>
        
        <!-- Processing... modal -->
        <div class="modal" tabindex="-1" role="dialog" aria-hidden="true" id="pleaseWaitDialog" data-backdrop="static" data-keyboard="false">
			<div class="modal-dialog">
		        <div class="modal-content">
		        	<div class="modal-header">
		        		<h1>Uploading...</h1>
		        	</div>
		        	<div class="modal-body">
		        		<div class="progress progress-striped active">
			        			<div class="progress-bar" role="progressbar" aria-valuenow="100"
									aria-valuemin="0" aria-valuemax="100" style="width:100%">
		        				</div>
			        	</div>
		        	</div>
		        </div>
			</div>
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <!-- <script src="resources/jquery-validation/1.13.1/js/jquery.validate.min.js"></script>
		<script src="resources/jquery-validation/1.13.1/js/additional-methods.min.js"></script>  -->
	
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				$('#logoutForm').submit();
			}
		</script>
		<script>
			var table = null;
			$(document).ready(function() {
				$('#title').text("ADMINISTRATION TASKS");
				
				$('#table').dataTable({
					scrollY: ($(window).height()/2),
					columnDefs: [
						{visible: false, searchable: false, targets: 0},
					],
					order: [0, 'asc']
				});
				
				$.ajax({
					cache: false,
					type: 'GET',
					url: 'admin/availablePackages',
					success: function (data) {
						$('#tcPackages').html('');
						$.each(data, function(i, file) {
							var split = file.split(": C");
							$('#tcPackages').append('<a class=\"list-group-item\"><strong>'+split[0]+"</strong> <small>C"+split[1]+'</small></a>');
						});
					}
				});
				
				$.ajax({
					cache: false,
					type: 'GET',
					url: 'admin/availableInstallers',
					success: function (data) {
						$('#laInstallers').html('');
						$.each(data, function(i, file) {
							$('#laInstallers').append('<a class=\"list-group-item\">'+file+'</a>');
						});
					}
				});
				
		        $('#file').on('change', function() {
	                if ($(this).val()) {
	                    $('#tcUploadSubmit').prop('disabled',false);
	                    $('#tcUploadSubmit').removeClass('disabled');
	                } else {
	                	$('#tcUploadSubmit').prop('disabled',true);
	                    $('#tcUploadSubmit').addClass('disabled');
	                } 
		        });
		        
		        $('#laFile').on('change', function() {
	                if ($(this).val()) {
	                    $('#laUploadSubmit').prop('disabled',false);
	                    $('#laUploadSubmit').removeClass('disabled');
	                } else {
	                	$('#laUploadSubmit').prop('disabled',true);
	                    $('#laUploadSubmit').addClass('disabled');
	                } 
		        });
		        
		        //$('#collapseTccl').collapse('hide');
			});
		</script>
		<script>
			$('#newTcclButton').on('click', function() {
				$.ajax({
					cache: false,
					type: 'GET',
					url : 'admin/crVersions',
					success : function (data) {
						$('#tccl-certrel').empty();
						$.each(data, function(i, cr) {
							if (i != data.length -1) 
								$('#tccl-certrel').append("<option value=\""+cr.idCertrel+"\">"+cr.name+"</option>");
							else
								$('#tccl-certrel').append("<option selected value=\""+cr.idCertrel+"\">"+cr.name+"</option>");
						});
					}
				});
			});
			
			$('#createContinue').on('click', function(){
										
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'admin/testcases',
					data : {
						idCertRel : $('#tccl-certrel').val()
					},
					success: function (data) {
						
						var selector = "<td width=\"10%\"><select class=\"form-control\">"
							+"<option value=\"A\">A</option>"
							+"<option value=\"B\">B</option>"
							+"<option value=\"D\">D</option>"
							+"<option value=\"N\">N</option>"
							+"<option value=\"P\">P</option></td>";
	
						$('#tcBody').empty();
						$.each(data, function(i, tc) {
							$('#tcBody').append("<tr><td class=\"hide\">"+tc[0]+"</td><td width=\"20%\">"
									+tc[1]+"</td><td width=\"60%\">"+tc[8]+selector
									+"</td><td width=\"10%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\">");
						});
						
						$('#testCasesModal').show();
						
						if(!$('#tcTable').hasClass("dataTable")) {
							table = $('#tcTable').dataTable({
								paging: false,
								//searching: false,
								"sDom": '<"top">rt<"bottom"flp><"clear">',
								scrollY: ($(window).height()/2),
								columnDefs: [        
									{ orderable: false, targets: [3, 4]},
								],
								order: [0, 'asc']
							});				
						}
						
						$('#tcBody').find('.is_checkbox').prop('checked', true);
						$('#tcBody').find('.is_checkbox').prop('disabled', true);
						
						$('.form-control').change(function() {
							var selected = $(this).find("option:selected").val();
							var state = ((selected=="A")||(selected=="B"));
							$(this).closest('tr').find('[type=checkbox]').prop('checked', state);
						});
				   }
				});
			});
			
			$('#createTccl').on('click', function() {

				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
				var array = new Array();
				var certrel = new Object();
				certrel.id = $('#tccl-certrel option:selected').val();
				certrel.name = $('#tccl-certrel option:selected').html();
				
				var MyRows = $('#tcTable').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var json = new Object();
					json.id = $(MyRows[i]).find('td:eq(0)').html();
					json.type = $(MyRows[i]).find('.form-control option:selected').html();
					json.enabled = $(MyRows[i]).find('.is_checkbox').is(':checked');
					array.push(json);
				}

				$.ajax({
					type : 'POST',
					url : 'admin/tccl/add',
					beforeSend: function(xhr) {
			            xhr.setRequestHeader(header, token);
			        },
					data : {
						certrel : certrel,
						json : array
					},
					success: function (tccl) {
						$("#testCasesModal").modal('hide');
						
						Number.prototype.padLeft = function(base,chr){
						   var  len = (String(base || 10).length - String(this).length)+1;
						   return len > 0? new Array(len).join(chr || '0')+this : this;
						}
						
						var cd = new Date(tccl.createdDate);
						cdFormat = [ cd.getFullYear(),
						             (cd.getMonth()+1).padLeft(),
				                    cd.getDate().padLeft()].join('-')+
				                    ' ' +
				                  [ cd.getHours().padLeft(),
				                    cd.getMinutes().padLeft(),
				                    cd.getSeconds().padLeft()].join(':');
						var md = new Date(tccl.modifiedDate);
						mdFormat = [ md.getFullYear(),
						             (md.getMonth()+1).padLeft(),
				                    md.getDate().padLeft()].join('-')+
				                    ' ' +
				                  [ md.getHours().padLeft(),
				                    md.getMinutes().padLeft(),
				                    md.getSeconds().padLeft()].join(':');
						
					    /*var row = '<td class=\"hide\">'+tccl.idTccl+'</td>'
		        			+'<td>'+tccl.name+'</td><td>'+cdFormat+'</td>'
							+'<td>'+mdFormat+'</td>'
							+'<td>'+tccl.nameCertrel+'</td>';*/
						
					    //$('#table').find('tbody').append(row);
					    
					    $('#table').DataTable().row.add([tccl.idTccl,tccl.name,cdFormat,mdFormat,tccl.nameCertrel]).draw();
					    
					    $("#table tbody tr:last").click(function(){
						   	
							if($(this).hasClass("selected")) {
								$(this).removeClass('selected');
								sessionStorage.removeItem("idTccl");
								
								$('#viewButton').addClass('disabled');
								$('#viewButton').prop("disabled", true);
								$('#deleteButton').addClass('disabled');
								$('#deleteButton').prop("disabled", true);
								$('#editButton').addClass('disabled');
								$('#editButton').prop("disabled", true);
							} else { 
								$(this).addClass('selected').siblings().removeClass('selected');    
								var id = $('#table').DataTable().row(this).data()[0];
								
								//var id=$(this).find('td:first').html();
							   	
							   	sessionStorage.setItem("idTccl",id);
				
							   	$('#viewButton').removeClass('disabled');
								$('#viewButton').prop("disabled", false);
								$('#deleteButton').removeClass('disabled');
								$('#deleteButton').prop("disabled", false);
								$('#editButton').removeClass('disabled');
								$('#editButton').prop("disabled", false);
							}
						});
				   }
				});
			});
			
			$('#deleteConfirm').on('click', function(e){
				
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
			    
			    $.ajax({
						type : 'POST',
						url : 'admin/tccl/delete',
						beforeSend: function(xhr) {
				            xhr.setRequestHeader(header, token);
				        },
						data : {
							idTccl : sessionStorage.getItem("idTccl")
						},
						success: function () {
							var MyRows = $('#table').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								//if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idTccl")) {
								if(($('#table').DataTable().row($(MyRows[i])).data()[0])==sessionStorage.getItem("idTccl")) {	
									var table = $('#table').DataTable();
									table.row($(MyRows[i])).remove().draw();
									
									/*$(MyRows[i]).fadeOut(400, function() {
										$(MyRows[i]).remove();
									});*/
								}
							}
							
							sessionStorage.removeItem("idTccl");
							
							$('#viewButton').addClass('disabled');
							$('#viewButton').prop("disabled", true);
							$('#deleteButton').addClass('disabled');
							$('#deleteButton').prop("disabled", true);
							$('#editButton').addClass('disabled');
							$('#editButton').prop("disabled", true);
					   }
				});
			});
			
			$('#editButton').on('click', function(){
				
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'admin/tccl/edit',
					data : {
						idTccl : sessionStorage.getItem("idTccl")
					},
					success: function (data) {
						
						var selector = "<td width=\"10%\"><select class=\"form-control\">"
							+"<option value=\"A\">A</option>"
							+"<option value=\"B\">B</option>"
							+"<option value=\"D\">D</option>"
							+"<option value=\"N\">N</option>"
							+"<option value=\"P\">P</option></td>";
	
						$('#editTcBody').empty();
						$.each(data, function(i, tc) {
							$('#editTcBody').append("<tr><td class=\"hide\">"+tc[0]+"</td><td width=\"20%\">"
									+tc[1]+"</td><td width=\"60%\">"+tc[2]+selector
									+"</td><td width=\"10%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\">");
							$('#editTcBody tr:last').find('.form-control').val(tc[3]);
							$('#editTcBody tr:last').find('.is_checkbox').prop('checked',tc[4]);
						});
						
						$('#editTcclModal').show();
						
						if(!$('#editTcTable').hasClass("dataTable")) {
							table = $('#editTcTable').dataTable({
								paging: false,
								//searching: false,
								"sDom": '<"top">rt<"bottom"flp><"clear">',
								scrollY: ($(window).height()/2),
								columnDefs: [        
									{ orderable: false, targets: [3, 4]},
								],
								order: [0, 'asc']
							});				
						}
						
						$('#editTcBody').find('.is_checkbox').prop('disabled', true);
						
						$('.form-control').change(function() {
							var selected = $(this).find("option:selected").val();
							var state = ((selected=="A")||(selected=="B"));
							$(this).closest('tr').find('[type=checkbox]').prop('checked', state);
						});
				   }
				});
			});
			
			$('#editConfirm').on('click', function() {
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
				var array = new Array();
				
				var MyRows = $('#editTcTable').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var json = new Object();
					json.id = $(MyRows[i]).find('td:eq(0)').html();
					json.type = $(MyRows[i]).find('.form-control option:selected').html();
					json.enabled = $(MyRows[i]).find('.is_checkbox').is(':checked');
					array.push(json);
				}

				$.ajax({
					type : 'POST',
					url : 'admin/tccl/edit',
					beforeSend: function(xhr) {
			            xhr.setRequestHeader(header, token);
			        },
					data : {
						idTccl : sessionStorage.getItem("idTccl"),
						json : array
					},
					success: function (tccl) {
						$("#editTcclModal").modal('hide');
						
						Number.prototype.padLeft = function(base,chr){
						   var  len = (String(base || 10).length - String(this).length)+1;
						   return len > 0? new Array(len).join(chr || '0')+this : this;
						}
						
						var md = new Date(tccl.modifiedDate);
						mdFormat = [ md.getFullYear(),
						             (md.getMonth()+1).padLeft(),
				                    md.getDate().padLeft()].join('-')+
				                    ' ' +
				                  [ md.getHours().padLeft(),
				                    md.getMinutes().padLeft(),
				                    md.getSeconds().padLeft()].join(':');
						
						var MyRows = $('#table').find('tbody').find('tr');
						for (var i = 0; i < MyRows.length; i++) {
							/*if($(MyRows[i]).find('td:eq(0)').html()==tccl.idTccl) {
								$(MyRows[i]).find('td:eq(3)').html(mdFormat);
							}*/
							var data = $('#table').DataTable().row($(MyRows[i])).data();

							if(data[0]==tccl.idTccl) {
								/*$(MyRows[i]).find('td:eq(2)').html(mdFormat);
								$('#table').dataTable().draw();*/
								$('#table').dataTable().fnUpdate(mdFormat, $(MyRows[i]), 3);//.fnAdjustColumnSizing();
								$('#table').dataTable().fnAdjustColumnSizing();
							}
						}
				   }
				});
			});
			
			$('#viewButton').on('click', function(){
				
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'admin/tccl/edit',
					data : {
						idTccl : sessionStorage.getItem("idTccl")
					},
					success: function (data) {
	
						$('#viewTcBody').empty();
						$.each(data, function(i, tc) {
							$('#viewTcBody').append("<tr><td class=\"hide\">"+tc[0]+"</td><td width=\"20%\">"
									+tc[1]+"</td><td width=\"60%\">"+tc[2]+"</td>"
									+"<td width=\"10%\">"+tc[3]
									+"</td><td width=\"10%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\">");
							$('#viewTcBody tr:last').find('.is_checkbox').prop('checked',tc[4]);
						});
						
						$('#viewTcclModal').show();
						
						if(!$('#viewTcTable').hasClass("dataTable")) {
							table = $('#viewTcTable').dataTable({
								paging: false,
								//searching: false,
								"sDom": '<"top">rt<"bottom"flp><"clear">',
								scrollY: ($(window).height()/2),
								columnDefs: [        
									{ orderable: false, targets: [3, 4]},
								],
								order: [0, 'asc']
							});				
						}
						
						$('#viewTcBody').find('.is_checkbox').prop('disabled', true);
				   }
				});
			});
			
			function comprobarDT()
			{
				console.log(table);
				
			}
		</script>
		
		<!-- Row selector script -->
		<script>
			$("#table tbody tr").click(function(){
			   	
				if($(this).hasClass("selected")) {
					$(this).removeClass('selected');
					sessionStorage.removeItem("idTccl");
					
					$('#viewButton').addClass('disabled');
					$('#viewButton').prop("disabled", true);
					$('#deleteButton').addClass('disabled');
					$('#deleteButton').prop("disabled", true);
					$('#editButton').addClass('disabled');
					$('#editButton').prop("disabled", true);
				} else { 
					$(this).addClass('selected').siblings().removeClass('selected');    
					var id = $('#table').DataTable().row(this).data()[0];
					
					//var id=$(this).find('td:first').html();
				   	
				   	sessionStorage.setItem("idTccl",id);
	
				   	$('#viewButton').removeClass('disabled');
					$('#viewButton').prop("disabled", false);
					$('#deleteButton').removeClass('disabled');
					$('#deleteButton').prop("disabled", false);
					$('#editButton').removeClass('disabled');
					$('#editButton').prop("disabled", false);
				}
			});
		</script>
		
		<script>
			$('#tcUploadSubmit').on('click', function(e) {
				e.preventDefault();
				
				//if($('#tcUploadForm').valid()) {
				
					$("#pleaseWaitDialog").modal('show');
					
					var oMyForm = new FormData();
					oMyForm.append("file", $('#file')[0].files[0]);
					oMyForm.append("description", $('#tcDescription').val());
					
					var token = $("meta[name='_csrf']").attr("content");
					var header = $("meta[name='_csrf_header']").attr("content");
					
					$.ajax({
						url: 'admin/uploadTCP',
						beforeSend: function(xhr) {
				            xhr.setRequestHeader(header, token);
				        },
						data: oMyForm,
						dataType: "json",
						processData: false,
						contentType: false,
						type: 'POST',
						success: function(data) {
	
							$("#pleaseWaitDialog").modal('hide');
							if (data.result=="Success") {
								$('#tcUploadResults').append('<a class=\"list-group-item list-group-item-success\"><span class=\"badge alert-success pull-right\">Success</span>'+data.message+'</a>');
								
								$.ajax({
									cache: false,
									type: 'GET',
									url: 'admin/availablePackages',
									success: function (data) {
										$('#tcPackages').html('');
										$.each(data, function(i, file) {
											var split = file.split(": C");
											$('#tcPackages').append('<a class=\"list-group-item\"><strong>'+split[0]+"</strong> <small>C"+split[1]+'</small></a>');
										});
									}
								});
							} else {
								$('#tcUploadResults').append('<a class=\"list-group-item list-group-item-danger\"><span class=\"badge alert-danger pull-right\">Fail</span>'+data.message+'</a>');
							}
							$('#file').val('');
							$('#tcUploadSubmit').prop('disabled',true);
		                    $('#tcUploadSubmit').addClass('disabled');
						}
					});
				//}
			});
			
			$('#laUploadSubmit').on('click', function(e) {
				e.preventDefault();
				
				$("#pleaseWaitDialog").modal('show');
				
				var oMyForm = new FormData();
				oMyForm.append("file", $('#laFile')[0].files[0]);
				
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
				
				$.ajax({
					url: 'admin/uploadLA',
					beforeSend: function(xhr) {
			            xhr.setRequestHeader(header, token);
			        },
					data: oMyForm,
					dataType: "json",
					processData: false,
					contentType: false,
					type: 'POST',
					success: function(data) {

						if (data.result=="Success") {
							$('#laUploadResults').append('<a class=\"list-group-item list-group-item-success\"><span class=\"badge alert-success pull-right\">Success</span>'+data.message+'</a>');
							
							$.ajax({
								cache: false,
								type: 'GET',
								url: 'admin/availableInstallers',
								success: function (data) {
									
									$("#pleaseWaitDialog").modal('hide');
									$('#laInstallers').html('');
									$.each(data, function(i, file) {
										$('#laInstallers').append('<a class=\"list-group-item\">'+file+'</a>');
									});
								}
							});
						} else {
							$('#laUploadResults').append('<a class=\"list-group-item list-group-item-danger\"><span class=\"badge alert-danger pull-right\">Fail</span>'+data.message+'</a>');
						}
						$('#laFile').val('');
						$('#laUploadSubmit').prop('disabled',true);
	                    $('#laUploadSubmit').addClass('disabled');
					}
				});
			});
			
			/*$('#collapseTccl').on('shown.bs.collapse', function() {
				$('#table').dataTable().fnAdjustColumnSizing();
			});*/
		</script>
		
		<!-- Validation scripts -->
		<script>
			/*$('#tcUploadForm').validate({
				rules: {
					file: {
						required: true,
						accept: "*.jar"
					},
					tcDescription: {
						required: true,
						maxlength: 255,
					}
				},
				messages: {
					file: {
						required: "Please select a file!",
					},
					tcDescription: {
						required: "Please enter a description!",
						maxlength: "Description must have a max of 255 characters!"
					}
				}
			});*/
		</script>
    </body>
</html>