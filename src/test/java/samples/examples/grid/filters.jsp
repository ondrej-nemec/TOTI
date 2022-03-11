<html>
<head>
	<title>Grid example</title>
	<link rel="stylesheet" href="/toti/totiStyle.css" />
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
		function onRowRenderer(container, values) {
			if (values.range > 50) {
				container.querySelector("td[index='0']").setAttribute("style", "color: red; font-weight: bold;");
			}
			return container;
		}
	</script>

</body>
</html>