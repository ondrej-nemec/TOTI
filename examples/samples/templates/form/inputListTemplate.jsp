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
		<div>
			<t:form label="simple-list[]"/>
			<t:form input="simple-list[]"/>
			<t:form error="simple-list[]"/>
		</div>
		<div>
			<t:form label="simple-list[]"/>
			<t:form input="simple-list[]"/>
			<t:form error="simple-list[]"/>
		</div>
		
		<h2>Simple map</h2>
		
		<h3>Item 1</h3>
		<div>
			<t:form label="simple-map[item1]"/>
			<t:form input="simple-map[item1]"/>
			<t:form error="simple-map[item1]"/>
		</div>
		
		<h3>Item 1</h3>
		<div>
			<t:form label="simple-map[item2]"/>
			<t:form input="simple-map[item2]"/>
			<t:form error="simple-map[item2]"/>
		</div>
		
		<h2>List in map</h2>
		
		<h3>Sublist 1</h3>
		<div>
			<t:form label="list-in-map[sub-list-1][]"/>
			<t:form input="list-in-map[sub-list-1][]"/>
			<t:form error="list-in-map[sub-list-1][]"/>
		</div>
		<div>
			<t:form label="list-in-map[sub-list-1][]"/>
			<t:form input="list-in-map[sub-list-1][]"/>
			<t:form error="list-in-map[sub-list-1][]"/>
		</div>
		
		<h3>Sublist 1</h3>
		<div>
			<t:form label="list-in-map[sub-list-2][]"/>
			<t:form input="list-in-map[sub-list-2][]"/>
			<t:form error="list-in-map[sub-list-2][]"/>
		</div>
		<div>
			<t:form label="list-in-map[sub-list-2][]"/>
			<t:form input="list-in-map[sub-list-2][]"/>
			<t:form error="list-in-map[sub-list-2][]"/>
		</div>
		
	</t:control>

</body>
</html>