<html>
<head>
<title>Develop Tools - Profiler bar</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
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