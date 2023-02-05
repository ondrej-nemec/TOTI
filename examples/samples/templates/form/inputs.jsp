<html>
<head>
	<title>Form - ${title}</title>
	
	<script src="/js/totiSortedMap.js" nonce="${nonce}"></script>
	<script src="/js/totiTranslations.js" nonce="${nonce}"></script>
	<script src="/js/totiImages.js" nonce="${nonce}"></script>
	<script src="/js/totiStorage.js" nonce="${nonce}"></script>
	<script src="/js/totiUtils.js" nonce="${nonce}"></script>
	<script src="/js/totiLang.js" nonce="${nonce}"></script>
	<script src="/js/totiLoad.js" nonce="${nonce}"></script>
	<script src="/js/totiAuth.js" nonce="${nonce}"></script>
	<script src="/js/totiFormCustomTemplate.js" nonce="${nonce}"></script>
	<script src="/js/totiFormDefaultTemplate.js" nonce="${nonce}"></script>
	<script src="/js/totiGridCustomTemplate.js" nonce="${nonce}"></script>
	<script src="/js/totiGridDefaultTemplate.js" nonce="${nonce}"></script>
	<script src="/js/totiDisplay.js" nonce="${nonce}"></script>
	<script src="/js/totiControl.js" nonce="${nonce}"></script>
	<script src="/js/totiForm.js" nonce="${nonce}"></script>
	<script src="/js/totiGrid.js" nonce="${nonce}"></script>
	<!-- <script src="/toti/totiJs.js" nonce="${nonce}"></script> -->
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