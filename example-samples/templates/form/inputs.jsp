<html>
<head>
	<title>Form - ${title}</title>
	
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<h1>Form - ${title}</h1>

	<div id="flash"></div>

	<script>
		function buttonOnSuccess(result) {
			totiDisplay.flash("success", "Button: " + result);
		}
		function buttonOnFailure(xhr) {
			totiDisplay.flash("error", "Button: " + xhr);
		}
		function submitOnSuccess(result, submit, form) {
			totiDisplay.flash("success", "Submit: " + result);
		}
		function submitOnFailure(xhr, submit, form) {
			totiDisplay.flash("error", "Submit: " + xhr);
		}

		function beforeRenderCallback() {
			totiDisplay.flash("success", "Before render");
		}
		function afterRenderCallback() {
			totiDisplay.flash("success", "After render");
		}
		function beforeBindCallback(values) {
			totiDisplay.flash("success", "Before bind: " + values.text);
		}
		function afterBindCallback(values) {
			totiDisplay.flash("success", "After bind: " + values.text);
		}
	</script>

	<t:control name="form"/>
	
</body>
</html>