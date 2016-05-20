<!--<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>-->
<!DOCTYPE HTML>
<html>
<head>
	<meta charset="UTF-8">
	<jsp:include page="/WEB-INF/views/page_head.jsp"/>
</head>
<body>
	<div id="wrap">
		<div id="main" class="container-fluid">
			<jsp:include page="/WEB-INF/views/header.jsp"/>
			<div class="row">
				<div class="col-md-2 asa-left-column">
					<div class="row asa-left-row">
						<div class="col-sm-4 col-md-12 asa-left-button">
							<button id="api-key-button" class="btn btn-block btn-default hidden-md no-overflow" title="CTT Local Agent Password">
								<span class="glyphicon glyphicon-lock"></span> CTT Local Agent Password</button>
							<button class="btn btn-block btn-default visible-md-block api-key" title="CTT Local Agent Password">
								<span class="glyphicon glyphicon-lock"></span> CTT-LA Pass</button>
						</div>
						<div class="col-sm-4 col-md-12 asa-left-button">
							<a type="button" class="btn btn-block btn-default hidden-md no-overflow" title="Download CTT Local Agent" href="end/download">
								<span class="glyphicon glyphicon-download-alt"></span> Download CTT Local Agent</a>	
							<a type="button" class="btn btn-block btn-default visible-md-block" title="Download CTT Local Agent" href="end/download">
								<span class="glyphicon glyphicon-download-alt"></span> CTT-LA</a>	
						</div>
						<div class="col-sm-4 col-md-12 asa-left-button">
							<button id="view-tccls" class="btn btn-block btn-default" title="View TCCLs"><span class="glyphicon glyphicon-th-list"></span> View TCCLs</button>
						</div>
					</div>
					<div class="row">
						<ul class="nav nav-pills nav-stacked asa-left-nav">
							<li id="project-nav" class="selected-step" role="presentation"><a disabled href="project">PROJECT</a></li>
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
			<div class="row" id="navigation-buttons">
				<div class="col-xs-12 col-md-10 col-md-offset-2">
					<button id="prevButton" disabled class="btn btn-custom disabled"><span class="glyphicon glyphicon-chevron-left"></span> Previous Step</button>
					<button id="endButton" disabled class="btn btn-custom disabled"><span class="glyphicon glyphicon-floppy-save"></span> Save Project</button>
					<span id="nextButtonTooltip" class="helper" data-toggle="tooltip" title="">
						<button id="nextButton" disabled class="btn btn-custom disabled">Next Step <span class="glyphicon glyphicon-chevron-right"></span></button>
					</span>
				</div>
			</div>
		</div>
	</div>
	
	 <!-- View TestCases Modal -->
     <div id="viewTcclModal" class="modal fade" tabindex="-1" role="dialog" aria-hidden="true">
     	<div class="modal-dialog modal-lg">
     		<div class="modal-content">
     			<div class="modal-header"><h3 style="margin:0;"></h3></div>
     			<div class="modal-body">
     				<table id="viewTcclTable" class="table table-hover">
     					<thead>
     						<tr>
     							<th class="hide">Test Case ID</th>
     							<th width="20%">Test Case</th>
     							<th width="60%">Test Case Description</th>
     							<th width="10%">Type</th>
     							<th width="10%">Enable</th>
     						</tr>
     					</thead>
     					<tbody>
     					</tbody>
     				</table>
     			</div>
     			<div class="modal-footer">
     				<button id="tcBack" class="btn btn-default" data-dismiss="modal"><span class="glyphicon glyphicon-chevron-left"></span> Back</button>
     			</div>
     		</div>
     	</div>
     </div>
	
	<jsp:include page="/WEB-INF/views/footer.jsp"/>
	<script src="resources/js/common.js"></script>
	<script src="resources/js/waitingDialog.js"></script>
	<script src="resources/js/yesNoModal.js"></script>
	
	<script>
		$(document).ready(function()
		{
			common.init();
		})
		
		// Function added to clear forms validation when modal is hidden. JQueryValidation .resetForm does not work with Bootstrap 3
		$.fn.clearValidation = function(){var v = $(this).validate();$('[name]',this).each(function(){v.successList.push(this);v.showErrors();});v.resetForm();v.reset();};
	</script>
</body>
</html>