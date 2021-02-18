<html>
<body></body>
</html>

<link rel="stylesheet" href="/toti.css" />

<script src="/new/images.js" crossorigin="anonymous"></script>
<script src="/new/translations.js" crossorigin="anonymous"></script>
<script src="/new/lang.js" crossorigin="anonymous"></script>
<script src="/new/utils.js" crossorigin="anonymous"></script>
<script src="/new/auth.js" crossorigin="anonymous"></script>
<script src="/new/display.js" crossorigin="anonymous"></script>
<script src="/new/load.js" crossorigin="anonymous"></script>
<script src="/new/settings.js" crossorigin="anonymous"></script>
<script src="/new/storage.js" crossorigin="anonymous"></script>
<script src="/new/control.js" crossorigin="anonymous"></script>
<script src="/new/form.js" crossorigin="anonymous"></script>

<div id="flash">
</div>

<t:control name="testForm" />

<t:control name="testForm2" >
	<h3>Styled form</h3>
	<t:error name="simple" />
	<t:label name="simple" />
	<t:input name="simple" />
	
	<t:error name="xss" />
	<t:label name="xss" />
	<t:input name="xss" />
	
	<t:error name="area" />
	<t:label name="area" />
	<t:input name="area" />
	
	<t:error name="datetimeInput" />
	<t:label name="datetimeInput" />
	<t:input name="datetimeInput" />
	
	<t:error name="radioInput" />
	<t:label name="radioInput" />
	<t:input name="radioInput" />
	
	<t:error name="no-def-no-override" />
	<t:label name="no-def-no-override" />
	<t:input name="no-def-no-override" />
	
	<t:error name="no-def-override" />
	<t:label name="no-def-override" />
	<t:input name="no-def-override" />
	
	<t:error name="def-no-override" />
	<t:label name="def-no-override" />
	<t:input name="def-no-override" />
	
	<t:error name="def-override" />
	<t:label name="def-override" />
	<t:input name="def-override" />
	
	<t:error name="selectInput" />
	<t:label name="selectInput" />
	<t:input name="selectInput" />
	
	<t:error name="back" />
	<t:label name="back" />
	<t:input name="back" />
	
	<t:error name="save" />
	<t:label name="save" />
	<t:input name="save" />
	
	<t:error name="save-back" />
	<t:label name="save-back" />
	<t:input name="save-back" />
</t:control>
	
</body>
</html>
