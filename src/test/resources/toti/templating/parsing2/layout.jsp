<html>
<head>
</head>
<body>

	<h1>${title}</h1>

	<h2><t:include block="title"/></h2>

	<t:include block="content" blockVar="Working block variable"/>

	<hr>
	<p>Block var outside: ${blockVar}</p>

</body>
</html>