<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Header -->
<div class="row asa-header">
	<!-- Logo AllJoyn Certified -->	
	<div class="col-xs-12 col-sm-2" align="center">
		<a href="https://allseenalliance.org" title="AllSeen Alliance Home" target="_blank">
			<img class="asa-header-logo" src="resources/img/logo-alljoyn-certified.png" alt="AllSeen Alliance Home">
		</a>
	</div>
	<!-- Title -->
	<div class="col-xs-12 col-sm-8" align="center">
		<h2 id="title" class="asa-header-title"></h2>
	</div>
	
	<!-- CSRT for logout -->
   	<c:url value="/logout" var="logoutUrl"/>

	<form action="${logoutUrl}" method="post" id="logoutForm">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	</form>
	
	<!-- Dropdown for User actions -->
	<div class="col-xs-12 col-sm-2 asa-header-logout">
		<c:if test="${pageContext.request.userPrincipal.name != null}">
			<div class="row">
				<div class="col-sm-6 col-sm-offset-3">
					<div class="dropdown">
		  				<button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
		    				<span class="glyphicon glyphicon-user"></span> ${pageContext.request.userPrincipal.name}
		    				<span class="caret"></span>
		  				</button>
		  				<ul class="dropdown-menu" aria-labelledby="dropdownMenu1">
		   					<li><a href="javascript:common.logout()"><span class="glyphicon glyphicon-off"></span> Logout</a></li>
		  				</ul>
					</div>
				</div>
			</div>
		</c:if>
	</div>
</div>