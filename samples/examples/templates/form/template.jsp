<html>
<head>
	<title>Form - ${title}</title>
	<!--  <script src="/toti/totiJs.js" nonce="${nonce}"></script> -->
	<script src="/js/totiImages.js" ></script>
	<script src="/js/totiUtils.js" ></script>
	<script src="/js/totiSortedMap.js" ></script>
	<script src="/js/totiTranslations.js" ></script> 
	<script src="/js/totiStorage.js" ></script>
	<script src="/js/totiLang.js" ></script>
	<script src="/js/totiLoad.js" ></script>
	<script src="/js/totiAuth.js" ></script> 
	<script src="/js/totiGridDefaultTemplate.js" ></script>
	<script src="/js/totiFormDefaultTemplate.js" ></script>
	<script src="/js/totiDisplay.js" ></script>
	<script src="/js/totiControl.js" ></script>
	
	<script src="/js/totiFormCustomTemplate.js" ></script>
	<script src="/js/totiForm.js" ></script>
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
		<hr>
		<div>
			<t:label name="numberInput" />
			<t:input name="numberInput" />
			<t:error name="numberInput" />
		</div>
		<hr>
		<div>
			<t:label name="selectInput" />
			<t:input name="selectInput" />
			<t:error name="selectInput" />
		</div>
		<hr>
		<div>
			<t:label name="checkboxInput" />
			<t:input name="checkboxInput" />
			<t:error name="checkboxInput" />
		</div>
		<hr>
		<div>
			<t:label name="notExpected" />
			<t:input name="notExpected" />
			<t:error name="notExpected" />
		</div>
		<hr>
		<div>
			<t:input name="submit1" />
			<t:input name="submit2" />
		</div>
	</t:control>

</body>
</html>