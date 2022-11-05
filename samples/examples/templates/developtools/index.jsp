<t:layout path="/layout.jsp">
<t:block name="content">
	<h1>Profiler bar</h1>
	
	<p>This is from main file</p>
	
	<hr>
	<t:include file="/included.jsp" index="1">
	<t:include file="/included.jsp" index="2">
	
	<script>
		/* make request on self for more data in profiler*/
		for (i = 0; i < 3; i++) {
			setTimeout(function() {
				totiLoad.async("", "get", {}, {}, function(){}, function(){});
			}, 1000 * i);	
		}
	</script>
</t:block>