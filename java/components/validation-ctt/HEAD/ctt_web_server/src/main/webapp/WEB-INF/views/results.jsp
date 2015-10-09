<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html>
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
				    	<c:if test="${pageContext.request.userPrincipal.name != null}">
							<h4>
								Welcome : ${pageContext.request.userPrincipal.name} | <a
								href="javascript:formSubmit()"> Logout</a>
							</h4>
						</c:if>
				    </div>
				    
				    <!-- Results table -->
			        <div class="row">      
					       	<table id="table" class="table table-hover hide dt-responsive">
					       		<!-- <thead class="scroll-thead">
					       			<tr class="scroll-tr">  -->
					       		<thead>
					       			<tr>
							        	<th width="20%">Name</th>
							        	<th width="50%">Description</th>
							        	<th width="10%">Date and Time Execution</th>
							        	<th width="10%">Certification Release</th>
							        	<th width="10%">Final Verdict</th>
							        	<th class="hide">Full Log</th>
							        </tr>
							    </thead>
							    <tbody>
					        	<!-- <tbody class="scroll-tbody">  -->
									<c:forEach var="result" items="${listTCResult}" varStatus="status">
										<tr>
							        	<!-- <tr class="scroll-tr">  -->
							        		<td width="20%">${result.name}</td>
							        		<td width="50%">${result.description}</td>
											<td width="10%">${result.execTimestamp}</td>
											<td width="10%">${result.version}</td>
											<c:if test="${result.verdict=='PASS'}">
												<td style="font-weight: bold; color: #99CC32"width="10%">${result.verdict}</td>
											</c:if>
											<c:if test="${result.verdict=='FAIL'}">
												<td style="font-weight: bold; color: #D43D1A"width="10%">${result.verdict}</td>
											</c:if>
											<c:if test="${result.verdict=='INCONC'}">
												<td style="font-weight: bold; color: #EEC900" width="10%">${result.verdict}</td>
											</c:if>
											<!-- <td width="10%"><a href="#" class="result-link">Link to log file</a></td>  -->
											<td class="hide">${result.log}</td>
										</tr>
									</c:forEach>
								</tbody>        	
					       	</table>
				     </div>
				     
				    <div class="row">
				    	<p id="notRanAll">You need to execute all applicable Test Cases to be able to create the Test Report</p>
				    </div>
				
					<!-- Action buttons -->
					<div class="row" align="left">
			        	<button class="btn btn-default btn-lg disabled" id="createTR">Create Test Report</button>
			        	<a class="btn btn-default btn-lg disabled" id="sendTR" href="results/tr/send">Send Test Report</a>
			        	<a class="btn btn-default btn-lg disabled" id="viewTR" href="results/tr/view" target="_blank">View Test Report</a>
			        	   
			        </div>
			        
			        <!-- Navigation buttons -->
			        <div class="row" align="left">
			        	<a id="prevButton" type="button" class="btn btn-custom btn-lg" href="project">« Back</a>
			        </div>
		        </div>
			</div>
		</div>
        
        <jsp:include page="/WEB-INF/views/footer.jsp"/>

		<script>
			$(document).ready(function() {
				if(!(localStorage.getItem("idProject"))) {
					document.getElementById("prevButton").click();
				}
				
				$('#title').text("TEST CASES RESULTS FOR \"${projectName}\"");
				
				/*var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);*/
				
				$('#table').removeClass('hide');
				
				$('#table').dataTable({
					autoWidth: false,
					pagingType: 'full_numbers',
					scrollY: ($(window).height()/2),
					order: [0, 'asc']
				});
				
				$.ajax({
					type: 'GET',
					url: 'results/tr/ranAll',
					data: {
						idProject : localStorage.getItem("idProject")
					},
					success: function(ranAll) {
						if(ranAll) {
							$('#createTR').removeClass('disabled');
							$('#createTR').prop("disabled", false);
							$('#notRanAll').prop("hidden",true);
						}
					}
				});
			});
		</script>
		
		<!-- Button scripts -->
		<script>
			$('#createTR').on('click', function(e){
				//alert("click");
				var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
				
				$.ajax({
					type : 'POST',
					url : 'results/tr/create',
					beforeSend: function(xhr) {
			            xhr.setRequestHeader(header, token);
			        },
					data : {
						idProject : localStorage.getItem("idProject")
					},
					success: function(created) {
						if(created) {
							$('#viewTR').removeClass('disabled');
							$('#viewTR').prop("disabled", false);
							$('#sendTR').removeClass('disabled');
							$('#sendTR').prop("disabled", false);
						}
					}
				});
			});
			
			$('#viewTR').on('click', function() {
				this.href= 'results/tr/view?idProject='+localStorage.getItem("idProject");
			});
			
			$('#sendTR').on('click', function() {
				this.href= 'results/tr/send?idProject='+localStorage.getItem("idProject");
			});
		</script>
		<!-- Full log access script -->
		<script>
			$.fn.multiline = function(text){
			    this.text(text);
			    this.html(this.html().replace(/\n/g,'<br/>'));
			    return this;
			};
			
			$('#table tbody tr').on('click', function() {
				$.ajax({
					cache: false,
					type : 'GET',
					url : 'results/fullLog',
					contentType: "text/plain; charset=UTF-8",
					data : {
						id : localStorage.getItem("idProject"),
						file : $(this).find('td:last').html()
					},
					success: function (data) {
						var w = window.open();

						$(w.document.head).html('<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />');
						$(w.document.body).multiline(data);		
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