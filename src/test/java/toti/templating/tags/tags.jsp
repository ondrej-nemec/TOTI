
<t:var name="i" type="int" value="0" />
<t:while cond="i<10" >
	<t:out name="i" />
	<t:set name="i" value="i+1"/>
</t:while>

<t:if cond="${ifTest}" >Is it true</t:if>

${ifTest} -- <t:out name="${ifTest}" />----------

<t:var type="String" name="global" value='"some-global-var"' />
<t:out name="global" /> -- ${global} -- ${global.length()}