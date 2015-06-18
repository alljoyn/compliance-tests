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
		    	<h4 id="selectedProject" class="pull-left"></h4>
		    	<c:if test="${pageContext.request.userPrincipal.name != null}">
					<h4>
						Welcome : ${pageContext.request.userPrincipal.name} | <a
						href="javascript:formSubmit()"> Logout</a>
					</h4>
				</c:if>
		    </div>
	        
	        <!-- IXIT Table -->
	        <div class="row">
	        	<!-- Service tabs -->
		        <ul class="nav nav-tabs">
		        	<c:forEach var="service" items="${serviceList}" varStatus="status">
		        		<c:choose>
							<c:when test="${service.idService==1}">
								<li class="active"><a href="#${service.idService}" data-toggle="tab">${service.name}</a></li>
							</c:when>
							<c:otherwise>
								<li><a href="#${service.idService}" data-toggle="tab">${service.name}</a></li>
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
						        	<th width="25%">Name</th>
						        	<th width="55%">Description</th>
						        	<th width="17%">Value</th>
						        </tr>
						    </thead>
				        	<tbody class="scroll-tbody">
								<c:forEach var="ixit" items="${ixitList}" varStatus="status">
									<c:if test="${ixit.serviceGroup==service.idService}">
							        	<tr class="scroll-tr">
							        		<td width="3%">${ixit.idIxit}</td>
							        		<td width="25%">${ixit.name}</td>
											<td width="55%">${ixit.description}</td>
											<td width="17%">
											<!-- <a href=# class="is_editable">${ixit.value}</a>  -->
											<input type="text" class="form-control" maxlength="255" value="${ixit.value}"/>
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
			
			<!-- Navigation buttons -->
			<div class="row" align="right">
				<a id="prevButton" type="button" class="btn btn-custom btn-lg pull-left">« Back</a>
        		<!-- <button id="nextButton" class="btn btn-custom btn-lg" data-toggle="modal" data-target="#pleaseWaitDialog">Next »</button> -->
        		<a id="nextButton" type="button" class="btn btn-custom btn-lg" href="testcase">Next »</a>
        	</div>
       	</div>
       	
    	<!-- Hidden form to previous view -->
        <div>
        	<form:form method="GET" id="prevForm" action="ics" modelAttribute="newProject">
        		<form:input type="hidden" id="idProjectPrev" name="idProject" path="idProject" value=""/>
        		<!-- <form:input type="hidden" id="isConfigured" name="isConfigured" path="isConfigured" value=""/> -->
        	</form:form>
        </div>
        
        <!-- Hidden form to next view -->
        <div>
        	<form:form method="GET" id="nextForm" action="parameter" modelAttribute="newProject">
        		<form:input type="hidden" id="idProjectNext" name="idProject" path="idProject" value=""/>
        	</form:form>
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
      
      	<script>
			$(document).ready(function() {
				
				$('#title').text("STEP 5: CONFIGURE YOUR IXIT");
				if(sessionStorage.getItem("type")=="Conformance") {
					$('#title').text(function(i, oldText) {
						return oldText === 'STEP 5: CONFIGURE YOUR IXIT' ? 'STEP 4: CONFIGURE YOUR IXIT' : oldText;
					});
				}
				
				$('#selectedProject').append("Project: "+sessionStorage.getItem("projectName"));
				$('#selectedProject').append(" / DUT: "+sessionStorage.getItem("dutName"));
				if(sessionStorage.getItem("type")!="Conformance") {
					$('#selectedProject').append(" / GUs: "+sessionStorage.getItem("guNames"));
				}
				
				$('#1').addClass('in active');
				
				var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);
				
				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(0)').html();
					
					if((id=="1")||(id=="12")||(id=="13")||(id=="14")||(id=="15")||(id=="16")
							||(id=="17")||(id=="24")||(id=="25")||(id=="26")||(id=="27")
							||(id=="28")||(id=="29")||(id=="30")||(id=="31")||(id=="32")
							||(id=="33")||(id=="35")||(id=="36")||(id=="37")||(id=="38")
							||(id=="39")||(id=="41")||(id=="42")||(id=="43")||(id=="44")
							||(id=="45")||(id=="46")||(id=="47")||(id=="48")||(id=="49")
							||(id=="50")||(id=="52")||(id=="53")||(id=="54")||(id=="55")
							||(id=="56")||(id=="57")||(id=="58")||(id=="59")||(id=="63")
							||(id=="64")||(id=="65")||(id=="66")||(id=="67")||(id=="68")
							||(id=="69")||(id=="71")||(id=="72")||(id=="73")||(id=="74")
							||(id=="75")||(id=="76")||(id=="77")) {
						$(MyRows[i]).find('.form-control').prop('type','number');
						$(MyRows[i]).find('.form-control').prop('min','1');
						$(MyRows[i]).find('.form-control').keypress(isNumberKey);
					}else if(id=="79") {
						$(MyRows[i]).find('.form-control').prop('type','date');
					}else if(id=="80") {
						$(MyRows[i]).find('.form-control').prop('type','url');
					}
					
					
					if((id=="2")||(id=="4")||(id=="5")||(id=="7")||(id=="8")||(id=="9")||(id=="11")) {
						$(MyRows[i]).find('.form-control').addClass('disabled');
						$(MyRows[i]).find('.form-control').prop('disabled',true);
						$(MyRows[i]).addClass('text-muted');
					} else {
						var name = $(MyRows[i]).find('td:eq(1)').html();

						if (sessionStorage.hasOwnProperty(name)) {
							$(MyRows[i]).find('.form-control').val(sessionStorage.getItem(name));
						}
					}
				}
			});
		</script>
		
		<script>
			function isNumberKey(evt){
			    var charCode = (evt.which) ? evt.which : event.keyCode
			    if (charCode > 31 && (charCode < 48 || charCode > 57))
			        return false;
			    return true;
			}    
		</script>
		
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				$('#logoutForm').submit();
			}
		</script>
		
		<!-- Button scripts -->
        <script>
	  		$('#nextButton').on('click', function(e){

	  			e.preventDefault();
	  			
				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					var value = $(MyRows[i]).find('.form-control').val();
					
					sessionStorage.setItem(id, value);
				}
				
				$('#idProjectNext').val(sessionStorage.getItem('idProject'));
				$('#nextForm').submit();		
			});
	  		
	  		$('#prevButton').on('click', function(e){
				e.preventDefault();
				
				$('#idProjectPrev').val(sessionStorage.getItem('idProject'));
				$('#prevForm').submit();
			});  
		</script>
    </body>
</html>