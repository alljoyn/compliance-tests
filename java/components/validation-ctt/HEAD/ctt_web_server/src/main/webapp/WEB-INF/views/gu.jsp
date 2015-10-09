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
				    	<h4 id="selectedProject" class="pull-left"></h4>
				    	<c:if test="${pageContext.request.userPrincipal.name != null}">
							<h4>
								Welcome : ${pageContext.request.userPrincipal.name} | <a
								href="javascript:goldenUnit.logout()"> Logout</a>
							</h4>
						</c:if>
				    </div>
				    
				    <!-- GUs table -->
			        <div class="row">
			        	<div>       
					       	<table id="table" class="table table-hover">
					       		<!-- <thead class="scroll-thead">
					       			<tr class="scroll-tr">  -->
					       		<thead>
					       			<tr>
							        	<th class="hide">GU ID</th>	
							        	<th>GU Name</th>
							        	<th>Created</th>
							        	<th>Modified</th>
							        	<th>Category</th>
							        	<th>OEM</th>
							        	<th>Model</th>
							        	<th>SW Ver</th>
							        	<th>HW Ver</th>
							        	<th>Description</th>
							        </tr>
							    </thead>
					        	<!-- <tbody class="scroll-tbody">  -->
					        	<tbody>
									<c:forEach var="gu" items="${guList}" varStatus="status">
										<tr>			
											<td class="hide">${gu.idGolden}</td>
							        		<td>${gu.name}</td>
											<td><fmt:formatDate value="${gu.createdDate}"
												pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<td><fmt:formatDate value="${gu.modifiedDate}"
												pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<c:forEach var="category" items="${categoryList}" varStatus="status">
												<c:if test="${category.idCategory==gu.category}">
													<td>${category.name}</td>
												</c:if>
											</c:forEach>
											<td>${gu.manufacturer}</td>
											<td>${gu.model}</td>
											<td>${gu.swVer}</td>
											<td>${gu.hwVer}</td>
											<td>${gu.description}</td>								
							        	</tr>
									</c:forEach>
								</tbody>        	
					       	</table>
					    </div>
				     </div>
				
					<!-- Action buttons -->
					<div class="row" align="left">
			        	<button type="button" class="btn btn-default btn-lg" data-toggle="modal" data-target="#newGuModal">New GU</button>
			        	<button id="editButton" type="button" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#editGuModal">Edit GU</button>   
			        	<button id="deleteButton" type="button" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#delGu">Delete GU</button>
			        	<p class="pull-right" id="guSelect">You need to select at least 3 Golden Units</p>
			        </div>
			        
			        <!-- Navigation buttons -->
			        <div class="row" align="left">
			        	<a id="prevButton" type="button" class="btn btn-custom btn-lg" href="dut">« Previous Step</a>
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
		        	<form:form method="POST" id="nextForm" action="gu/save" modelAttribute="newProject">
		        		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
		        		<form:input type="hidden" id="gUnits" name="gUnits" path="gUnits" value=""/>
		        	</form:form>
		        </div>
		        
		        <!-- Modal forms -->
		        <div>
		        	<!-- New GU form -->
			        <div id ="newGuModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			        	<div class="modal-dialog">
			        		<div class="modal-content">
			        			<div class="modal-body">
			        				<form:form method="POST" id="newGuForm" action="gu/add" modelAttribute="newGu">
			        					<div class="form-group">
			        						<label path="name" for="gu-name" class="control-label">Name</label>
			        						<form:input path="name" type="text" class="form-control" id="gu-name"/>
			        					</div>
			        					<div class="form-group">
			        						<label path="category" for="gu-category" class="control-label">Category</label>
			        						<form:select path="category" class="form-control" id="gu-category">
			        							<c:forEach var="category" items="${categoryList}" varStatus="status">
													<option value="${category.idCategory}">${category.name}</option>
												</c:forEach>
			        						</form:select>
			        					</div>
			        					<div class="form-group">
			        						<label path="manufacturer" for="gu-manufacturer" class="control-label">OEM</label>
			        						<form:input path="manufacturer" type="text" class="form-control" id="gu-manufacturer"/>
			        					</div>
			        					<div class="form-group">
			        						<label path="model" for="gu-model" class="control-label">Model</label>
			        						<form:input path="model" type="text" class="form-control" id="gu-model"/>
			        					</div>
			        					<div class="form-group">
			        						<label path="swVer" for="gu-sw-ver" class="control-label">Software Version</label>
			        						<form:input path="swVer" type="text" class="form-control" id="gu-sw-ver"/>
			        					</div>
			        					<div class="form-group">
			        						<label path="hwVer" for="gu-hw-ver" class="control-label">Hardware Version</label>
			        						<form:input path="hwVer" type="text" class="form-control" id="gu-hw-ver"/>
			        					</div>
			        					<div class="form-group">
			        						<label path="description" for="gu-description" class="control-label">Description</label>
			        						<form:input path="description" type="text" class="form-control" id="gu-description"/>
			        					</div>
			        					<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
			        				</form:form>
			        			</div>
			        			<div class="modal-footer">
			        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			        				<button id="createGu" type="submit" class="btn btn-custom">Create GU</button>
			        			</div>
			        		</div>
			        	</div>
			        </div>
			        
			        <!-- Edit GU form -->
			        <div id ="editGuModal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			        	<div class="modal-dialog">
			        		<div class="modal-content">
			        			<div class="modal-body">
			        				<form:form method="POST" id="editGuForm" action="gu/edit" modelAttribute="newGu">
			        					<form:input type="hidden" id="edit-id" path="idGolden"/>
			        					<div class="form-group">
			        						<label path="name" for="edit-name" class="control-label">Name</label>
			        						<form:input path="name" type="text" class="form-control" id="edit-name"/>
			        					</div>
			        					<div class="form-group">
			        						<label path="category" for="edit-category" class="control-label">Category</label>
			        						<form:select path="category" class="form-control" id="edit-category">
			        							<c:forEach var="category" items="${categoryList}" varStatus="status">
													<option value="${category.idCategory}">${category.name}</option>
												</c:forEach>
			        						</form:select>
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
			        						<label path="swVer" for="edit-sw-ver" class="control-label">Software Version</label>
			        						<form:input path="swVer" type="text" class="form-control" id="edit-sw-ver"/>
			        					</div>
			        					<div class="form-group">
			        						<label path="hwVer" for="edit-hw-ver" class="control-label">Hardware Version</label>
			        						<form:input path="hwVer" type="text" class="form-control" id="edit-hw-ver"/>
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
			        
			        <!-- Delete GU modal -->
			        <div id ="delGu" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			        	<div class="modal-dialog">
			        		<div class="modal-content">
			        			<div class="modal-body">
			        				<h4>This GU might have associated projects. Are you sure you want to delete it?</h4>
			        			</div>
			        			<div class="modal-footer">
			        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
			        				<button id="deleteConfirm" type="button" class="btn btn-custom" data-dismiss="modal">Delete GU</button>
			        			</div>
			        		</div>
			        	</div>
			        </div>
		        </div>
	        </div>
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
		<script src="resources/jquery-validation/1.13.1/js/jquery.validate.min.js"></script>
		<script src="resources/jquery-validation/1.13.1/js/additional-methods.min.js"></script>
		<script src="resources/js/gu.js"></script>
		
		<script>
			$(document).ready(function()
			{
				goldenUnit.init();
			});
			
		</script>
    </body>
</html>