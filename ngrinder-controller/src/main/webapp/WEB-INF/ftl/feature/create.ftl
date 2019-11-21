<!DOCTYPE html>
<html>
<head>
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
	<META HTTP-EQUIV="Expires" CONTENT="-1">
	<#include "../common/common.ftl">
	<#include "../common/jqplot.ftl">
	<title><@spring.message "perfTest.title"/></title>
	<link href="${req.getContextPath()}/css/slider.css" rel="stylesheet">
	<link href="${req.getContextPath()}/plugins/datepicker/css/datepicker.css" rel="stylesheet">
	<style>
	.popover {
		width: auto;
		min-width: 200px;
		max-width: 600px;
		max-height: 500px;
	}

	.select-item {
		width: 60px;
	}

	.control-label input {
		vertical-align: top;
		margin-left: 2px
	}

	li.monitor-state {
		height: 10px;
	}
	.controls code {
		vertical-align: middle;
	}

	.datepicker {
		z-index:1151;
	}

	div.chart {
		border: 1px solid #878988;
		margin-bottom: 12px;
	}

	div.modal-body div.chart {
		border:1px solid #878988;
		height:250px;
		min-width:500px;
		margin-bottom:12px;
		padding:5px
	}

	.table thead th {
		vertical-align: middle;
	}

	.jqplot-yaxis {
		margin-right: 20px;
	}

	.jqplot-xaxis {
		margin-top: 5px;
	}

	.rampup-chart {
		width: 400px;
		height: 300px
	}

	div.div-host {
		border: 1px solid #D6D6D6;
		height: 50px;
		margin-bottom: 8px;
		overflow-y: scroll;
		border-radius: 3px 3px 3px 3px;
	}

	div.div-host .host {
		color: #666666;
		display: inline-block;
		margin-left: 7px;
		margin-top: 2px;
		margin-bottom: 2px;
	}

    .add-host-btn {
        margin-top:27px;
        margin-left:287px;
        position:absolute
    }

	i.expand {
        background: url('${req.getContextPath()}/img/icon_expand.png') no-repeat;
        display: inline-block;
		height: 16px;
		width: 16px;
		line-height: 16px;
		vertical-align: text-top;
	}

	i.collapse{
        background: url('${req.getContextPath()}/img/icon_collapse.png') no-repeat;
        display: inline-block;
		height: 16px;
		width: 16px;
		line-height: 16px;
		vertical-align: text-top;
	}

	#test_name + span {
		float: left;
	}

	#query_div label {
		width: 100px;
	}

	.form-horizontal .control-group {
		margin-bottom:10px;
	}

	.controls .span3 {
		margin-left: 0;
	}

	.control-group.success td > label[for="test_name"] {
		color: #468847;
	}

	.control-group.error td > label[for="test_name"] {
		color: #B94A48;
	}

	#script_control.error .select2-choice {
		border-color: #B94A48;
		color: #B94A48;
	}

	#script_control.success .select2-choice {
		border-color: #468847;
		color: #468847;
	}

	legend {
		padding-top: 10px;
	}

	label.region {
		margin-left:-40px;
	}

	.span4-5 {
		width: 340px;
	}
	.span3-4 {
		width: 260px;
	}
	.span2-3 {
		width: 180px;
	}
	</style>
</head>

<body>
<div id="wrap">
	<#include "../common/navigator.ftl">
	<div class="container">
		123
	</div>
	<!--end container-->

	<div class="modal hide fade" id="schedule_modal">
		<div class="modal-header">
			<a class="close" data-dismiss="modal">&times;</a>
			<h4>
				<@spring.message "perfTest.running.scheduleTitle"/>
			</h4>
		</div>
		<div class="modal-body">
			<div class="form-horizontal">
				<fieldset>
					<div class="control-group">
						<label class="control-label"><@spring.message "perfTest.running.schedule"/></label>
						<div class="controls form-inline">
							<input type="text" class="input span2" id="scheduled_date" value="" readyonly>&nbsp;
							<select id="scheduled_hour" class="select-item"></select> : <select id="scheduled_min" class="select-item"></select>
							<code>HH:MM</code>
							<div class="help-inline" class="margin-left:30px"></div>
						</div>
					</div>
				</fieldset>
			</div>
		</div>
		<div class="modal-footer">
			<a class="btn btn-primary" id="run_now_btn"><@spring.message "perfTest.running.runNow"/></a> <a class="btn btn-primary" id="add_schedule_btn"><@spring.message "perfTest.running.schedule"/></a>
		</div>
	</div>
	<#include "../perftest/host_modal.ftl">
</div>
<#include "../common/copyright.ftl">
<script src="${req.getContextPath()}/plugins/datepicker/js/bootstrap-datepicker.js"></script>
<script src="${req.getContextPath()}/js/bootstrap-slider.min.js"></script>
<script src="${req.getContextPath()}/js/ramp_up.js?${nGrinderVersion}"></script>

</body>
</html>
