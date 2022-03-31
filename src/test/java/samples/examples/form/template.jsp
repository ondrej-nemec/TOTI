<html>
<head>
	<title>Form - ${title}</title>
	<link rel="stylesheet" href="/toti/totiStyle.css" />
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<h1>Form - ${title}</h1>

	<div id="flash"></div>
	
	<t:control name="form">
		<!-- error container for global errors -->
		<t:error name="form" />
		<div>
			<t:label name="textInput" class="custom-class" id="some-id" />
			<t:input name="textInput" class="custom-class" id="some-id" />
			<t:error name="textInput" class="custom-class" id="some-id" />
		</div>
		<div>
			<t:label name="numberInput" />
			<t:input name="numberInput" />
			<t:error name="numberInput" />
		</div>
		<div>
			<t:label name="notExpected" />
			<t:input name="notExpected" />
			<t:error name="notExpected" />
		</div>
		<div>
			<t:input name="submit1" />
			<t:input name="submit2" />
		</div>
	</t:control>

</body>
</html>