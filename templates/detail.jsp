<t:layout path="layout.jsp" />
<t:block name="content" >
	<t:if cond="${itemId} == null">
		<h1>Add Item</h1>
	<t:else/>
		<h1>Item #${itemId}</h1>
	</t:if>
	<t:control name="personControl" />
</t:block>
