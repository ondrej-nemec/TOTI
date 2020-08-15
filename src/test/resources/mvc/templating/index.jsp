<!DOCTYPE html>
<html>
	<body>
		<h1>Testing JSP for tags and variables</h1>
	
		<h2>Variables</h2>
		
		<h3>Printing variable "article" to &lt;p&gt;</h3>
		<p>${article}</p>
		
		<h3>Printing variable "xss" with XSS danger text</h3>
		<p>${xss}</p>
		
		<h2>Tags</h2>
		
		<h3>Print to Java console</h3>
		<t:console value='"----ConsoleOutput-----"' />
		
		<h3>Variable - create, set, print</h3>
	    <t:var type="double" name="number"/>
	    <t:set name="number" value="2.4"/>
	    <t:out var="number" />
	    <br>
	    <t:var type="String" name="xss2" value='"<script>alert(\"Successfully XSS2\");</script>"'/>
	    <t:out var="xss2" />
	    
		<h3>Translate</h3>
		<t:trans message="single key" /><br>
		<t:trans message="parametrized key">
			<t:param key='"paramKey"' value='"paramValue"'/>
		</t:trans>
		
		<h3>Try - catch - finally</h3>
		<t:try >
			${not-existing-var.class}
		<t:catch exception="Exception" name="e"/>
			Exception occured: <t:out var="e" />
		<t:finally />
			<br>
			after
		</t:try>
		
		<h3>if - else if - else</h3>
		<t:if cond="1 == 2" >
			1 == 2
		<t:elseif cond="2 == 1" />
			2 == 1
		<t:else />
			Mathic works
		</t:if>
		
		<h3>Switch - case - default</h3>
		<t:switch object='"aaaa!"' >
			<t:case cond='"10"'>
			<t:case cond='"--"'/>
			<t:case cond='"aaa!"' >AAAA</t:case>
			<t:default/>
		</t:switch>
		
		<h3>For, while, for-each, Do-while, Continue, Break</h3>
		<t:for from="int i = 0" to="i < 5"  change="i++"> 
			<t:out var='"For iteration: " + i' /> <br>
		</t:for> <br>
		<t:var type="int" name="index" value="0"/>
		<t:while cond="index < 3">
			<t:out var='"While iteration: " + index' /> <br>
			<t:set name="index" value="index + 1" />
		</t:while> <br>
		TODO for-each, Do-while, Continue, Break
		
		<h3>Import, layout, define, block</h3>
		TODO
		<h3>Link</h3>
		TODO
		<h3>Print not escaped variable</h3>
		TODO
		
		<h2>Tags and variables</h2>
		<h3>For with given limit</h3>
		<t:for from="int i = 0" to="i < (int)${limit}"  change="i++"> 
			<t:out var='"Iteration: " + i' />
		</t:for> 

	</body>
</html>