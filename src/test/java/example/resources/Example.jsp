<t:layout path="layout.jsp" />
<t:block name="content">
	<h1>${title}</h1>
	
	<div> <a href="/example-module/example/add"><t:trans message="messages.add-button" /></a> </div>
	<t:control name="exampleControl" />
	
	<script>
		function afterBind(values) {
			console.log("after", values);
		}
		function beforeBind(values) {
			console.log("before", values);
		}
		function colCondition(row) {
			return row.active;
		}
	</script>
</t:block>