<html>
<head>
	<title>TODO</title>
	<script src="/toti/totiJs.js"></script>
	<script>totiSettings.showProfiler = false;</script>
	<style>
		.button {
			color: #fff;
		    background-color: #6c757d;
		    border-color: #6c757d;
		    display: inline-block;
		    border-radius: 0.25rem;
		    border: 1px solid transparent;
    		padding: 0.375rem 0.75rem;
		}
		.button:hover {
			color: #fff;
		    background-color: #5c636a;
		    border-color: #565e64;
		}
		.number-length {
			width: 5em;
		}
	</style>
</head>
<body>

	<h2>Class to generate</h2>

	<t:control name="control">
		<fieldset>
			<legend>
				<t:input name="api[use]" class="legend"/>
				<t:label name="api[use]" />
			</legend>
			
			<t:input name="api[get-all]"/>
			<t:label name="api[get-all]" />
			
			<t:input name="api[get]" />
			<t:label name="api[get]" />
			
			<t:input name="api[update]" />
			<t:label name="api[update]" />
			
			<t:input name="api[insert]"/>
			<t:label name="api[insert]" />
			
			<t:input name="api[delete]"/>
			<t:label name="api[delete]" />
		</fieldset>
	
		<fieldset>
			<legend>
				<t:input name="page[use]" class="legend"/>
				<t:label name="page[use]" />
			</legend>
			
			<t:input name="page[grid]"/>
			<t:label name="page[grid]" />
			
			<t:input name="page[detail]" />
			<t:label name="page[detail]" />
			
			<t:input name="page[create-form]" />
			<t:label name="page[create-form]" />
			
			<t:input name="page[edit-form]"/>
			<t:label name="page[edit-form]" />
		</fieldset>
	
		<fieldset>
			<legend>Others</legend>
			<t:input name="others[jsp]"/>
			<t:label name="others[jsp]" />
			
			<t:input name="others[migration]" />
			<t:label name="others[migration]" />
			
			<t:input name="others[dao]" />
			<t:label name="others[dao]" />
			
			<t:input name="others[entity]"/>
			<t:label name="others[entity]" />
			
			<t:input name="others[translation]"/>
			<t:label name="others[translation]" />
		</fieldset>
	
		<h2>Basic</h2>
	
		<t:label name="name" />
		<t:input name="name"/>
	
		<div>
			<t:label name="secured" /> <t:input name="secured" class="checkbox-control" control="secured">
	
			<t:label name="domain" />
			<t:input name="domain" class="secured"/>
		</div>	
	
		<h2>Inputs</h2>
		
		<table name="input-container">
			<tr>
				<th>Input name</th>
				<th>Type</th>
				<th>Max length</th>
				<th>Min length</th>
				<th>Max value</th>
				<th>Min value</th>
				<th><span name="add" class="button">Add</span></th>
			</tr>
			<template name="pattern">
				<tr>
					<td><t:input name="%p[%s][name]" /></td>
					<td><t:input name="%p[%s][type]" /></td>
					<td>
						<t:input name="%p[%s][max-length]" class="number-length"/>
					</td>
					<td>
						<t:input name="%p[%s][min-length]" class="number-length" />
					</td>
					<td>
						<t:input name="%p[%s][max-value]" class="number-length" />
					</td>
					<td>
						<t:input name="%p[%s][min-value]" class="number-length" />
					</td>
					<td><span name="remove" class="button">Remove</span></td>
				</tr>
			</template>
		</table>
	
		<t:input name="submit"/>
	</t:control>

<script type="text/javascript">
	function initControl() {
		document.querySelectorAll('.legend').forEach(function(element) {
			var set = function () {
				if (element.checked) {
					/*element.parentElement.parentElement.parentElement.removeAttribute("disabled");*/
					element.closest('fieldset').removeAttribute("disabled");
				} else {
					/*element.parentElement.parentElement.parentElement.setAttribute("disabled", true);*/
					element.closest('fieldset').setAttribute("disabled", true);
				}
			};
			set();
			element.onchange = set;
		});
		document.querySelectorAll('.checkbox-control').forEach(function(checkbox) {
			var set = function() {
				document.querySelectorAll('.' + checkbox.getAttribute("control")).forEach(function(element) {
					if (checkbox.checked) {
						element.removeAttribute("disabled");
					} else {
						element.setAttribute("disabled", true);
					}
				});
			};
			set();
			checkbox.onchange = set;
		});
	}
	function onFailure(response, button, form) {
		console.log(response, button, form);
	}
	function onSuccess(response, button, form) {
		console.log(response, button, form);
		if (response.hasOwnProperty("api")) {
			
		}
		if (response.hasOwnProperty("page")) {
			
		}
		if (response.hasOwnProperty("jsp")) {
			
		}
		if (response.hasOwnProperty("migration")) {
			
		}
		if (response.hasOwnProperty("dao")) {
			
		}
		if (response.hasOwnProperty("entity")) {
			
		}
		if (response.hasOwnProperty("translation")) {
			
		}
	}
</script>

</body>
</html>