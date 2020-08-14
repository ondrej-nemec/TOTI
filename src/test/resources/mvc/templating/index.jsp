<!DOCTYPE html>
<html>
	<body>
		<h1>${title}</h1>
		<t:console value='"----" + ${outputValue} + "-----"' />
		<br>
		${outputValue}

		<t:for from="int i = 0" to="i < (int)${limit}"  change="i++"> 
			in for<br>
		</t:for> 

		<t:if cond='"a" + ${title} != null'>
			Title is not null<br>
			${title.length()}<br>
			<%-- ${title.equals(${limit})} <br>  --%>
		</t:if>

	</body>
</html>