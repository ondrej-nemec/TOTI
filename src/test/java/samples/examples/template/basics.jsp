<html>
<head>
	<title>Template example - basics</title>
</head>
<body>

	<h1>Basics template examples</h1>
	
	<h2>Java code</h2>
	
	<p>
		<%
			System.err.println("Java code inside TOTI template");
			/* following line write text to template */
			write("Text from Java code");
		%>
	</p>
	
	<h2>Comment</h2>
	
	<p>
		<%--This text will not appear in HTML page --%>
	</p>
	
	<h2>In line code</h2>
	
	<p>
		Java code with returning value: {{ 10 > 9 ? "Math works" : "Math does not works" }} <br>
		Java code with returning value: <%= 10 > 9 ? "Math works" : "Math does not works" %>
	</p>
	
	<h2>Variable</h2>
	
	<p>
		Variable value: ${title}
	</p>
	
	<h2>Tags</h2>
	
	<p>
		Tag in example create link to this page: <t:link controller="samples.examples.template.TemplateExample" method="basics" />
	</p>
	
	<h2>Parameters</h2>
	
	<p>
		Parameter in example create link to this page: <a t:href="samples.examples.template.TemplateExample:basics">link</a>
	</p>
	
</body>
</html>