<t:layout path="/dbviewer/dataLayout.jsp" />
<t:block name="data">
	<t:foreach item="toti.dbviewer.QueryResult item" collection="${queries}">
		<div>
			<div><t:out name="item.getSql()" /></div>
			<t:if cond="item.getException() != null">
				<div class="error">
					<t:out name="item.getException()" />
				</div>
			<t:elseif cond="item.getResultSet() != null">
				<t:var type="int" name="i" value="0" /> 
				<div>
					<table>
						<t:foreach item="database.support.DatabaseRow row" collection="item.getResultSet()">
							<t:if cond="i == 0">
								<tr>
									<t:foreach key="String name" value="Object value" map="row.getValues()">
										<th><t:out name="name"/></th>
									</t:foreach>
								</tr>
								<t:set name="i" value="1"/>
							</t:if>
							<tr>
								<t:foreach key="String name" value="Object value" map="row.getValues()">
									<td><t:out name="value"/></td>
								</t:foreach>
							</tr>
						</t:foreach>
					</table>
				</div>
			<t:else>
				<div>
					Result Count: 
					<t:out name="item.getResultCount()" />
				</div>
			</t:if>
		</div>
	</t:foreach>

	<form action="/toti/db" method="post">
		<textarea name="query" rows="15" cols="50">${query}</textarea>
		<br>
		<input type="submit" value="Execute" />
	</form>
</t:block>