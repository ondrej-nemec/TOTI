<%-- <t:layout path="layout.jsp" />
<t:block name="content" > --%>

<link rel="stylesheet" href="/toti.css" />

<script src="https://code.jquery.com/jquery-3.5.1.min.js" integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0=" crossorigin="anonymous"></script>
<script src="/toti.js" crossorigin="anonymous"></script>

<div id="flash">
</div>

	<h1>${title}</h1>
	<div> <a href="/entity/add">Add</a> </div>
	<t:control name="control" />
	
	<script>
		function actionSuccess(res) {
			alert("success");
			console.log(res);
		}
		function actionFailure(xhr) {
			alert("failure");
			console.log(xhr);
		}
		function isMainRenderer(value) {
			if (value) {
				return "<t:trans message='module.yes' />";
			} else {
				return "<t:trans message='module.no' />";
			}
		}
		function a() {
			console.log("before callback");
		}
		function b() {
			console.log("after callback");
		}
	/*$('#flash').append(totiControl.inputs.button(function() {}, title = "info", {'class': 'toti-button-info'}));
		$('#flash').append(totiControl.inputs.button(function() {}, title = "error", {'class': 'toti-button-danger'}));
		$('#flash').append(totiControl.inputs.button(function() {}, title = "warning", {'class': 'toti-button-warning'}));
		$('#flash').append(totiControl.inputs.button(function() {}, title = "success", {'class': 'toti-button-success'}));
		$('#flash').append(totiControl.inputs.button(function() {}, title = "basic", {'class': 'toti-button-basic'}));
		$(document).ready(function() {
			totiControl.display.flash('error', 'some text error');
			totiControl.display.flash('success', 'some text success');
			totiControl.display.flash('info', 'some text info');
			totiControl.display.flash('warning', 'some text warning');
			totiControl.display.flash('error', 'some text error 1');
			totiControl.display.flash('success', 'some text success 1');
			totiControl.display.flash('info', 'some text info 1');
			totiControl.display.flash('warning', 'some text warning 1');
		});*/
	</script>
	
<%-- </t:block> --%>
