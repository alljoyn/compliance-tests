<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
        <div class="container" id="container">
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
	        
	        <!-- Testcases table -->
	        <div class="row">
		       	<table class="table table-hover">
		       		<thead>
		       			<tr>
		       				<th class="hide">TC ID</th>
				        	<th>Test Case</th>
				        	<th>Description</th>
				        	<th>Select</th>
				        </tr>
				    </thead>
		        	<tbody id="tcBody">
						<c:forEach var="tc" items="${tcList}" varStatus="status">
							<tr>
				        		<td class="hide">${tc.idTC}</td>
				        		<td width="15%">${tc.name}</td>
								<td width="80%">${tc.description}</td>
								<td width="5%" style="text-align: center">
									<input class="is_checkbox" type="checkbox">
								</td>								
				        	</tr>
						</c:forEach>
					</tbody>        	
		       	</table>
			</div>
			
			<!-- Action buttons -->
			<div class="row" align="left">
	        	<button id="selectAll" type="button" class="btn btn-default btn-lg">Select All (*)</button>
	        	<button id="deselectAll" type="button" class="btn btn-default btn-lg">Deselect All</button>
	        </div>
	        
	        <div class="row" align="left">
	        	<p>(*) Mandatory for Certification purpose</p>
	        </div>
	        
	        <!-- Navigation buttons -->
	        <div class="row" align="left">
	        	<button id="prevButton" class="btn btn-custom btn-lg">« Previous Step</button>
	        	<a id="nextButton" type="button" class="btn btn-custom btn-lg pull-right" href="end">Next Step »</a>
	        </div>
        </div>
        
        <!-- Hidden form to previous view -->
        <div>
        	<form:form method="GET" id="prevForm" action="parameter" modelAttribute="newProject">
        		<form:input type="hidden" id="idProject" name="idProject" path="idProject" value=""/>
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
		
		<!-- Test Case selection depends on project type -->
		<script>	
			$(document).ready(function() {
				
				$("#pleaseWaitDialog").modal('show');
				
				$('#title').text("STEP 7: TEST PLAN GENERATION");
				if(sessionStorage.getItem("type")=="Conformance") {
					$('#title').text(function(i, oldText) {
						return oldText === 'STEP 7: TEST PLAN GENERATION' ? 'STEP 6: TEST PLAN GENERATION' : oldText;
					});
				}
				
				$('#selectedProject').append("Project: "+sessionStorage.getItem("projectName"));
				$('#selectedProject').append(" / DUT: "+sessionStorage.getItem("dutName"));
				if ((sessionStorage.getItem("type")!="Conformance") && (sessionStorage.getItem("guNames") != null)) {
					$('#selectedProject').append(" / GUs: "+sessionStorage.getItem("guNames"));
				}
				
				var data = {};
				for (var j = 0; j < sessionStorage.length; j++) {
					var key = sessionStorage.key(j);
					data[key] = sessionStorage.getItem(key);
				}
				
				$.ajax({
				   cache: false,
				   url: "testcase/load",
				   type: 'GET',
				   data: {
						data : data	
					},
				   success: function (data) {
					   	$('#tcBody').empty();
						$.each(data, function(i, tc) {
							$('#tcBody').append("<tr><td class=\"hide\">"+tc.idTC
									+"</td><td width=\"15%\">"+tc.name
									+"</td><td width=\"80%\">"+tc.description
									+"</td><td width=\"5%\" style=\"text-align: center\"><input class=\"is_checkbox\" type=\"checkbox\"/></td></tr>");
						});
						
						$('.table').dataTable({
							autoWidth: false,
							paging: false,
							searching: false,
							"sDom": '<"top">rt<"bottom"flp><"clear">',
							scrollY: ($(window).height()/2),
							columnDefs: [        
								{ orderable: false, targets: 3},
							],
							order: [0, 'asc']
						});
						
						$('.is_checkbox').prop('checked', true);
						$("#pleaseWaitDialog").modal('hide');
						
						if(sessionStorage.getItem("type")!="Development")
						{
							$.ajax({
								url: "testcase/disabled",
								type: 'GET',
								data: {
									idProject : sessionStorage.getItem("idProject")
								},
								success: function(data) {
									var MyRows = $('#tcBody').find('tr');

									$.each(data, function(i, disabled) {
										for (var j = 0; j < MyRows.length; j++) {
											if ($(MyRows[j]).find('td:eq(0)').html()==disabled)
											{
												$(MyRows[j]).find('.is_checkbox').prop('checked',false);
												$(MyRows[j]).find('.is_checkbox').prop('disabled',true);
												$(MyRows[j]).addClass('text-muted');
											}
										}
									});
								}
							});
						}
						else
						{
							var MyRows = $('#tcBody').find('tr');
							var noGUs = sessionStorage.getItem("guNames") == "";

							for (var j = 0; j < MyRows.length; j++) {
								
								if ((noGUs) && ($(MyRows[j]).find('td:eq(1)').html().indexOf("IOP") >= 0))
								{
									$(MyRows[j]).find('.is_checkbox').prop('checked', false);
									$(MyRows[j]).find('.is_checkbox').prop('disabled', true);
									$(MyRows[j]).addClass('text-muted');
								}
							}
						}
							
						$('tbody').find('tr').dblclick(function() {
							if(!($(this).hasClass("text-muted"))) {
								var val = $(this).find('.is_checkbox').is(':checked');
								$(this).find('.is_checkbox').prop('checked',!(val));
							}
						});
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
		
		<!-- Button scripts -->
		<script>
	  		$('#nextButton').on('click', function(){

				var MyRows = $('.table').find('tbody').find('tr');
				for (var i = 0; i < MyRows.length; i++) {
					var id = $(MyRows[i]).find('td:eq(1)').html();
					var value = $(MyRows[i]).find('.is_checkbox').is(':checked');
					if (value==true) {
						sessionStorage.setItem(id, value);
					}
				}
				
				applyTC = ($('.is_checkbox').length) - ($('.text-muted').length);
				sessionStorage.setItem("applyTC",applyTC);
				
			});

	  		$('#selectAll').on('click', function(){
				$('.is_checkbox').prop('checked', true);
				$('.text-muted').find('.is_checkbox').prop('checked', false);
				
			});

	  		$('#deselectAll').on('click', function(){
				$('.is_checkbox').prop('checked', false);
				
			});
	  		
	  		$('#prevButton').on('click', function(e){
				e.preventDefault();
				
				$('#idProject').val(sessionStorage.getItem('idProject'));
				$('#prevForm').submit();
			});  
		</script>
    </body>
</html>