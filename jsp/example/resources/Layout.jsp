<html>
<head>
	<title></title>
	<link rel="stylesheet" href="/styleToti.css" />
	<!-- 
	<script src="/toti/totiJs.js"></script>
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
<body>
	<div id="lang"></div>
	<script>
		//totiAuth.onLoad();
		document.getElementById("lang").innerText = totiLang.getLang();
	</script>
	<div id="flash"></div>
	<t:include block="content" />
</body>
</html>