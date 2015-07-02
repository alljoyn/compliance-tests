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
		    
		    <!-- GUs table -->
	        <div class="row">
	        	<div>       
			       	<table id="table" class="table table-hover">
			       		<!-- <thead class="scroll-thead">
			       			<tr class="scroll-tr">  -->
			       		<thead>
			       			<tr>
			       				<!-- <th class="hide">GU ID</th>	
					        	<th width="10%">GU Name</th>
					        	<th width="8%">Created</th>
					        	<th width="8%">Modified</th>
					        	<th width="18%">Category</th>
					        	<th width="10%">OEM</th>
					        	<th width="10%">Model</th>
					        	<th width="8%">SW Ver</th>
					        	<th width="8%">HW Ver</th>
					        	<th width="20%">Description</th> -->
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
					        	<!-- <tr class="scroll-tr">  -->
					        		<!-- <td class="hide">${gu.idGolden}</td>
					        		<td width="10%">${gu.name}</td>
									<td width="8%"><fmt:formatDate value="${gu.createdDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td width="8%"><fmt:formatDate value="${gu.modifiedDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<c:forEach var="category" items="${categoryList}" varStatus="status">
										<c:if test="${category.idCategory==gu.category}">
											<td width="18%">${category.name}</td>
										</c:if>
									</c:forEach>
									<td width="10%">${gu.manufacturer}</td>
									<td width="10%">${gu.model}</td>
									<td width="8%">${gu.swVer}</td>
									<td width="8%">${gu.hwVer}</td>
									<td width="20%">${gu.description}</td> -->
									
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
	        
	        <!-- <div class="row" align="right">
	        	<p id="guSelect">You need to select at least 3 Golden Units</p>
	        </div> -->
	        
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
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
		<script src="resources/jquery-validation/1.13.1/js/jquery.validate.min.js"></script>
		<script src="resources/jquery-validation/1.13.1/js/additional-methods.min.js"></script>
		
		<script>
			$(document).ready(function() {
				$('#title').text("STEP 3: SELECT/EDIT/CREATE A GOLDEN UNIT (GU)");
				
				$('#selectedProject').append("Project: "+sessionStorage.getItem("projectName"));
				$('#selectedProject').append(" / DUT: "+sessionStorage.getItem("dutName"));
				
				/*var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);*/
				
				$('#table').removeClass('hide');
				
				$('#table').dataTable({
					autoWidth: false,
					pagingType: 'full_numbers',
					scrollY: ($(window).height()/2),
					order: [0, 'asc']
				});
			});
		</script>
		
		<script>
			$('#newGuForm').validate({
				rules: {
					name: {
						required: true,
						minlength: 2,
						maxlength: 255,
						remote: {
							url: "gu/validateName",
							type: "get",
							data: {
								id: 0,
								name: function() {
									return $('#gu-name').val();
								}
							}
						}
					},
					manufacturer: {
						required: true,
						minlength: 2,
						maxlength: 60,
					},
					model: {
						required: true,
						minlength: 2,
						maxlength: 60,
					},
					hwVer: {
						required: true,
						minlength: 2,
						maxlength: 60,
					},
					swVer: {
						required: true,
						minlength: 2,
						maxlength: 60,
					},
					description: {
						required: true,
						minlength: 2,
						maxlength: 255,
					}
				},
				messages: {
					name: {
						required: "Please enter GU name!",
						maxlength: "GU name must have a max of 255 characters!",
						remote: "GU already exists!"
					},
					manufacturer: {
						required: "Please enter manufacturer!",
						maxlength: "Manufacturer must have a max of 60 characters!"
					},
					model: {
						required: "Please enter model!",
						maxlength: "Model must have a max of 60 characters!"
					},
					hwVer: {
						required: "Please enter hardware version!",
						maxlength: "Hardware version must have a max of 60 characters!"
					},
					swVer: {
						required: "Please enter software version!",
						maxlength: "Software version must have a max of 60 characters!"
					},
					description: {
						required: "Please enter description!",
						maxlength: "Description must have a max of 255 characters!"
					}
				}
			});
			
			$('#editGuForm').validate({
				rules: {
					name: {
						required: true,
						minlength: 2,
						maxlength: 255,
						remote: {
							url: "gu/validateName",
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
						minlength: 2,
						maxlength: 60,
					},
					model: {
						required: true,
						minlength: 2,
						maxlength: 60,
					},
					hwVer: {
						required: true,
						minlength: 2,
						maxlength: 60,
					},
					swVer: {
						required: true,
						minlength: 2,
						maxlength: 60,
					},
					description: {
						required: true,
						minlength: 2,
						maxlength: 255,
					}
				},
				messages: {
					name: {
						required: "Please enter GU name!",
						maxlength: "GU name must have a max of 255 characters!",
						remote: "GU already exists!"
					},
					manufacturer: {
						required: "Please enter manufacturer!",
						maxlength: "Manufacturer must have a max of 60 characters!"
					},
					model: {
						required: "Please enter model!",
						maxlength: "Model must have a max of 60 characters!"
					},
					hwVer: {
						required: "Please enter hardware version!",
						maxlength: "Hardware version must have a max of 60 characters!"
					},
					swVer: {
						required: "Please enter software version!",
						maxlength: "Software version must have a max of 60 characters!"
					},
					description: {
						required: "Please enter description!",
						maxlength: "Description must have a max of 255 characters!"
					}
				}
			});
		</script>
		
		<script>
			function formSubmit() {
				$('#logoutForm').submit();
			}
		</script>
		
		<!-- Button scripts -->
		<script>
		
			$('#nextButton').on('click', function(){
			
				$('#idProject').val(sessionStorage.getItem('idProject'));
				
				var text="";
				var textNames="";
				
				$("#table tbody tr").each(function(index){
					if($(this).hasClass("selected")) {
						text+=$(this).find('td:first').html()+".";
						textNames+=$(this).find('td:eq(1)').html()+", ";
					}
	    		});
				$('#gUnits').val(text);
				sessionStorage.setItem("guNames", textNames.substring(0, textNames.length -2));
				$('#nextForm').submit();
					
			});
			
			$('#continueButton').on('click', function() {
				$('#idProject').val(sessionStorage.getItem('idProject'));
				$('#idGolden').val(sessionStorage.getItem('idGu'));
				sessionStorage.setItem("associatedGu",sessionStorage.getItem("idGu"));
				$('#nextForm').submit();
			});
		
		  	$('#createGu').on('click', function(e){
		    	e.preventDefault();
		    	
		    	$('#newGuForm').submit();
		  	});

			$('#deleteConfirm').on('click', function(){
				
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
			    
			    $.ajax({
						type : 'POST',
						url : 'gu/delete',
						beforeSend: function(xhr) {
				            xhr.setRequestHeader(header, token);
				        },
						data : {
							idGu : sessionStorage.getItem("idGu")
						},
						success: function() {
						    var MyRows = $('#table').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idGu")) {
									$(MyRows[i]).click();
									var table = $('#table').DataTable();
									table.row($(MyRows[i])).remove().draw();
									/*$(MyRows[i]).removeClass('selected');
									$(MyRows[i]).fadeOut(400, function() {
										$(MyRows[i]).remove();
									});*/
								}
							}
							
							sessionStorage.removeItem("idGu");
							
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
				
				$('#editGuForm').submit();
			});
			
			$('#editButton').on('click', function(){
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'gu/edit',
					data : {
						idGu : sessionStorage.getItem("idGu")
					},
					success: function (data) {
						
						$('#edit-id').val(data.idGolden);
						$('#edit-name').val(data.name);
						$('#edit-category').val(data.category);
						$('#edit-manufacturer').val(data.manufacturer);
						$('#edit-model').val(data.model);
						$('#edit-sw-ver').val(data.swVer);
						$('#edit-hw-ver').val(data.hwVer);
						$('#edit-description').val(data.description);
				   }
				});
			});
			 	
		</script>
		
		<!-- Row selector script -->
		<script>
			var nSel=0;
			$("#table tbody tr").click(function(){			
			   	if($(this).hasClass("selected")) {
			   		$(this).removeClass("selected");
			   		nSel--;

			   	} else {
			   		$(this).addClass("selected");
				   	nSel++;
			   	}

			   	if(nSel>=3) {
			   		$('#nextButton').removeClass('disabled');
				   	$('#nextButton').prop("disabled", false);
				   	$('#guSelect').prop("hidden", true);
			   	} else if ((nSel==2)||(nSel==0)) {
					$('#deleteButton').addClass('disabled');
					$('#deleteButton').prop("disabled", true);
					$('#editButton').addClass('disabled');
					$('#editButton').prop("disabled", true);
					$('#nextButton').addClass('disabled');
				   	$('#nextButton').prop("disabled", true);
				   	$('#guSelect').prop("hidden", false);
				   	sessionStorage.removeItem("idGu");
			   	} else if (nSel==1) {
			   		$('#deleteButton').removeClass('disabled');
					$('#deleteButton').prop("disabled", false);
					$('#editButton').removeClass('disabled');
					$('#editButton').prop("disabled", false);
					id=$(this).find('td:first').html();
				   	sessionStorage.setItem("idGu",id);
			   	}
			});
		</script>
    </body>
</html>