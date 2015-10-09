<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
   		<div id="wrap">
		  	<div id="main" class="container clear-top">
		    	<!-- Main -->
		        <div class="container">
					<jsp:include page="/WEB-INF/views/header.jsp"/>
				    
					<!-- Login panel -->
					<div id="login-box" class="col-sm-6 col-md-4 col-md-offset-4">
						<div class="panel panel-default">
							<!-- Panel header -->
							<div class="panel-heading">
								<strong>Connect to the Linux Foundation to continue</strong>
							</div>
							<!-- Panel body -->
							<div class="panel-body">
							
								<div class="text-center">
									<img class="img-circle" src="resources/img/login.png"
											width="120" height="120"></img>
								</div>
												 
								<form name='loginForm' class="form-signin"
										action="<c:url value='j_spring_security_check' />" method='POST'>
										 
								  	<input type="hidden" name="${_csrf.parameterName}"
									value="${_csrf.token}" />
									
									<button class="btn btn-lg btn-custom btn-block" type="submit">Connect</button>	
								</form>
							</div>
						</div>
						<!-- Logout alert -->
						<c:if test="${not empty msg}">
							<div class="alert alert-info" align="center">
								<span class="glyphicon glyphicon-ok"></span>
								${msg}
								<div class="row">
								<a class="btn btn-info" href="j_spring_cas_security_logout">Logout of CAS</a>
								</div>
							</div>
						</c:if>
						
						<!-- Session expired alert -->
						<c:if test="${not empty session_expired}">
							<div class="alert alert-info" align="center">
								<span class="glyphicon glyphicon-ok"></span>
								${session_expired}
							</div>
						</c:if>
					</div>
				</div>
			</div>
		</div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
        
        <script src="resources/jquery/js/jquery-1.11.2.min.js"></script>
		<script src="resources/bootstrap/js/bootstrap.min.js"></script>
		
		<script>
			$(document).ready(function() {
				$('#title').text("MEMBERS LOGIN");
				setTimeout("location.reload(true);",300000);
			});
		</script>
    </body>
</html>