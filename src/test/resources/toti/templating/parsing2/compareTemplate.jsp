<t:layout path="layout.jsp" />
<t:block name="title">Block title</t:block>

<t:block name="content">
	<p>Block variable: ${blockVar}</p>

	<h3>Try</h3>
	<div>
		<t:try>
			Try <br>
			${emptyVar.size()}
		<t:catch name="e"/>
			Catch<br>
			${e}<br>
			<t:out name="e.getMessage()" /><br>
		<t:finally />
			Finally
		</t:try>
	</div>
	
	<h3>Switch</h3>
	<div>
		${switch} <br>
		<t:switch object="${switch}.toString()">
			<t:case cond='"case1"'>Case 1</t:case>
			<t:case cond='"case2"'>Case 2</t:case>
			<t:default>Default</t:default>
		</t:switch>
	</div>
	
	<h3>Variable</h3>
	<div>
		<t:var type="int" name="count" value="0" />
		Vars: ${count} <br>
		<t:set name="count" value="2" />
		Vars: ${count} <br>
		<t:out name="count" /> <br>
		<t:console value="count"/>
	</div>
	
	<h3>If</h3>
	<div>
		<t:if cond="12 > 21">
			IF
		<t:elseif cond="32 < 23" />
			ELSE IF
		<t:else>
			ELSE
		</t:if>
	</div>
	
	<h3>While</h3>
	<div>
		<t:while cond="count < 5">
			<t:set name="count" value="count++" />
			While cycle <br>		
		</t:while>
	</div>
	
	<h3>Do while</h3>
	<div>
		<t:dowhile>
			<t:set name="count" value="count++" />
			Do while <br>
		</t:dowhile cond="count < 7">
	</div>
	
	<h3>For</h3>
	<div>
		<t:for from="int i = 0" to="i < 10" change="i++">
			<t:if cond="i == 5">
				Continue ${i}<br>
				<t:continue />
			<t:elseif cond="i > 8" />
				Break ${i}
				<t:break />
			<t:else />
				FOR ${i}
			</t:if>
			after
		</t:for>
	</div>
	
	<h3>ForEach</h3>
	<div>
		<t:foreach item="Object item" collection="${list}">
			<t:out name="item" /> - ${item} <br>
		</t:foreach>
		<br>
	</div>
	<div>
		<t:foreach key="Object key" value="Object value" map="${map}">
			<t:out name="key" />: <t:out name="value" /> - ${key}: ${value} <br>
		</t:foreach>
	</div>

	<h3>Trans</h3>
	<div><t:trans message="common.trans" var1="A" var2="B" var3="C"/></div>

	<h3>Code</h3>
	<div>
		<%
			write("working code");
		%>
	</div>

	<h3>Inline code</h3>
	<div style='height:100px; width:100px;background-color:{{ 10 < 5 ? "green" : "red"}}'></div>

</t:block>
