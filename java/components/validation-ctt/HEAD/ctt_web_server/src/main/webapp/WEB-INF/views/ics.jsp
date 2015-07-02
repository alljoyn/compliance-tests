<%@page contentType="text/html" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<!DOCTYPE HTML>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<html lang="en">
    <head>
    	<jsp:include page="/WEB-INF/views/page_head.jsp"/>	
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
					       		<!-- <thead class="scroll-thead">
					       			<tr class="scroll-tr">  -->
					       		<thead>
					       			<tr>
							        	<th>Id</th>
							        	<th>Name</th>
							        	<th>Description</th>
							        	<th>Value</th>
							        </tr>
							    </thead>
					        	<!-- <tbody class="scroll-tbody">  -->
					        	<tbody>
									<c:forEach var="ics" items="${icsList}" varStatus="status">
										<c:if test="${ics.serviceGroup==service.idService}">
											<tr>
								        	<!-- <tr class="scroll-tr">  -->
								        		<td>${ics.id}</td>
								        		<td>${ics.name}</td>
												<td>${ics.description}</td>
												<td>
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
	       		<a id="prevButton" type="button" class="btn btn-custom btn-lg pull-left">« Previous Step</a>
	       		<button id="changeButton" type="button" class="btn btn-default btn-lg">Change All Service ICS</button>
	        	<button id="scrButton" type="button" class="btn btn-default btn-lg" data-toggle="modal" data-target="#pleaseWaitDialog">SCR</button>
	        	<button id="nextButton" disabled class="btn btn-custom btn-lg disabled">Next Step »</button>
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
				
				$('#selectedProject').append("Project: "+sessionStorage.getItem("projectName"));
				$('#selectedProject').append(" / DUT: "+sessionStorage.getItem("dutName"));
				if(sessionStorage.getItem("type")!="Conformance") {
					$('#selectedProject').append(" / GUs: "+sessionStorage.getItem("guNames"));
				}
				
				/*var w = $('.scroll-tbody').find('.scroll-tr').first().width();
				$('.scroll-thead').find('.scroll-tr').width(w);*/
				
				//$('.table').removeClass('hide');
				$('#1').addClass('in active');
				
				$("#1").find('table').dataTable({
					autoWidth: false,
					paging: false,
					searching: false,
					"sDom": '<"top">rt<"bottom"flp><"clear">',
					scrollY: ($(window).height()/2),
					columnDefs: [       
					    { width: "3%", targets: 0},
					    { width: "30%", targets: 1},
					    { width: "57%", targets: 2},
						{ width: "10%", orderable: false, targets: 3}
					],
					order: [0, 'asc']
				});
				
				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					
					if (sessionStorage.hasOwnProperty(id)) {
						$(MyRows[i]).find('.form-control').val(sessionStorage.getItem(id));
					}
				}
			});
			
			$('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
			   e.target; // activated tab
			   e.relatedTarget; // previous tab
			   var tab = $(e.target).attr('href');
			   if(!$(""+tab).find('table').hasClass('dataTable')) {
				   $(""+tab).find('table').dataTable({
					    autoWidth: false,
					   	paging: false,
						searching: false,
						"sDom": '<"top">rt<"bottom"flp><"clear">',
						scrollY: ($(window).height()/2),
						columnDefs: [        
							{ width: "3%", targets: 0},
						    { width: "30%", targets: 1},
						    { width: "57%", targets: 2},
							{ width: "10%", orderable: false, targets: 3}
						],
						order: [0, 'asc']
				   });
			   }
			});
		</script>
		
		<!-- Logout form script -->
		<script>
			function formSubmit() {
				$('#logoutForm').submit();
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
			$('tbody').find('tr').dblclick(function() {
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
				
				$('#prevIdProject').val(sessionStorage.getItem('idProject'));
				$('#prevForm').submit();
				
			});
	  		$('#nextButton').on('click', function(){
					
				$('#idProject').val(sessionStorage.getItem('idProject'));
				$('#idDut').val(sessionStorage.getItem('idDut'));
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
				
				var data = {};
				for (var j = 0; j < sessionStorage.length; j++) {
					var key = sessionStorage.key(j);
					data[key] = sessionStorage.getItem(key);
				}
  			
	  			$.ajax({
	  				   cache: false,
					   url: "ics/scr",
					   type: 'GET',
					   data: {
							data : data
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
	  		
	  		$('#changeButton').on('click', function() {
	  			$(".tab-pane.active").find('table tbody tr').dblclick();
	  			
	  			//This one changes all
	  			//$('.scroll-tbody').find('tr').dblclick();
	  		});
		</script>
    </body>
</html>
