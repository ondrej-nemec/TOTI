<html>
<head>
<title>Develop Tools - Profiler bar</title>
<!-- 
	<link rel="stylesheet" href="/toti/totiStyle.css" />
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
	 -->
	<script src="/js/totiSettings.js"></script>
	<script src="/js/totiImages.js" ></script>
	<script src="/js/totiUtils.js" ></script>
	<script src="/js/totiTranslations.js" ></script>
	<script src="/js/totiStorage.js" ></script>
	<script src="/js/totiProfiler.js" ></script>
	<script src="/js/totiLang.js" ></script>
	<script src="/js/totiLoad.js" ></script>
	<script src="/js/totiAuth.js" ></script>
	<script src="/js/totiDisplay.js" ></script>
	<script src="/js/totiControl.js" ></script>
	<script src="/js/totiForm.js" ></script>
	<script src="/js/totiGrid.js" ></script>
</head>
	<h1>Profiler bar</h1>
	<script>
		/* make request on self for more data in profiler*/
		for (i = 0; i < 3; i++) {
			setTimeout(function() {
				totiLoad.async("", "get", {}, {}, function(){}, function(){});
			}, 1000 * i);	
		}
	</script>
</html>