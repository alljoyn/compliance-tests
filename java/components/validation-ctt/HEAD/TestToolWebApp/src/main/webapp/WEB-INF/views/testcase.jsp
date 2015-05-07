<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html lang="en">
    <head>
    	<title>AllSeen</title>
    	<meta charset="utf-8">
    	
    	<!-- Add the next line to ensure proper rendering and touch zooming -->
    	<meta name="viewport" content="width=device-width, initial-scale=1">
        
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
        <div class="container" id="container">
        	<jsp:include page="/WEB-INF/views/header.jsp"/>
		    
		    <div class="row" align="right">
		    	<c:if test="${pageContext.request.userPrincipal.name != null}">
					<h4>
						Welcome : ${pageContext.request.userPrincipal.name} | <a
						href="javascript:formSubmit()"> Logout</a>
					</h4>
				</c:if>
		    </div>
	        
	        <!-- Testcases table -->
	        <div class="row">
		       	<table class="table table-hover">
		       		<thead class="scroll-thead">
		       			<tr class="scroll-tr">
				        	<th width="15%">Test Case</th>
				        	<th width="79%">Description</th>
				        	<th width="6%">Select</th>
				        </tr>
				    </thead>
		        	<tbody id="tcBody" class="scroll-tbody">
						<c:forEach var="tc" items="${tcList}" varStatus="status">
				        	<tr class="scroll-tr">
				        		<td class="hide">${tc.idTC}</td>
				        		<td width="15%">${tc.name}</td>
								<td width="79%">${tc.description}</td>
								<td width="6%" style="text-align: center">
									<input class="is_checkbox" type="checkbox">
								</td>								
				        	</tr>
						</c:forEach>
					</tbody>        	
		       	</table>
			</div>
			
			<!-- Action buttons -->
			<div class="row" align="left">
	        	<button id="selectAll" type="button" class="btn btn-default btn-lg">Select All</button>
	        	<button id="deselectAll" type="button" class="btn btn-default btn-lg">Deselect All</button>
	        </div>
	        
	        <!-- Navigation buttons -->
	        <div class="row" align="left">
	        	<button id="prevButton" class="btn btn-custom btn-lg">« Back</button>
	        	<a id="nextButton" type="button" class="btn btn-custom btn-lg pull-right" href="end">Next »</a>
	        </div>
        </div>
        
        <!-- Hidden form to previous view -->
        <div>
        	<form:form method="GET" id="prevForm" action="parameter" modelAttribute="newProject">
        		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
        		<!-- <form:input type="hidden" id="isConfigured" name="isConfigured" path="isConfigured" value=""/> -->
        	</form:form>
        </div>
        
        <!-- Processing... modal -->	
       	<div class="modal" tabindex="-1" role="dialog" aria-hidden="true" id="pleaseWaitDialog" data-backdrop="static" data-keyboard="false">
			<div class="modal-dialog">
		        <div class="modal-content">
		        	<div class="modal-header">
		        		<h1>Processing...</h1>
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

        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
		
		<!-- Testcase selection depends on project type -->
		<script>
			$(document).ready(function() {
				
				$("#pleaseWaitDialog").modal('show');
				
				$('#title').text("STEP 7: TEST PLAN GENERATION");
				if(sessionStorage.getItem("type")=="Conformance") {
					$('#title').text(function(i, oldText) {
						return oldText === 'STEP 7: TEST PLAN GENERATION' ? 'STEP 6: TEST PLAN GENERATION' : oldText;
					});
				}
				
				$.ajax({
				   url: "testcase/load",
				   type: 'GET',
				   data: {
						data : sessionStorage	
					},
				   success: function (data) {
					   	$('#tcBody').empty();
						$.each(data, function(i, tc) {
							$('#tcBody').append("<tr class=\"scroll-tr\"><td class=\"hide\">"+tc.idTC
									+"</td><td width=\"15%\">"+tc.name
									+"</td><td width=\"80%\">"+tc.description
									+"</td><td width=\"5%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\"/></td></tr>");
						});
						
						$('.is_checkbox').prop('checked', true);
						$("#pleaseWaitDialog").modal('hide');
						
						$.ajax({
							url: "testcase/disabled",
							type: 'GET',
							data: {
								idProject : sessionStorage.getItem("idProject")
							},
							success: function(data) {
								var MyRows = $('#tcBody').find('tr');
								$.each(data, function(i, disabled) {
									for (var j = 0; j < MyRows.length; j++) {
										//if(j==0) alert($(MyRows[j]).find('td:eq(0)').html());
										if($(MyRows[j]).find('td:eq(0)').html()==disabled) {
											//alert("entra");
											$(MyRows[j]).find('.is_checkbox').prop('checked',false);
											$(MyRows[j]).find('.is_checkbox').prop('disabled',true);
											$(MyRows[j]).addClass('text-muted');
										}
									}
								});
							}
						});
						
						var w = $('.scroll-tbody').find('.scroll-tr').first().width();
						$('.scroll-thead').find('.scroll-tr').width(w);
						
						$('.scroll-tbody').find('tr').dblclick(function() {
							if(!($(this).hasClass("text-muted"))) {
								var val = $(this).find('.is_checkbox').is(':checked');
								$(this).find('.is_checkbox').prop('checked',!(val));
							}
						});
				   }
				});
			});
		</script>
		
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				document.getElementById("logoutForm").submit();
			}
		</script>
		
		<!-- Button scripts -->
		<script>
	  		$('#nextButton').on('click', function(){

				var MyRows = $('.table').find('tbody').find('tr');
				//for (var i = 0; i < MyRows.length; i++) {
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					var value = $(MyRows[i]).find('.is_checkbox').is(':checked');
					if (value==true) {
						//alert(id);
						sessionStorage.setItem(id, value);
					}
				}
				
			});

	  		$('#selectAll').on('click', function(){
				$('.is_checkbox').prop('checked', true);
				$('.text-muted').find('.is_checkbox').prop('checked', false);
				
			});

	  		$('#deselectAll').on('click', function(){
				$('.is_checkbox').prop('checked', false);
				
			});
	  		
	  		$('#prevButton').on('click', function(e){
				e.preventDefault();
				
				document.getElementById('idProject').value = sessionStorage.getItem("idProject");
				//document.getElementById('isConfigured').value = sessionStorage.getItem("isConfigured");
				$('#prevForm').submit();
			});  
		</script>
    </body>
</html>