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
		    
		    <!-- GUs table -->
	        <div class="row">
	        	<div>       
			       	<table id="table" class="table table-hover">
			       		<thead class="scroll-thead">
			       			<tr class="scroll-tr">
					        	<th width="10%">GU Name</th>
					        	<th width="8%">Created</th>
					        	<th width="8%">Modified</th>
					        	<th width="18%">Category</th>
					        	<th width="10%">OEM</th>
					        	<th width="10%">Model</th>
					        	<th width="8%">SW Ver</th>
					        	<th width="8%">HW Ver</th>
					        	<th width="20%">Description</th>
					        </tr>
					    </thead>
			        	<tbody class="scroll-tbody">
							<c:forEach var="gu" items="${guList}" varStatus="status">
					        	<tr class="scroll-tr">
					        		<td class="hide">${gu.idGolden}</td>
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
									<td width="20%">${gu.description}</td>							
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
	        </div>
	        
	        <div class="row" align="right">
	        	<p id="guSelect">You need to select at least 3 Golden Units</p>
	        </div>
	        
	        <!-- Navigation buttons -->
	        <div class="row" align="left">
	        	<a id="prevButton" type="button" class="btn btn-custom btn-lg" href="dut">« Back</a>
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
	        
	        <!-- Different DUT modal -->
	        <!-- <div id ="diffGu" class="modal fade" tabindex="-1" role="dialog" data-backdrop="static" data-keyboard="false">
	        	<div class="modal-dialog">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<h4>Project was configured with a different GU than selected. If you continue, project
	        				configuration will be deleted.</h4>
	        			</div>
	        			<div class="modal-footer">
	        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        				<button id="continueButton" class="btn btn-custom" data-dismiss="modal">Continue</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>  -->
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
		
		<script>
			$(document).ready(function() {
				if(sessionStorage.getItem("idProject")===null) {
					//document.getElementById("prevButton").click();
					document.getElementById("errorButton").click();
				}
				$('#title').text("STEP 3: SELECT/EDIT/CREATE A GOLDEN UNIT (GU)");
				
				var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);
				
				/*var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					if(($(MyRows[i]).find('td:eq(1)').html())==sessionStorage.getItem("associatedGu")) {
						$(MyRows[i]).addClass('selected');
						
						sessionStorage.setItem("idGu",($(MyRows[i]).find('td:eq(0)').html()));
						sessionStorage.setItem("associatedGu",($(MyRows[i]).find('td:eq(0)').html()));

					   	$('#nextButton').removeClass('disabled');
					   	$('#nextButton').prop("disabled", false);
						$('#deleteButton').removeClass('disabled');
						$('#deleteButton').prop("disabled", false);
						$('#editButton').removeClass('disabled');
						$('#editButton').prop("disabled", false);
					}
				}*/
				
				/*var idP = sessionStorage.getItem("idProject");
				var idD = sessionStorage.getItem("idDut");
				var idG = sessionStorage.getItem("idGu");
				var asD = sessionStorage.getItem("associatedDut");
				var asG = sessionStorage.getItem("associatedGu");
				sessionStorage.clear();
				sessionStorage.setItem("idProject", idP);
				sessionStorage.setItem("idDut", idD);
				sessionStorage.setItem("idGu", idG);
				sessionStorage.setItem("associatedDut", asD);
				sessionStorage.setItem("associatedGu", asG);*/

			});
		</script>
		
		<script>
			function formSubmit() {
				document.getElementById("logoutForm").submit();
			}
		</script>
		
		<!-- Button scripts -->
		<script>
		
			$('#nextButton').on('click', function(){
			
				/*if(sessionStorage.getItem("associatedGu")!="N/A") {
					if(sessionStorage.getItem("associatedGu")==sessionStorage.getItem("idGu")) {
						document.getElementById('idProject').value = sessionStorage.getItem("idProject");
						//document.getElementById('isConfigured').value = sessionStorage.getItem("isConfigured");
						document.getElementById('idGolden').value = sessionStorage.getItem("idGu");
						
						$('#nextForm').submit();
					} else {
						$('#diffGu').modal({
							show: true
						});
					}
				} else {*/
					document.getElementById('idProject').value = sessionStorage.getItem("idProject");
					//document.getElementById('idGolden').value = sessionStorage.getItem("idGu");
					
					var text="";
					$("#table tbody tr").each(function(index){
						if($(this).hasClass("selected")) {
							text+=$(this).find('td:first').html()+".";
						}
		    			
		    		});
					document.getElementById('gUnits').value = text;
					
					$('#nextForm').submit();
					
				//}
			});
			
			$('#continueButton').on('click', function() {
				document.getElementById('idProject').value = sessionStorage.getItem("idProject");
				document.getElementById('idGolden').value = sessionStorage.getItem("idGu");
				sessionStorage.setItem("associatedGu",sessionStorage.getItem("idGu"));
				$('#nextForm').submit();
			});
		
		  	$('#createGu').on('click', function(e){
		    	// We don't want this to act as a link so cancel the link action
		    	e.preventDefault();
		    	
		    	// Find form and submit it
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
							data : sessionStorage.getItem("idGu")
						},
						success: function() {
						    var MyRows = $('.table').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idGu")) {
									$(MyRows[i]).fadeOut(400, function() {
										$(MyRows[i]).remove();
									});
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
					type : 'GET',
					url : 'gu/edit',
					data : {
						data : sessionStorage.getItem("idGu")
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
			var nSel = 0;
			var id = [];
			$("#table tbody tr").click(function(){
			   	/*$(this).addClass('selected').siblings().removeClass('selected');    
			   	var id=$(this).find('td:first').html();
			   	sessionStorage.setItem("idGu",id);*/
			   	
			   	if($(this).hasClass("selected")) {
			   		$(this).removeClass("selected");
			   		for (var i=0; i<nSel; i++) {
			   			if ($(this).find('td:first').html()==id[i]) {
			   				//sessionStorage.removeItem("idGu"+i);
			   			}
			   		}
			   		nSel--;

			   	} else {
			   		$(this).addClass("selected");
				   	id[nSel]=$(this).find('td:first').html();
				   	//sessionStorage.setItem("idGu"+nSel,id[nSel]);
				   	nSel++;
			   	}
			   	/*$(this).addClass("selected");
			   	id=$(this).find('td:first').html();
			   	sessionStorage.setItem("idGu"+nSel,id);
			   	nSel++;*/

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

			   	/*$('#nextButton').removeClass('disabled');
			   	$('#nextButton').prop("disabled", false);
				$('#deleteButton').removeClass('disabled');
				$('#deleteButton').prop("disabled", false);
				$('#editButton').removeClass('disabled');
				$('#editButton').prop("disabled", false);*/
			});
		</script>
    </body>
</html>