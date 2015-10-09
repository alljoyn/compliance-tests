<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html lang="en">
    <head>
    	<jsp:include page="/WEB-INF/views/page_head.jsp"/>
    </head>
    <body>
    	<div id="wrap">
		  	<div id="main" class="container clear-top">
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
			        
			        <!-- Parameters Table -->
			        <div class="row">
				        
				        <!-- Service tables -->
		
						<table class="table table-hover">
				       		<!-- <thead class="scroll-thead">
				       			<tr class="scroll-tr">  -->
				       		<thead>
				       			<tr>
						        	<th width="3%">Id</th>
						        	<th width="30%">Name</th>
						        	<th width="57%">Description</th>
						        	<th width="10%">Value</th>
						        </tr>
						    </thead>
				        	<!-- <tbody class="scroll-tbody">  -->
				        	<tbody>
								<c:forEach var="parameter" items="${listParameter}" varStatus="status">
									<tr>
							        	<!-- <tr class="scroll-tr">  -->
							        		<td width="3%">${parameter.idParam}</td>
							        		<td width="30%">${parameter.name}</td>
											<td width="57%">${parameter.description}</td>
											<td width="10%">
											<input style="width: 100%" type="number" min="1" class="form-control" value="${parameter.value}"/>
											</td>								
							        	</tr>
								</c:forEach>
							</tbody>        	
				       	</table>
				    </div>
		
					
					<!-- Navigation buttons -->
					<div class="row" align="right">
						<a id="prevButton" type="button" class="btn btn-custom btn-lg pull-left">« Previous Step</a>
		        		<!-- <button id="nextButton" class="btn btn-custom btn-lg" data-toggle="modal" data-target="#pleaseWaitDialog">Next »</button> -->
		        		<a id="nextButton" type="button" class="btn btn-custom btn-lg" href="testcase">Next Step »</a>
		        	</div>
		       	</div>
		       	
		    	<!-- Hidden form to previous view -->
		        <div>
		        	<form:form method="GET" id="prevForm" action="ixit" modelAttribute="newProject">
		        		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
		        	</form:form>
		        </div>
	        </div>
        </div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>
      
      	<script>
			$(document).ready(function() {
				
				$('#title').text("STEP 6: CONFIGURE YOUR GENERAL PARAMETERS");
				if(sessionStorage.getItem("type")=="Conformance") {
					$('#title').text(function(i, oldText) {
						return oldText === 'STEP 6: CONFIGURE YOUR GENERAL PARAMETERS' ? 'STEP 5: CONFIGURE YOUR GENERAL PARAMETERS' : oldText;
					});
				}
				
				$('#selectedProject').append("Project: "+sessionStorage.getItem("projectName"));
				$('#selectedProject').append(" / DUT: "+sessionStorage.getItem("dutName"));
				if(sessionStorage.getItem("type")!="Conformance") {
					$('#selectedProject').append(" / GUs: "+sessionStorage.getItem("guNames"));
				}
				
				/*var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);*/
				
				$('.table').dataTable({
					//autoWidth: false,
					paging: false,
					searching: false,
					"sDom": '<"top">rt<"bottom"flp><"clear">',
					scrollY: ($(window).height()/2),
					columnDefs: [        
						{ orderable: false, targets: 3},
					],
					order: [0, 'asc']
				});
				
				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					
					$(MyRows[i]).find('.form-control').keypress(isNumberKey);
					
					if (sessionStorage.hasOwnProperty(id)) {
						$(MyRows[i]).find('.form-control').val(sessionStorage.getItem(id));
					}
				}
			});
		</script>
		
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				$('#logoutForm').submit();
			}
		</script>
		
		<script>
			function isNumberKey(evt){
			    var charCode = (evt.which) ? evt.which : event.keyCode
			    if (charCode > 31 && (charCode < 48 || charCode > 57))
			        return false;
			    return true;
			}    
		</script>
		
		<!-- Button scripts -->
        <script>
	  		$('#nextButton').on('click', function(){

				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					var value = $(MyRows[i]).find('.form-control').val();
					
					sessionStorage.setItem(id, value);
				}
			});
	  		
	  		$('#prevButton').on('click', function(e){
				e.preventDefault();
				
				$('#idProject').val(sessionStorage.getItem('idProject'));
				$('#prevForm').submit();
			});  
		</script>
    </body>
</html>