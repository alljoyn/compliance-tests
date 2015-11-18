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
				<!-- Main -->
		    	<div id="main" class="container-fluid">
		    		<jsp:include page="/WEB-INF/views/header.jsp"/>
				    		    
				    <!-- Test Cases Control List panel -->
				    <div class="row" align="left" style="margin-top: 25px;">
				    	<div class="col-xs-12">
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
							        	<button id="newTcclButton" class="btn btn-default" data-toggle="modal" data-target="#newTcclModal"><span class="glyphicon glyphicon-plus"></span> New TCCL</button>
							        	<button id="viewButton" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#viewTcclModal"><span class="glyphicon glyphicon-eye-open"></span> View TCCL</button>
							        	<button id="editButton" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#editTcclModal"><span class="glyphicon glyphicon-pencil"></span> Edit TCCL</button>
							        	<button id="deleteButton" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#delTcclModal"><span class="glyphicon glyphicon-trash"></span> Delete TCCL</button>
							        </div>
							    </div>
						    </div>
						</div>
						</div>
					</div>
				
			        <!-- Test Cases Packages panel -->
			        <div class="row" align="left">
			        	<div class="col-xs-12">
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
												<button type="submit" disabled class="btn btn-sm btn-custom disabled" id="uploadPackageButton"><span class="glyphicon glyphicon-upload"></span> Upload file</button>
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
			        </div>
			        
			        <!-- Local Agent Installers panel -->
			        <div class="row" align="left">
			        	<div class="col-xs-12">
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
									<div class="col-md-6">
										<!-- Standard Form -->
										<h4>Select file from your computer</h4>
										<h6> Uploading an already existing installer will cause overriding</h6>
										<form method="post" enctype="multipart/form-data" id="laUploadForm">
											<div class="form-inline">
												<div class="form-group">
													<input type="file" name="localAgentInstaller" id="localAgentInstaller" accept=".exe">
												</div>
												<button type="submit" disabled class="btn btn-sm btn-custom disabled" id="uploadInstallerButton"><span class="glyphicon glyphicon-upload"></span> Upload file</button>
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
			        
			        <!-- ICS Management Panel -->
				    <div class="row" align="left">
				    	<div class="col-xs-12">
						<div class="panel panel-default">
				        	<div class="panel-heading">
				        		<h4 class="panel-title">
				        			<a data-toggle="collapse" class="collapsed" data-target="#collapseIcsManagement">ICS Management</a>
				        		</h4>
				        	</div>
				        	<div id="collapseIcsManagement" class="panel-collapse collapse">
								<div class="panel-body">
									<div class="row">
									<div class="col-md-6">
									<form:form method="POST" id="newIcsForm" action="admin/saveIcs" modelAttribute="newIcs">
										<div class="form-group col-md-6">
											<label for="ics-name" class="control-label">Name (*)</label>
											<form:input path="name" type="text" class="form-control" id="ics-name"/>
										</div>
										<div class="form-group col-md-6">
											<label for="ics-service-group" class="control-label">Service Group (*)</label>
											<form:select path="serviceGroup" class="form-control" id="ics-service-group"></form:select>
										</div>
										<div class="form-group col-md-12">
											<label for="ics-scr" class="control-label">SCR Expression</label>
											<form:input path="scrExpression" class="form-control" id="ics-scr"></form:input>
										</div>
										<div class="form-group col-md-12">
											<label for="ics-description" class="control-label">Description (*)</label>
											<form:input path="description" type="text" class="form-control" id="ics-description"/>
										</div>
									</form:form>
									<p>(*) This is a mandatory field</p>
									<button id="addIcs" type="submit" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Save ICS</button>
									</div>
									<div class="col-md-6">
										<!-- ICS Saving Status -->
							        	<h4>Status</h4>
							        	<div class="list-group" id="icsSavingResults">
							         	</div>
									</div>
									</div>
							    </div>
						    </div>
						</div>
						</div>
					</div>
					
					<!-- IXIT Management Panel -->
				    <div class="row" align="left">
				    	<div class="col-xs-12">
						<div class="panel panel-default">
				        	<div class="panel-heading">
				        		<h4 class="panel-title">
				        			<a data-toggle="collapse" class="collapsed" data-target="#collapseIxitManagement">IXIT Management</a>
				        		</h4>
				        	</div>
				        	<div id="collapseIxitManagement" class="panel-collapse collapse">
								<div class="panel-body">
									<div class="row">
									<div class="col-md-6">
									<form:form method="POST" id="newIxitForm" action="admin/saveIxit" modelAttribute="newIxit">
										<div class="form-group col-md-6">
											<label for="ixit-name" class="control-label">Name (*)</label>
											<form:input path="name" type="text" class="form-control" id="ixit-name"/>
										</div>
										<div class="form-group col-md-6">
											<label for="ixit-service-group" class="control-label">Service Group (*)</label>
											<form:select path="serviceGroup" class="form-control" id="ixit-service-group"></form:select>
										</div>
										<div class="form-group col-md-12">
											<label for="ixit-value" class="control-label">Default Value</label>
											<form:input path="value" class="form-control" id="ixit-value"></form:input>
										</div>
										<div class="form-group col-md-12">
											<label for="ixit-description" class="control-label">Description (*)</label>
											<form:input path="description" type="text" class="form-control" id="ixit-description"/>
										</div>
									</form:form>
									<p>(*) This is a mandatory field</p>
									<button id="addIxit" type="submit" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Save IXIT</button>
									</div>
									<div class="col-md-6">
										<!-- IXIT Saving Status -->
							        	<h4>Status</h4>
							        	<div class="list-group" id="ixitSavingResults">
							         	</div>
									</div>
									</div>
							    </div>
						    </div>
						</div>
						</div>
					</div>
			        
			        <!-- Test Cases Management Panel -->
				    <div class="row" align="left">
				    	<div class="col-xs-12">
						<div class="panel panel-default">
				        	<div class="panel-heading">
				        		<h4 class="panel-title">
				        			<a data-toggle="collapse" class="collapsed" data-target="#collapseTcManagement">Test Cases Management</a>
				        		</h4>
				        	</div>
				        	<div id="collapseTcManagement" class="panel-collapse collapse">
								<div class="panel-body">
									<div class="row">
									<div class="col-md-6">
									<form:form method="POST" id="newTcForm" action="admin/saveTc" modelAttribute="newTestCase">
										<div class="form-group col-md-6">
											<label for="tc-name" class="control-label">Name (*)</label>
											<form:input path="name" type="text" class="form-control" id="tc-name"/>
										</div>
										<div class="form-group col-md-6">
											<label for="tc-type" class="control-label">Type (*)</label>
											<form:select path="type" class="form-control" id="tc-type">
												<option value="Conformance">Conformance</option>
												<option value="Interoperability">Interoperability</option>
											</form:select>
										</div>
										<div class="form-group col-md-6">
											<label for="tc-service-group" class="control-label">Service Group (*)</label>
											<form:select path="serviceGroup" class="form-control" id="tc-service-group"></form:select>
										</div>
										<form:input type="hidden" id="supportedCrs" name="supportedCrs" path="supportedCrs" value=""/>
										<div class="form-group col-md-6">
											<label for="tc-certification-releases" class="control-label">Supported Certification Releases (*)</label>
											<select id="tc-certification-releases" class="form-control" multiple>
											</select>
										</div>
										<div class="form-group col-md-12">
											<label for="tc-applicability" class="control-label">Applicability</label>
											<form:input path="applicability" class="form-control" id="tc-applicability"></form:input>
										</div>
										
										<div class="form-group col-md-12">
											<label for="tc-description" class="control-label">Description (*)</label>
											<form:input path="description" type="text" class="form-control" id="tc-description"/>
										</div>
									</form:form>
									<p>(*) This is a mandatory field</p>
									<button id="addTc" type="submit" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Save Test Case</button>
									</div>
									<div class="col-md-6">
										<!-- Test Case Saving Status -->
							        	<h4>Status</h4>
							        	<div class="list-group" id="tcSavingResults">
							         	</div>
									</div>
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
			        						</form:select>
			        					</div>			
			        				</form:form>
			        			</div>
			        			<div class="modal-footer">
			        				<button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
			        				<button id="createContinue" type="submit" class="btn btn-custom" data-dismiss="modal" data-toggle="modal" data-target="#testCasesModal"><span class="glyphicon glyphicon-ok"></span> Continue</button>
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
			        					<thead>
			        						<tr>
			        							<th class="hide">Test Case ID</th>
			        							<th width="25%">Test Case</th>
			        							<th width="60%">Test Case Description</th>
			        							<th width="10%">Type</th>
			        							<th width="10%">Enable</th>
			        						</tr>
			        					</thead>
			        					<tbody id="tcBody">
			        					</tbody>
			        				</table>
			        			</div>
			        			<div class="modal-footer">
			        				<button id="tcBack" class="btn btn-default" data-dismiss="modal" data-toggle="modal" data-target="#newTcclModal"><span class="glyphicon glyphicon-remove"></span> Back</button>
			        				<button id="createTccl" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Create TCCL</button>
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
			        					<thead>
			        						<tr>
			        							<th class="hide">Test Case ID</th>
			        							<th width="20%">Test Case</th>
			        							<th width="60%">Test Case Description</th>
			        							<th width="10%">Type</th>
			        							<th width="10%">Enable</th>
			        						</tr>
			        					</thead>
			        					<tbody id="viewTcBody">
			        					</tbody>
			        				</table>
			        			</div>
			        			<div class="modal-footer">
			        				<button id="tcBack" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-chevron-left"></span> Back</button>
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
			        					<thead>
			        						<tr>
			        							<th class="hide">Test Case ID</th>
			        							<th width="20%">Test Case</th>
			        							<th width="60%">Test Case Description</th>
			        							<th width="10%">Type</th>
			        							<th width="10%">Enable</th>
			        						</tr>
			        					</thead>
			        					<tbody id="editTcBody">
			        					</tbody>
			        				</table>
			        			</div>
			        			<div class="modal-footer">
			        				<button id="tcBack" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
			        				<button id="editConfirm" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Save Changes</button>
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
			        				<button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
			        				<button id="deleteConfirm" type="button" class="btn btn-custom" data-dismiss="modal"><span class="glyphicon glyphicon-ok"></span> Delete TCCL</button>
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
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/js/admin.js"></script>
        <script src="resources/js/common.js"></script>
	
		<!-- Initialization script -->
		<script>
			$(document).ready(function()
			{				
				admin.init();
			});
		</script>
    </body>
</html>