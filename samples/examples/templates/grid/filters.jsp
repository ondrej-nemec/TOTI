<html>
<head>
	<title>Grid example</title>
<%--	<link rel="stylesheet" href="/toti/totiStyle.css" />
	 <script src="/toti/totiJs.js" nonce="${nonce}"></script> --%>
	 <!-- TODO -->
	<script src="/js/totiImages.js" ></script>
	<script src="/js/totiUtils.js" ></script>
	<script src="/js/totiSortedMap.js" ></script>
	<script src="/js/totiTranslations.js" ></script> 
	<script src="/js/totiStorage.js" ></script>
	<script src="/js/totiLang.js" ></script>
	<script src="/js/totiLoad.js" ></script>
	<script src="/js/totiAuth.js" ></script> 
	<script src="/js/totiGridDefaultTemplate.js" ></script>
	<script src="/js/totiFormDefaultTemplate.js" ></script>
	<script src="/js/totiDisplay.js" ></script>
	<script src="/js/totiControl.js" ></script>
	
	<script src="/js/totiGridCustomTemplate.js" ></script>
	<script src="/js/totiGrid.js" ></script>
	
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