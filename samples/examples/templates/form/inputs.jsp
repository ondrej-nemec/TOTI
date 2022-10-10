<html>
<head>
	<title>Form - ${title}</title>
	<!--  <link rel="stylesheet" href="/toti/totiStyle.css" />
	<script src="/toti/totiJs.js" nonce="${nonce}"></script> -->
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
	
	<script src="/js/totiFormCustomTemplate.js" ></script>
	<script src="/js/totiForm.js" ></script>
</head>
<body>

	<h1>Form - ${title}</h1>

	<div id="flash"></div>

	<t:control name="form"/>

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
		
		function afterPrintCallback() {
			totiDisplay.flash("success", "After print");
		}
		function beforeBindCallback(values) {
			totiDisplay.flash("success", "Before bind: " + values.text);
		}
		function afterBindCallback(values) {
			totiDisplay.flash("success", "After bind: " + values.text);
		}
	</script>

</body>
</html>