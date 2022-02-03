<html>
<head>
</head>
<body>
	<h1>${title}</h1>

	<p>${title|noescape}</p>
	<p>${title.length()}</p>
	<p>${title.equals(${title|Object})}</p>
	<p>${age}</p>
	<p>${age.class}</p>
	<p>${age|Integer}</p>
	<p>{{${age|Integer} > 516 }}</p>
	<p>${map.get("key1")}</p>
	<p>${map.get("key2")}</p>
	
	<div>{{10>1 ? "true": "false"}}</div>
	<div>{{${age|Integer}>1 ? "true": "false"}}</div>
	<div class="{{10>1 ? "true": "false"}}"></div>
	
	START
	<p></p>
	SEPARATOR
	< p></ p>
	END	
	
	<p><t:tagName message='"Text of message"' noescape text = "text"another="aa"/></p>
	<p>
		<t:tagName >
			PairedTag 
		</t:tagName>
	</p>
	
	<p t:paramTagName="value">
	</p>

<%
for (int i = 0; i < 10; i++) {
	System.err.println("#" + i);
}
%>

<%-- <% System.err.println("ERROR"); %> --%>

<p><t:tagName message='${title}' /></p>
<p><t:tagName message='{{10>1 ? "true": "false"}}' /></p>
	
</body>
</html>