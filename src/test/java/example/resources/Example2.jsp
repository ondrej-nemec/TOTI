<t:layout path="layout.jsp" />
<t:block name="content">
	<h1>${title}</h1>
	<div> <a href="/example-module/example/add"><t:trans message="messages.add-button" /></a> </div>
	<t:control name="exampleControl" >
		<t:input name="id" />
		<hr>
		<t:error name="name" />
		<t:label name="name" />
		<t:input name="name" />
		<hr>
		<t:error name="email" />
		<t:label name="email" />
		<t:input name="email" />
		<hr>
		<t:error name="age" />
		<t:label name="age" />
		<t:input name="age" />
		<hr>
		<t:error name="pasw" />
		<t:label name="pasw" />
		<t:input name="pasw" />
		<hr>
		<t:error name="range" />
		<t:label name="range" />
		<t:input name="range" />
		<hr>
		<t:error name="active" />
		<t:label name="active" />
		<t:input name="active" />
		<hr>
		<t:error name="defvalue" />
		<t:label name="defvalue" />
		<t:input name="defvalue" />
		<hr>
		<t:error name="sex" />
		<t:label name="sex" />
		<t:input name="sex" />
		<hr>
		<t:error name="parent" />
		<t:label name="parent" />
		<t:input name="parent" />
		<hr>
		<t:error name="select1" />
		<t:label name="select1" />
		<t:input name="select1" />
		<hr>
		<t:error name="simple_date" />
		<t:label name="simple_date" />
		<t:input name="simple_date" />
		<hr>
		<t:error name="dt_local" />
		<t:label name="dt_local" />
		<t:input name="dt_local" />
		<hr>
		<t:error name="month" />
		<t:label name="month" />
		<t:input name="month" />
		<hr>
		<t:error name="time" />
		<t:label name="time" />
		<t:input name="time" />
		<hr>
		<t:error name="week" />
		<t:label name="week" />
		<t:input name="week" />
		<hr>
		<t:error name="favorite_color" />
		<t:label name="favorite_color" />
		<t:input name="favorite_color" />
		<hr>
			<t:error name="map[subText1]" />
			<t:label name="map[subText1]" />
			<t:input name="map[subText1]" />
			<br>
			<t:error name="map[subText2]" />
			<t:label name="map[subText2]" />
			<t:input name="map[subText2]" />
		<hr>
			<t:error name="list[]" />
			<t:label name="list[]" />
			<t:input name="list[]" />
			<br>
			<t:error name="list[]" />
			<t:label name="list[]" />
			<t:input name="list[]" />
			<br>
			<t:error name="list[]" />
			<t:label name="list[]" />
			<t:input name="list[]" />
		<hr>
		<fieldset name="pairs-container">
			<template name="pattern">
				<t:error name="pairs[%s][first-in-pair]" />
				<t:label name="pairs[%s][first-in-pair]" />
				<t:input name="pairs[%s][first-in-pair]" /> <br>
				
				<t:error name="pairs[%s][second-in-pair]" />
				<t:label name="pairs[%s][second-in-pair]" />
				<t:input name="pairs[%s][second-in-pair]" /> <br>
				
				<span name="remove">Remove</span>
			</template>
			<span name="add">Add</span>
		</fieldset>
		
		<fieldset name="main-container">
			<template name="pattern">
				<t:error name="main[%s][text1]" />
				<t:label name="main[%s][text1]" />
				<t:input name="main[%s][text1]" /> <br>
				
				<t:error name="main[%s][text2]" />
				<t:label name="main[%s][text2]" />
				<t:input name="main[%s][text2]" /> <br>
				
			</template>
			
		</fieldset>
		<hr>
		<t:input name="reset" />
		<hr>
		<t:input name="save" />
		<hr>
		<t:input name="save-back" />
		<hr>
		<t:input name="back" />
		<hr>
	
	</t:control>
	
	<script>
		function afterBind(values) {
			console.log(values);
		}
		function beforeBind(values) {
			console.log(values);
		}
	</script>
</t:block>