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
		<h2>Simple list</h2>
		<fieldset name="simple-list-container">
			<template name="pattern">
				<%-- %s is index, --%>
				<t:label name="simple-list[%s]" />
				<t:input name="simple-list[%s]" /> 
				<t:error name="simple-list[%s]" />
				
				<span name="remove">Remove</span>
			</template>
			<span name="add">Add</span>
		</fieldset>
		
		<h2>Simple map</h2>
		<fieldset name="simple-map-container">
			<template name="pattern">
				<%-- %s is name, in this case 'item_x' (x = {1, 2}) --%>
				<t:label name="simple-map[%s]" />
				<t:input name="simple-map[%s]" /> 
				<t:error name="simple-map[%s]" />
				
				<span name="remove">Remove</span>
			</template>
			<span name="add">Add</span>
		</fieldset>
		
		<t:input name="submit" /> 
	</t:control>

</body>
</html>