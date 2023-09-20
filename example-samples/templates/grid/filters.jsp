<html>
<head>
	<title>Grid example</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
	
</head>
<body>

	<h1>Grid example</h1>

	<div id="flash"></div>

	<t:control name="grid"/>

	<script>
		/* buttons */
		function buttonOnSuccess(res) {
			totiDisplay.flash("success", "Button: " + res);
		}
		function buttonOnFailure(xhr) {
			totiDisplay.flash("error", "Button: " + xhr);
		}
		/* actions */
		function actionOnSuccess(res) {
			totiDisplay.flash("success", "Action: " + res);
		}
		function actionOnFailure(xhr) {
			totiDisplay.flash("error", "Action: " + xhr);
		}
		/* renderers */
		function onColumnRenderer(value, values) {
			return value + " - " + values.month;
		}
		function rowRenderer(container, data) {
			container.style['background-color'] = "lightgreen";
		}
	</script>

</body>
</html>