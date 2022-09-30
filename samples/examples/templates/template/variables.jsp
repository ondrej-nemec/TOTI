<html>
<head>
	<title>Template example - variables</title>
</head>
<body>

	<h1>Variables in templates</h1>
	
	<p>
		Some variables are in JS, so please open develop console for view.
	</p>
	
	<h2>Print variable</h2>
	
	<p class='${title}'>${title}</p>
	
	<style>
		.${title} {
			"${title}": ${title};
		}
	</style>
	
	<script>
		console.log('${title}');
	</script>
	
	<h2>Print variable unescaped</h2>
	
	<p id='${color|noescape}'>${color|noescape}</p>
	
	<style>
		${color|noescape} {
			"${color|noescape}": ${color|noescape};
		}
	</style>
	
	<script>
		console.log('${color|noescape}');
	</script>
	
	<h2>Print variable as URL</h2>
	
	<p href='${url}'>${url}</p>
	
	<style>
		.backgroun {
			"background-image": ${url};
		}
	</style>
	
	<script>
		console.log('${url}');
	</script>
	
	<h2>Call method</h2>
	
	<p>Title length: ${title.length()}</p>
	
	<style></style>
	<script></script>
	
	<h2>Call method 2:</h2>
	
	<p>Title class: ${title.class} or ${title.getClass()}</p>
	
	<style></style>
	
	<script></script>
	
	<h2>Get variable as</h2>
	
	<p>
		Variable origin: ${age.class}<br>
		Get as origin type: ${age}<br>
		Get as number: ${age|Integer}
	</p>
	
	<style></style>
	
	<script>
		console.log("As origin type", ${age});
		console.log("As number", ${age|Integer});
	</script>
	
	<h2>Secure XSS</h2>
	
	<p>
		Example of excaping varibles.
		For full XSS protection see <a href="https://cheatsheetseries.owasp.org/cheatsheets/Cross_Site_Scripting_Prevention_Cheat_Sheet.html">OWASP</a>
	</p>
	
	<p class="${attack-parameter}">
		Attack in HTML: ${attack-html} and in parameter
	</p>
	
	<p onclick="${attack-onclick}" style="background-image: '${attack-color}'">
		Attack in 'onclick' and 'style' attribute
	</p>
	
	<p>
		Attack in src: <img src="${attack-src}" />
	</p>
	
	<style>
		.colored {
			color: '${attack-color}';
		}
	</style>
	
	<script>
		var myVariable = '${attack-js}';
		console.log(myVariable);
	</script>
	
	<%--
	<h2></h2>
	
	<p></p>
	
	<style>
	
	</style>
	
	<script>
	
	</script>
	 --%>
</body>
</html>