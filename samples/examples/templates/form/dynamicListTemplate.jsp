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