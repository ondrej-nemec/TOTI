<html>
<head>
	<title>Form - ${title}</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
</head>
<body>

	<h1>Form - ${title}</h1>

	<div id="flash"></div>

	<t:control name="form">
		<h2>Simple list</h2>
		<fieldset toti-form-dynamic-container="simple-list">
			<legend><button toti-form-add-button="simple-list">Add</button></legend>
			<template toti-form-dynamic-template="simple-list">
				<t:form label="simple-list[]" />
				<t:form input="simple-list[]" /> 
				<t:form error="simple-list[]" />
				
				<button toti-form-remove-button="simple-list">Remove</button>
			</template>
		</fieldset>
		
		<t:form input="submit" /> 
	</t:control>

</body>
</html>