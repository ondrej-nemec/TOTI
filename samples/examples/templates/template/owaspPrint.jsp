<html>
<head>
	<title>Template example - OWASP print</title>
</head>
<body>

	<h1>OWASP print</h1>
	
	<div>
		<p onclick="${first}" style="background-image: '${first}'" class="${first}">
			${first}
			<img src="${first}" />
		</p>
		<style>
			.colored {
				background-image: '${first}';
			}
		</style>
		<script>
			var myVariable = '${first}';
			console.log(myVariable);
		</script>
	</div>
	
	<div>
		<p onclick="${second}" style="background-image: '${second}'" class="${second}">
			${second}
			<img src="${second}" />
		</p>
		<style>
			.colored {
				background-image: '${second}';
			}
		</style>
		<script>
			var myVariable = '${second}';
			console.log(myVariable);
		</script>
	</div>
	
</body>
</html>