<t:layout path="/dbviewer/layout.jsp"/>
<t:block name="context">
	<style type="text/css">
		.login {
			margin-left: 33%;
			margin-right: 33%;
			border: solid 1px;
			padding-left: 2em;
			padding-right: 2em;
		}
		.login div {
			margin-bottom: 0.25em;
		}
		.form-element {
			width: 40%;
			display: inline-block;
		}
		label {
			font-weight: bold;
		}
	</style>
	<div class="login">
		<form method="post" action="/toti/db">
			<h3>Login</h3>
			<div>
				<div class="form-element"><label>Database type:</label></div>
				<div class="form-element">
					<select name="type" required>
						<option value="derby">Derby</option>
						<option value="mysql">MySQL</option>
						<option value="postgresql" selected>PostgreSql</option>
						<option value="sqlserver">Sql Sever</option>
					</select>
				</div>
			</div>
			
			<div>
				<div class="form-element">
					<label>Server</label>
				</div>
				<div class="form-element">
					<input type="text" name="server" value="//localhost:5432"/>
				</div>
			</div>
			
			<div>
				<div class="form-element">
					<label>Is server external:</label>
				</div>
				<div class="form-element">
					<input type="checkbox" name="runExternal" checked="true"/>
				</div>
			</div>
			
			<div>
				<div class="form-element">
					<label>User name:</label>
				</div>
				<div class="form-element">
					<input type="text" name="name" required value="postgres"/>
				</div>
			</div>
			
			<div>
				<div class="form-element">
					<label>Password</label>
				</div>
				<div class="form-element">
					<input type="password" name="psw" value="1234"/>
				</div>
			</div>
			
			<div>
				<div class="form-element">
					<label>Database:</label>
				</div>
				<div class="form-element">
					<input type="text" name="database" value="eda"/>
				</div>
			</div>
			
			<div>
				<input type="submit" value="Login" />
			</div>
		</form>
	</div>
</t:block>