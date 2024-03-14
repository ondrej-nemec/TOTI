<html>
<head>
	<title>Form - ${title}</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<h1>Form - ${title}</h1>

	<div id="flash"></div>
	
	<t:control name="form">
		<!-- error container for global errors -->
		<t:form error="form" />
		<div>
			<t:form label="textInput" class="custom-class" id="some-id" />
			<t:form input="textInput" class="custom-class" id="some-id" />
			<t:form error="textInput" class="custom-class" id="some-id" />
		</div>
		<hr>
		<div>
			<t:form label="numberInput" />
			<t:form input="numberInput" />
			<t:form error="numberInput" />
		</div>
		<hr>
		<div>
			<t:form label="selectInput" />
			<t:form input="selectInput" />
			<t:form error="selectInput" />
		</div>
		<hr>
		<div>
			<t:form label="checkboxInput" />
			<t:form input="checkboxInput" />
			<t:form error="checkboxInput" />
		</div>
		<hr>
		<div>
			<t:form label="notExpected" />
			<t:form input="notExpected" />
			<t:form error="notExpected" />
		</div>
		<hr>
		<div>
			<t:form input="submit1" />
			<t:form input="submit2" />
		</div>
	</t:control>

</body>
</html>