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
	
	<h2>Returning code</h2>
	
	<p>
		Java code with returning value: <%= 10 > 9 ? "Math works" : "Math does not works" %>
	</p>
	
	<h2>Variable</h2>
	
	<p>
		Variable value: ${title}
	</p>
	
	<h2>Tags</h2>
	
	<p>
		Tag in example create link to this page: 
		<t:link
		 controller="toti.samples.template.TemplateExample" 
		 method="basics" />
	</p>
	
	<h2>Parameters</h2>
	
	<p>
		Parameter in example create link to this page: <a t:href="toti.samples.template.TemplateExample:basics">link</a>
	</p>
	
	<h2>Inside each other</h2>
	
	<h3>Variable in Java code</h3>
	
	<% 
		System.err.println("Title is: " + ${title});
	%>
	
	<h3>Variable inside returning code</h3>
	
	<p>
		Title variable is <%= ${title.length()|Integer} > 5 ? "long" : "short" %>
	<p>
	
	<h3>Variable in comment = commented variable</h3>
	
	<%--This is commented ${title} --%>
	
	<h3>Variable in tag</h3>
	
	<p>
		<t:if cond="${title.length()|Integer} > 5">
			Title variable is long
		<t:else />
			Title variable is short
		</t:if>
	</p>	
	
	<h3>Returning code in Tag</h3>
	
	<p>
		<t:if cond="<%= 10 > 5 %>">
			Math works
		<t:else />
			Math not works
		</t:if>
	</p>
	
	<h3>Tag in comment</h3>
	<%--
	<p>
		<t:if cond="${title.length()} > 5">
			Title variable is long
		<t:else />
			Title variable is short
		</t:if>
	</p>
	 --%>
</body>
</html>