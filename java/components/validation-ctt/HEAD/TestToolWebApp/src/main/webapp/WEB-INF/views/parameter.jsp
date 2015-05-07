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
	        
	        <!-- Parameters Table -->
	        <div class="row">
		        
		        <!-- Service tables -->

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
						<c:forEach var="parameter" items="${listParameter}" varStatus="status">
					        	<tr class="scroll-tr">
					        		<td width="3%">${parameter.idParam}</td>
					        		<td width="30%">${parameter.name}</td>
									<td width="57%">${parameter.description}</td>
									<td width="10%">
									<input type="text" class="form-control" value="${parameter.value}"/>
									</td>								
					        	</tr>
						</c:forEach>
					</tbody>        	
		       	</table>
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
        	<form:form method="GET" id="prevForm" action="ixit" modelAttribute="newProject">
        		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
        	</form:form>
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
      
      	<script>
			$(document).ready(function() {
				
				$('#title').text("STEP 6: CONFIGURE YOUR GENERAL PARAMETERS");
				if(sessionStorage.getItem("type")=="Conformance") {
					$('#title').text(function(i, oldText) {
						return oldText === 'STEP 6: CONFIGURE YOUR GENERAL PARAMETERS' ? 'STEP 5: CONFIGURE YOUR GENERAL PARAMETERS' : oldText;
					});
				}
				
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
		
		<!-- Button scripts -->
        <script>
	  		$('#nextButton').on('click', function(){

				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					//var value = $(MyRows[i]).find('.is_editable').html();
					var value = $(MyRows[i]).find('.form-control').val();
					
					sessionStorage.setItem(id, value);
				}		
			});
	  		
	  		$('#prevButton').on('click', function(e){
				e.preventDefault();
				
				document.getElementById('idProject').value = sessionStorage.getItem("idProject");
				$('#prevForm').submit();
			});  
		</script>
    </body>
</html>