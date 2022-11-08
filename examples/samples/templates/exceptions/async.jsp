<html>
<head>
	<title>Exception example - async calls</title>
</head>
<body>

	<table id="requests">
		<tr>
			<th>Name</th>
			<th>Result</th>
		</tr>
		
		<tr>
			<td><a href="http://localhost:8080/not-existing" target=_blank>
				Not existing route
			</a></td>
			<td></td>
		</tr>
		<tr>
			<td><a href="http://localhost:8080/examples/exceptions/method" target=_blank>
				Exception in method
			</a></td>
			<td></td>
		</tr>
		<tr>
			<td><a href="http://localhost:8080/examples/exceptions/secured" target=_blank>
				Required logged user
			</a></td>
			<td></td>
		</tr>
		<tr>
			<td><a href="http://localhost:8080/examples/exceptions/post" target=_blank>
				Requred POST method
			</a></td>
			<td></td>
		</tr>
		<tr>
			<td><a href="http://localhost:8080/examples/exceptions/notemplate" target=_blank>
				Missing template
			</a></td>
			<td></td>
		</tr>
		<tr>
			<td><a href="http://localhost:8080/examples/exceptions/intemplate" target=_blank>
				Exception in template
			</a></td>
			<td></td>
		</tr>
		<tr>
			<td><a href="http://localhost:8080/examples/exceptions/syntax" target=_blank>
				Template syntax exception
			</a></td>
			<td></td>
		</tr>
		
	</table>

	<script>
		var table = document.getElementById("requests");
		table.querySelectorAll("tr td a").forEach(function(link) {
			var xhr = new XMLHttpRequest();
			xhr.open("get", link.getAttribute("href"));
			var onDone = function(xhr) {
				link.parentElement.parentElement.lastChild.innerText = xhr.currentTarget.status + " " + xhr.currentTarget.response;
			};
			xhr.onload = onDone;
			xhr.onerror = onDone;
			xhr.send();
		});
	</script>

</body>
</html>