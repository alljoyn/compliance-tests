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
    <body style="overflow-y: scroll;">
    
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
		    
		    <!-- Tccls table -->
	        <div class="row">
	        	<div>       
			       	<table id="table" class="table table-hover">
			       		<thead>
			       			<tr>
					        	<th>TCCL Name</th>
					        	<th>Created</th>
					        	<th>Modified</th>
					        	<th>Certification Release</th>
					        </tr>
					    </thead>
			        	<tbody>
							<c:forEach var="tccl" items="${tcclList}" varStatus="status">
					        	<tr>
					        		<td class="hide">${tccl.idTccl}</td>
					        		<td>${tccl.name}</td>
									<td><fmt:formatDate value="${tccl.createdDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<td><fmt:formatDate value="${tccl.modifiedDate}"
										pattern="yyyy-MM-dd HH:mm:ss"/></td>
									<c:forEach var="certrel" items="${certrelList}" varStatus="status">
										<c:if test="${certrel.idCertrel==tccl.idCertrel}">
											<td>${certrel.name}</td>
										</c:if>
									</c:forEach>
								</tr>
							</c:forEach>
						</tbody>        	
			       	</table>
			    </div>
		     </div>
		
			<!-- Action buttons -->
			<div class="row" align="left">
	        	<button class="btn btn-default btn-lg" data-toggle="modal" data-target="#newTcclModal">New TCCL</button>
	        	<button id="viewButton" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#viewTcclModal">View TCCL</button>
	        	<button id="editButton" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#editTcclModal">Edit TCCL</button>
	        	<button id="deleteButton" disabled class="btn btn-default btn-lg disabled" data-toggle="modal" data-target="#delTcclModal">Delete TCCL</button>
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
	        					<thead class="scroll-thead">
	        						<tr class="scroll-tr">
	        							<th width="20%">Test Case</th>
	        							<th width="60%">Test Case Description</th>
	        							<th width="10%">Type</th>
	        							<th width="10%">Enable</th>
	        					</thead>
	        					<tbody id="tcBody" class="scroll-tbody">
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
	        					<thead class="scroll-thead">
	        						<tr class="scroll-tr">
	        							<th width="20%">Test Case</th>
	        							<th width="60%">Test Case Description</th>
	        							<th width="10%">Type</th>
	        							<th width="10%">Enable</th>
	        					</thead>
	        					<tbody id="viewTcBody" class="scroll-tbody">
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
	        					<thead class="scroll-thead">
	        						<tr class="scroll-tr">
	        							<th width="20%">Test Case</th>
	        							<th width="60%">Test Case Description</th>
	        							<th width="10%">Type</th>
	        							<th width="10%">Enable</th>
	        					</thead>
	        					<tbody id="editTcBody" class="scroll-tbody">
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
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
	
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				$('#logoutForm').submit();
			}
		</script>
		<script>
			$(document).ready(function() {
				$('#title').text("VIEW/EDIT/CREATE A TEST CASE CONTROL LIST (TCCL)");
				
				var w = $('#table').find('.scroll-tbody').find('.scroll-tr').first().width();
				$('#table').find('.scroll-thead').find('.scroll-tr').width(w);
			});
		</script>
		<script>	
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
							$('#tcBody').append("<tr class=\"scroll-tr\"><td class=\"hide\">"+tc[0]+"</td><td width=\"20%\">"
									+tc[1]+"</td><td width=\"60%\">"+tc[8]+selector
									+"</td><td width=\"10%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\">");
						});
						
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
						
					    var row = "<tr><td class=\"hide\">"+tccl.idTccl+"</td>"
		        			+"<td>"+tccl.name+"</td><td>"+cdFormat+"</td>"
							+"<td>"+mdFormat+"</td>"
							+"<td>"+tccl.nameCertrel+"</td></tr>";
						
					    $('#table').find('tbody').append(row);
					    
					    $("#table tbody tr").click(function(){
						   	$(this).addClass('selected').siblings().removeClass('selected');    
						   	var id=$(this).find('td:first').html();
						   	
						   	sessionStorage.setItem("idTccl",id);

							$('#viewButton').removeClass('disabled');
							$('#viewButton').prop("disabled", false);
							$('#deleteButton').removeClass('disabled');
							$('#deleteButton').prop("disabled", false);
							$('#editButton').removeClass('disabled');
							$('#editButton').prop("disabled", false);
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
							var MyRows = $('.table').find('tbody').find('tr');
							for (var i = 0; i < MyRows.length; i++) {
								if(($(MyRows[i]).find('td:eq(0)').html())==sessionStorage.getItem("idTccl")) {
									$(MyRows[i]).fadeOut(400, function() {
										$(MyRows[i]).remove();
									});
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
							if($(MyRows[i]).find('td:eq(0)').html()==tccl.idTccl) {
								$(MyRows[i]).find('td:eq(3)').html(mdFormat);
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
						
						$('#viewTcBody').find('.is_checkbox').prop('disabled', true);
				   }
				});
			});
		</script>
		
		<!-- Row selector script -->
		<script>
			$("#table tbody tr").click(function(){
			   	$(this).addClass('selected').siblings().removeClass('selected');    
			   	var id=$(this).find('td:first').html();
			   	
			   	sessionStorage.setItem("idTccl",id);

			   	$('#viewButton').removeClass('disabled');
				$('#viewButton').prop("disabled", false);
				$('#deleteButton').removeClass('disabled');
				$('#deleteButton').prop("disabled", false);
				$('#editButton').removeClass('disabled');
				$('#editButton').prop("disabled", false);
			});
		</script>
    </body>
</html>