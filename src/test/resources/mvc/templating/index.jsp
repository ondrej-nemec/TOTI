<!DOCTYPE html>
<html>
	<body>
		<h1>${title}</h1>
		<t:console ac${title} value="value to console output${outputValue}" />
		<br>
		${outputValue}
		
		<t:for from="int i = 0" to="i < 10" change="i++">
			in for<br>
		</t:for>
		
		<t:if cond="a${title} != null">
			Title is not null
		</t:if>
	</body>
</html>