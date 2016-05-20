<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!-- Main -->	
<div class="col-xs-12">
	<!-- GUs table -->
	<div class="row">
      	<div class="col-xs-12">       
       		<table id="table" class="table table-hover">
	       		<thead>
	       			<tr>
			        	<th>GU ID</th>	
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
	        	<tbody>
				</tbody>        	
       		</table>
    	</div>
   	</div>

	<!-- Action buttons -->
	<div class="row" align="left">
		<div class="col-xs-12">
	       	<button type="button" class="btn btn-default" data-toggle="modal" data-target="#newGuModal"><span class="glyphicon glyphicon-plus"></span> New GU</button>
	       	<button id="editButton" type="button" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#editGuModal"><span class="glyphicon glyphicon-pencil"></span> Edit GU</button>   
	       	<button id="deleteButton" type="button" disabled class="btn btn-default disabled" data-toggle="modal" data-target="#delGu"><span class="glyphicon glyphicon-trash"></span> Delete GU</button>
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
 						<div class="row">
 							<div class="col-md-12">
 								<div class="form-group">
		 							<label path="name" for="gu-name" class="control-label">Name (*)</label>
		 							<form:input path="name" type="text" class="form-control" id="gu-name"/>
		 						</div>
 							</div>
 							<div class="col-md-12">
 								<div class="form-group">
		 							<label path="category" for="gu-category" class="control-label">Category</label>
		 							<form:select path="category" class="form-control" id="gu-category">
		 								<c:forEach var="category" items="${categoryList}" varStatus="status">
											<option value="${category.idCategory}">${category.name}</option>
										</c:forEach>
		 							</form:select>
		 						</div>
 							</div>
 							<div class="col-md-6">
 								<div class="form-group">
			 						<label path="manufacturer" for="gu-manufacturer" class="control-label">OEM (*)</label>
			 						<form:input path="manufacturer" type="text" class="form-control" id="gu-manufacturer"/>
			 					</div>
 							</div>
 							<div class="col-md-6">
 								<div class="form-group">
			 						<label path="model" for="gu-model" class="control-label">Model (*)</label>
			 						<form:input path="model" type="text" class="form-control" id="gu-model"/>
			 					</div>
 							</div>
 							<div class="col-md-6">
 								<div class="form-group">
			 						<label path="swVer" for="gu-sw-ver" class="control-label">Software Version (*)</label>
			 						<form:input path="swVer" type="text" class="form-control" id="gu-sw-ver"/>
			 					</div>
 							</div>
 							<div class="col-md-6">
 								<div class="form-group">
			 						<label path="hwVer" for="gu-hw-ver" class="control-label">Hardware Version (*)</label>
			 						<form:input path="hwVer" type="text" class="form-control" id="gu-hw-ver"/>
			 					</div>
 							</div>
 							<div class="col-md-12">
 								<div class="form-group">
			 						<label path="description" for="gu-description" class="control-label">Description (*)</label>
			 						<form:input path="description" type="text" class="form-control" id="gu-description"/>
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
	 				<button id="createCancel" type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
	 				<button id="createGu" type="submit" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Create GU</button>
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
    					<div class="row">
    						<div class="col-md-12">
	    						<div class="form-group">
		    						<label path="name" for="edit-name" class="control-label">Name (*)</label>
		    						<form:input path="name" type="text" class="form-control" id="edit-name"/>
		    					</div>
    						</div>
    						<div class="col-md-12">
		    					<div class="form-group">
		    						<label path="category" for="edit-category" class="control-label">Category</label>
		    						<form:select path="category" class="form-control" id="edit-category">
		    							<c:forEach var="category" items="${categoryList}" varStatus="status">
											<option value="${category.idCategory}">${category.name}</option>
										</c:forEach>
		    						</form:select>
		    					</div>
    						</div>
    						<div class="col-md-6">
		    					<div class="form-group">
		    						<label path="manufacturer" for="edit-manufacturer" class="control-label">OEM (*)</label>
		    						<form:input path="manufacturer" type="text" class="form-control" id="edit-manufacturer"/>
		    					</div>
    						</div>
    						<div class="col-md-6">
		    					<div class="form-group">
		    						<label path="model" for="edit-model" class="control-label">Model (*)</label>
		    						<form:input path="model" type="text" class="form-control" id="edit-model"/>
		    					</div>
    						</div>
    						<div class="col-md-6">
		    					<div class="form-group">
		    						<label path="swVer" for="edit-sw-ver" class="control-label">Software Version (*)</label>
		    						<form:input path="swVer" type="text" class="form-control" id="edit-sw-ver"/>
		    					</div>
    						</div>
    						<div class="col-md-6">
		    					<div class="form-group">
		    						<label path="hwVer" for="edit-hw-ver" class="control-label">Hardware Version (*)</label>
		    						<form:input path="hwVer" type="text" class="form-control" id="edit-hw-ver"/>
		    					</div>
    						</div>
    						<div class="col-md-12">
		    					<div class="form-group">
		    						<label path="description" for="edit-description" class="control-label">Description (*)</label>
		    						<form:input path="description" type="text" class="form-control" id="edit-description"/>
		    					</div>
    						</div>
    					</div>
    					<form:input type="hidden" id="edit-id" path="idGolden"/>
    					<form:input type="hidden" name="user" path="user" value="${pageContext.request.userPrincipal.name}"/>			
    				</form:form>
    				<div class="row">
						<div class="col-md-12">
							<p>(*) This is a mandatory field</p>
						</div>
					</div>
    			</div>
    			<div class="modal-footer">
    				<button id="editCancel" type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
    				<button id="editConfirm" type="submit" class="btn btn-custom"><span class="glyphicon glyphicon-ok"></span> Save Changes</button>
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
     				<button type="button" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-remove"></span> Cancel</button>
     				<button id="deleteConfirm" type="button" class="btn btn-custom" data-dismiss="modal"><span class="glyphicon glyphicon-ok"></span> Delete GU</button>
     			</div>
     		</div>
    	</div>
	</div>
</div>

<script>
	$(document).ready(function()
	{
		jQuery.getScript('resources/js/dynamic-gu.js', function()
		{
			goldenUnit.init();
		});
	});
</script>