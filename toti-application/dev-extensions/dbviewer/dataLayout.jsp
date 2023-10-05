<t:layout path="/dbviewer/layout.jsp"/>
<t:block name="context">
	<style type="text/css">
		.menu {
			width: 25%;
		}
		#logout {
			text-align: right;
		}
	</style>
	<div>
		<div id="logout">
			<form action="/toti/db" method="post">
				<input type="submit" value="Logout" name="logout"/>
			</form>
		</div>
		<div class="menu">
		<%--	<h3>Tables:</h3>
			<t:foreach key="String table" value="String type" map="${tables}">
				<div class="left">
					<strong><t:out name="type"/>:</strong>
					<a href="/toti/db/<t:out name='table'/>"><t:out name="table"/></a>
				</div>
			</t:foreach>  --%>
		</div>
		<div>
			<t:include block="data" />
		</div>
	</div>
</t:block>