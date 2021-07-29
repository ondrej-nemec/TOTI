<html>
<head>
</head>
<body>
	<h1>${title}</h1>

	<p>${title.length()}</p>
	<p>${title.equals(${title|Object})}</p>
	<p>${age}</p>
	<p>${age.class}</p>
	<p>${age|Integer}</p>
	<p>{{${age|Integer} > 516 }}</p>

<%
for (int i = 0; i < 10; i++) {
	System.err.println("#" + i);
}
%>

<%-- <% System.err.println("ERROR"); %> --%>

</body>
</html>