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
	        
	        <!-- ICS table -->
	        <div class="row">
	        	<!-- Service tabs -->
		        <ul class="nav nav-tabs">
		        	<c:forEach var="service" items="${serviceList}" varStatus="status">
		        		<c:choose>
							<c:when test="${service.idService==1}">
								<li class="active">
									<a href="#${service.idService}" data-toggle="tab">
										${service.name}  <span id="badge${service.idService}" 
											style="color:#000000; background-color:#F2DEDE;" class="badge"></span>
									</a>
								</li>
							</c:when>
							<c:otherwise>
								<li>
									<a href="#${service.idService}" data-toggle="tab">
										${service.name}  <span id="badge${service.idService}"
											style="color:#000000; background-color:#F2DEDE;" class="badge"></span>
									</a>
								</li>
							</c:otherwise>
						</c:choose>
					</c:forEach>
		        </ul>
		        
		        <!-- Service tables -->
		        <div class="tab-content">
			        <c:forEach var="service" items="${serviceList}" varStatus="status">
			        	<div class="tab-pane" id="${service.idService}">
					       	<table class="table table-hover">
					       		<thead class="scroll-thead">
					       			<tr class="scroll-tr">
							        	<th width="3%">Id</th>
							        	<th width="30%">Name</th>
							        	<th width="57%">Description</th>
							        	<th width="10%">Value</th>
							        </tr>
							    </thead>
					        	<tbody class="scroll-tbody">
									<c:forEach var="ics" items="${icsList}" varStatus="status">
										<c:if test="${ics.serviceGroup==service.idService}">
								        	<tr class="scroll-tr">
								        		<td width="3%">${ics.id}</td>
								        		<td width="30%">${ics.name}</td>
												<td width="57%">${ics.description}</td>
												<td width="10%">
													<select class="form-control">
														<option value="${ics.value}">${ics.value}</option>
														<option value="${ics.value==false}">${ics.value==false}</option>
													</select>
												</td>							
								        	</tr>
							        	</c:if>
									</c:forEach>
								</tbody>        	
					       	</table>
					    </div>
			        </c:forEach>
		        </div>
	        </div>
	        
	        <div class="row" align="right">
	        	<p id="scrNeed">You need to perform SCR verification</p>
	        	<p hidden="true" id="fixErrors">You need to correct invalid ICS values</p>
	        </div>
	        
	        <!-- Navigation and SCR buttons -->
	        <div class="row" align="right">
	       		<a id="prevButton" type="button" class="btn btn-custom btn-lg pull-left">« Back</a>
	        	<button id="scrButton" type="button" class="btn btn-default btn-lg" data-toggle="modal" data-target="#pleaseWaitDialog">SCR</button>
	        	<button id="nextButton" disabled class="btn btn-custom btn-lg disabled">Next »</button>
	        </div>
        </div>
        
        <!-- Hidden form to next view -->
        <div>
        	<form:form method="GET" id="nextForm" action="ixit" modelAttribute="newProject">
        		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
        		<!-- <form:input type="hidden" id="isConfigured" name="isConfigured" path="isConfigured" value=""/> -->
        		<form:input type="hidden" id="idDut" name="idDut" path="idDut" value=""/>
        	</form:form>
        </div>
        
        <!-- Hidden form to previous view -->
        <div>
        	<form:form method="GET" id="prevForm" action="ics/decide" modelAttribute="newProject">
        		<form:input type="hidden" id="prevIdProject" name="idProject" path="idProject" value=""/>
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
		
		<script>
			$(document).ready(function() {
				
				$('#title').text("STEP 4: CONFIGURE YOUR ICS");
				
				if(sessionStorage.getItem("type")=="Conformance") {
					$('#title').text(function(i, oldText) {
						return oldText === 'STEP 4: CONFIGURE YOUR ICS' ? 'STEP 3: CONFIGURE YOUR ICS' : oldText;
					});
					$('#prevButton').attr("href","dut");
				}
				
				if(sessionStorage.getItem("idDut")===null) {
					document.getElementById("prevButton").click();
				}
				$('#1').addClass('in active');
				
				var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);
				
				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					
					if (sessionStorage.hasOwnProperty(id)) {
						$(MyRows[i]).find('.form-control').val(sessionStorage.getItem(id));
					}
				}
			});
		</script>
		
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				document.getElementById("logoutForm").submit();
			}
		</script>
		
		<!-- Disable Next button when ICS change -->
		<script>
			$('.form-control').on('change', function() {
	    		$('#nextButton').addClass('disabled');
	    		$('#nextButton').prop("disabled", true);
	    		$('#scrNeed').prop("hidden", false);
	    		$('#fixErrors').prop("hidden",true);
	    	});
		</script>
		
		<script>
			$('.scroll-tbody').find('tr').dblclick(function() {
				var val = $(this).find('.form-control').val();
				$(this).find('.form-control').val((val=="false").toString());
				$('#nextButton').addClass('disabled');
	    		$('#nextButton').prop("disabled", true);
	    		$('#scrNeed').prop("hidden", false);
	    		$('#fixErrors').prop("hidden",true);
			});
		</script>
        
        <!-- Button scripts -->	
        <script>
	        $('#prevButton').on('click', function(){
				
				document.getElementById('prevIdProject').value = sessionStorage.getItem("idProject");
				$('#prevForm').submit();
				
			});
	  		$('#nextButton').on('click', function(){
					
				document.getElementById('idProject').value = sessionStorage.getItem("idProject");
				//document.getElementById('isConfigured').value = sessionStorage.getItem("isConfigured");
				document.getElementById('idDut').value = sessionStorage.getItem("idDut");
				$('#nextForm').submit();
				
			});
	  		
	  		$('#scrButton').on('click', function(){
	  					
	  			$('.badge').text("");
	  			
				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					var value = $(MyRows[i]).find('.form-control option:selected').html();

					sessionStorage.setItem(id, value);
				}
  			
	  			$.ajax({	
					   url: "ics/scr",
					   type: 'GET',
					   data: {
							data : sessionStorage	
						},
						dataType : 'json',
					   success: function (data) {

							var wrong=0;
							$.each(data, function(i, result) {
								if (!result.result) {
									for (var i = 0; i < MyRows.length; i++) {
										if($(MyRows[i]).find('td:eq(0)').html()==result.id) {
											$(MyRows[i]).addClass('danger');
										}
									}
									var label = "badge"+result.idService;
									if(($('#'+label).text())=="") {
										$('#'+label).text("1");
									} else {
										var e = parseInt($('#'+label).text());
										e+=1;
										$('#'+label).text(e);
									}
									wrong++;	
								} else {
									for (var i = 0; i < MyRows.length; i++) {
										if($(MyRows[i]).find('td:eq(0)').html()==result.id) {
											$(MyRows[i]).removeClass('danger');
										}
									}
								}
								$("#pleaseWaitDialog").modal('hide');
							});
							if (wrong==0) {
								$('#nextButton').removeClass('disabled');
								$('#nextButton').prop("disabled", false);
								$('#scrNeed').prop("hidden", true);
							} else {
								$('#scrNeed').prop("hidden", true);
								$('#fixErrors').prop("hidden",false);
							}
					   }
				});
	  		});
		</script>
    </body>
</html>
