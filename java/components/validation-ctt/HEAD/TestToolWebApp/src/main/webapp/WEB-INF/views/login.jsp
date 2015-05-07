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
    <body onload='document.loginForm.username.focus();'>
    
    	<!-- Main -->
        <div class="container">
			<jsp:include page="/WEB-INF/views/header.jsp"/>
		    
			<!-- Login panel -->
			<div id="login-box" class="col-sm-6 col-md-4 col-md-offset-4">
				<div class="panel panel-default">
					<!-- Panel header -->
					<div class="panel-heading">
						<strong>Sign in to continue</strong>
					</div>
					<!-- Panel body -->
					<div class="panel-body">
					
						<div class="text-center">
							<img class="img-circle" src="resources/img/login.png"
									width="120" height="120"></img>
						</div>
										 
						<form name='loginForm' class="form-signin"
								action="<c:url value='j_spring_security_check' />" method='POST'>
						
							<label for="inputUser" class="sr-only">Username</label>
				        	<input name="username" type="text" id="inputUser" class="form-control" placeholder="Username" required autofocus>
				        	<label for="inputPassword" class="sr-only">Password</label>
				        	<input name="password" type="password" id="inputPassword" class="form-control" placeholder="Password" required>
				        	<div class="checkbox" align="left">
				          		<label>
				            		<input type="checkbox" value="remember-me">Remember me
				          		</label>
				        	</div>		 
						  	<input type="hidden" name="${_csrf.parameterName}"
							value="${_csrf.token}" />
							
							<button class="btn btn-lg btn-custom btn-block" type="submit">Sign in</button>	
				 
						</form>
						
						<!-- Action buttons -->
						<div align="left">
			        		<button id="newAccountButton" type="button" class="btn btn-link pull-left" data-toggle="modal" data-target="#newAccountForm">Need an account?</button>
			        		<button id="forgotPassButton" type="button" class="btn btn-link pull-right" data-toggle="modal" data-target="#forgotPassForm">Forgot password?</button>
			        	</div>
					</div>
				</div>
				<!-- Bad login alert -->
				<c:if test="${not empty error}">
					<div class="alert alert-danger" role="alert" align="center">
						<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true"></span>
						<span class="sr-only">Error:</span>
						${error}
					</div>
				</c:if>
				<!-- Logout alert -->
				<c:if test="${not empty msg}">
					<div class="alert alert-info" role="alert" align="center">
						<span class="glyphicon glyphicon-ok" aria-hidden="true"></span>
						${msg}
					</div>
				</c:if>
			</div>
		</div>
		
		<!-- Modal forms -->
		<div>
			<!-- New account form -->
			<div id ="newAccountForm" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	        	<div class="modal-dialog">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<form:form method="post" id="registration-form" action="login/addUser" modelAttribute="newUser">
		       					<div class="form-group">
		       						<label path="user" for="formUser" class="control-label">Username</label>
		       						<form:input path="user" type="text" class="form-control" id="user"/>
		       					</div>
		       					<div class="form-group">
		       						<label path="password" for="formPass" class="control-label">Password</label>
		       						<!-- <div class="input-group">
		       							<span class="input-group-addon glyphicon glyphicon-lock"></span>  -->
		       							<form:input type="password" path="password" class="form-control" id="formPass"/>  
		       						<!-- </div>  -->
		       					</div>
		       					<div class="form-group">
		       						<label path="repPassword" for="formRepPass" class="control-label">Repeat password</label>
		       						<form:input type="password" path="repPassword" class="form-control" id="formRepPass"/>  
		       					</div>
		       					<div class="form-group">
	        						<label path="role" for="role" class="control-label">Role</label>
	        						<form:select path="role" class="form-control" id="role">
	        							<option value="ROLE_USER">User</option>
	        							<option value="ROLE_ADMIN">Administrator</option>
	        						</form:select>
	        					</div>
		       				</form:form>
	        			</div>
	        			<div class="modal-footer">
	        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        				<button id="createUser" type="submit" class="btn btn-custom">Create account</button>
	        			</div>
	        		</div>
	        	</div>
	        </div>
	        
	        <!-- Forgot password form -->
	        <div id ="forgotPassForm" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	        	<div class="modal-dialog">
	        		<div class="modal-content">
	        			<div class="modal-body">
	        				<form:form method="post" id="forgot-password-form" action="requestPassword" modelAttribute="newUser">	
		       				</form:form>
	        			</div>
	        			<div class="modal-footer">
	        				<button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
	        				<button id="recoverPass" type="submit" class="btn btn-custom disabled" disabled>Request new password</button>
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
				$('#title').text("MEMBERS LOGIN");
				setTimeout("location.reload(true);",300000);
			});
		</script>
		
		<script>
			$('#createUser').on('click', function() {
				$('#registration-form').submit();
			});
		</script>
    </body>
</html>