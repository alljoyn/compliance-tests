<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="es">
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
		    	<h4 id="selectedProject" class="pull-left"></h4>
		    	<c:if test="${pageContext.request.userPrincipal.name != null}">
					<h4>
						Welcome : ${pageContext.request.userPrincipal.name} | <a
						href="javascript:formSubmit()"> Logout</a>
					</h4>
				</c:if>
		    </div>
	        
	        <!-- End box -->
	        <div class="row">
		    	<div class="jumbotron">
		    		<h2>Your project has been successfully configured!</h2>   		
		    		<p>1) Open the Test Tool Local Agent</p>
		    		<p>2) Select your configured Project</p>
		    		<p>3) Run the Test Cases</p>
		    		<p>4) Summit the results</p>
		    		<p><a type="button" class="btn btn-default btn-lg" href="end/download">Download Test Tool Local Agent</a></p> 
		    		<p><a type="button" class="btn btn-custom btn-lg" href="project">End</a></p>
		    	</div>
	    	</div>
	    </div>
	    
	    <jsp:include page="/WEB-INF/views/footer.jsp"/>
	      
        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
		
    	<script>
			$(document).ready(function() {
				
				$('#title').text("STEP 8: END CONFIGURATION");
				if(sessionStorage.getItem("type")=="Conformance") {
					$('#title').text(function(i, oldText) {
						return oldText === 'STEP 8: END CONFIGURATION' ? 'STEP 7: END CONFIGURATION' : oldText;
					});
				}
				
				$('#selectedProject').append("Project: "+sessionStorage.getItem("projectName"));
				$('#selectedProject').append(" / DUT: "+sessionStorage.getItem("dutName"));
				if(sessionStorage.getItem("type")!="Conformance") {
					$('#selectedProject').append(" / GUs: "+sessionStorage.getItem("guNames"));
				}
				
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
				
				var data = {};
				for (var j = 0; j < sessionStorage.length; j++) {
					var key = sessionStorage.key(j);
					data[key] = sessionStorage.getItem(key);
				}
				
				$.ajax({
					   url: 'end/save',
					   type: 'POST',
					   beforeSend: function(xhr) {
				            xhr.setRequestHeader(header, token);
				        },
					   data: {
						   	data : data	
						   }
					});				
			});
		</script>
		
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				$('#logoutForm').submit();
			}
		</script>
    </body>
</html>