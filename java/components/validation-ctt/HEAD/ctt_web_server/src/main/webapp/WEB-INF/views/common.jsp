<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
	<jsp:include page="/WEB-INF/views/page_head.jsp"/>
</head>
<body>
	<div id="wrap">
		<div id="main" class="container-fluid">
			<jsp:include page="/WEB-INF/views/header.jsp"/>
			<div class="row">
				<div class="col-md-2 asa-left-column">
					<div class="row asa-left-row">
						<div class="col-sm-6 col-md-12 asa-left-button">
							<button class="btn btn-block btn-default hidden-md api-key"><span class="glyphicon glyphicon-lock"></span> CTT Local Agent Password</button>
							<button class="btn btn-block btn-default visible-md-block api-key"><span class="glyphicon glyphicon-lock"></span> CTT-LA Pass</button>
						</div>
						<div class="col-sm-6 col-md-12 asa-left-button">
							<a type="button" class="btn btn-block btn-default hidden-md" href="end/download"><span class="glyphicon glyphicon-download-alt"></span> Download CTT Local Agent</a>	
							<a type="button" class="btn btn-block btn-default visible-md-block" href="end/download"><span class="glyphicon glyphicon-download-alt"></span> CTT-LA</a>	
						</div>
					</div>
					<div class="row">
						<ul class="nav nav-pills nav-stacked asa-left-nav">
							<li id="project-nav" class="disabled" role="presentation"><a disabled href="project">PROJECT</a></li>
							<li id="dut-nav" class="disabled" role="presentation"><a disabled href="dut">DUT</a></li>
							<li id="gu-nav" class="disabled" role="presentation"><a disabled href="gu">GOLDEN UNITS</a></li>
							<li id="ics-nav" class="disabled" role="presentation"><a disabled href="ics">ICS</a></li>
							<li id="ixit-nav" class="disabled" role="presentation"><a disabled href="ixit">IXIT</a></li>
							<li id="gp-nav" class="disabled" role="presentation"><a disabled href="parameter">GENERAL PARAMETERS</a></li>
							<li id="tc-nav" class="disabled" role="presentation"><a disabled href="testcase">TEST CASES</a></li>
						</ul>
					</div>
				</div>
				<div class="col-md-10">
					<ol class="breadcrumb asa-right-breadcrumb">
					  <li id="project-breadcrumb" class="hidden">Project</li>
					  <li id="dut-breadcrumb" class="hidden">DUT</li>
					  <li id="gu-breadcrumb" class="hidden">GU</li>
					</ol>
					<div id="dynamic" class="row asa-right-dynamic-row">
					</div>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12 col-md-10 col-md-offset-2">
					<button id="prevButton" disabled class="btn btn-custom disabled"><span class="glyphicon glyphicon-chevron-left"></span> Previous Step</button>
					<button id="endButton" disabled class="btn btn-custom disabled"><span class="glyphicon glyphicon-floppy-save"></span> Save Project</button>
					<button id="nextButton" disabled class="btn btn-custom disabled">Next Step <span class="glyphicon glyphicon-chevron-right"></span></button>
				</div>
			</div>
		</div>
		
		<!-- Generated password modal -->
		<div id="generatedKey" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-body">
		 				<h4>Your CTT Local Agent Password is:</h4>
		 				<h4 id="key"></h4>
		 				<h4>Please copy it because it cannot be retrieved</h4>
		 			</div>
		 			<div class="modal-footer">
		 				<button id="closeApiKey" type="button" class="btn btn-default" data-dismiss="modal">Close</button>
		 			</div>
		 		</div>
		 	</div>
		</div>
	</div>
	
	<jsp:include page="/WEB-INF/views/footer.jsp"/>
	<script src="resources/js/common.js"></script>
	
	<script>
		$(document).ready(function() {
			common.init();
		})
	</script>
</body>
</html>