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
		<div>
			<t:label name="simple-list[]"/>
			<t:input name="simple-list[]"/>
			<t:error name="simple-list[]"/>
		</div>
		<div>
			<t:label name="simple-list[]"/>
			<t:input name="simple-list[]"/>
			<t:error name="simple-list[]"/>
		</div>
		
		<h2>Simple map</h2>
		
		<h3>Item 1</h3>
		<div>
			<t:label name="simple-map[item1]"/>
			<t:input name="simple-map[item1]"/>
			<t:error name="simple-map[item1]"/>
		</div>
		
		<h3>Item 1</h3>
		<div>
			<t:label name="simple-map[item2]"/>
			<t:input name="simple-map[item2]"/>
			<t:error name="simple-map[item2]"/>
		</div>
		
		<h2>List in map</h2>
		
		<h3>Sublist 1</h3>
		<div>
			<t:label name="list-in-map[sub-list-1][]"/>
			<t:input name="list-in-map[sub-list-1][]"/>
			<t:error name="list-in-map[sub-list-1][]"/>
		</div>
		<div>
			<t:label name="list-in-map[sub-list-1][]"/>
			<t:input name="list-in-map[sub-list-1][]"/>
			<t:error name="list-in-map[sub-list-1][]"/>
		</div>
		
		<h3>Sublist 1</h3>
		<div>
			<t:label name="list-in-map[sub-list-2][]"/>
			<t:input name="list-in-map[sub-list-2][]"/>
			<t:error name="list-in-map[sub-list-2][]"/>
		</div>
		<div>
			<t:label name="list-in-map[sub-list-2][]"/>
			<t:input name="list-in-map[sub-list-2][]"/>
			<t:error name="list-in-map[sub-list-2][]"/>
		</div>
		
	</t:control>

</body>
</html>