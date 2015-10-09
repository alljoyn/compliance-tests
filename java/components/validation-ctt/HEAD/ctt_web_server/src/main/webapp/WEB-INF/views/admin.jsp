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
	    <div id="wrap">
		  	<div id="main" class="container clear-top">
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
								href="javascript:admin.logout()"> Logout</a>
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
									
									<!-- TCCLs table -->      
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
							<div id="collapseTCPackages" class="panel-collapse collapse">
								<div class="panel-body">
									<!-- Available packages -->
									<div class="col-md-6">
										<h4>Available packages</h4>
										<div class="tree" id="tcPackages">
										</div>
									</div>
									<div class="col-md-6">
										<!-- Standard Form -->
										<h4>Select file from your computer</h4>
										<h6>Uploading an already existing package will cause overriding</h6>
										<form method="post" enctype="multipart/form-data" id="tcUploadForm">
											<div class="form-inline">
												<div class="form-group">
													<input type="file" name="testCasesPackage" id="testCasesPackage" accept=".jar">
												</div>
												<button type="submit" disabled class="btn btn-sm btn-custom disabled" id="uploadPackageButton">Upload file</button>
											</div>
											<div class="form-group">
												<label for="tcDescription" class="control-label">Update package description</label>
												<input type="text" class="form-control" id="tcDescription" name="tcDescription">
											</div>
										</form>
																
										<!-- Upload Finished -->
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
							<div id="collapseLAInstallers" class="panel-collapse collapse">
								<div class="panel-body">
									<!-- Available packages -->
									<div class="col-md-6">
										<h4>Available installers</h4>
										<div class="tree" id="laInstallers">
										</div>
									</div>
									<div>
										<!-- Standard Form -->
										<h4>Select file from your computer</h4>
										<h6> Uploading an already existing installer will cause overriding</h6>
										<form method="post" enctype="multipart/form-data" id="laUploadForm">
											<div class="form-inline">
												<div class="form-group">
													<input type="file" name="localAgentInstaller" id="localAgentInstaller" accept=".exe">
												</div>
												<button type="submit" disabled class="btn btn-sm btn-custom disabled" id="uploadInstallerButton">Upload file</button>
											</div>
										</form>
																
										<!-- Upload Finished -->
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
	  		</div>
		</div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/js/admin.js"></script>
	
		<!-- Initialization script -->
		<script>
			$(document).ready(function()
			{				
				admin.init();
			});
		</script>
    </body>
</html>