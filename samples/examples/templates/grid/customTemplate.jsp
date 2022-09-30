<html>
<head>
	<title>Grid example</title>
<%--	<link rel="stylesheet" href="/toti/totiStyle.css" />
	 <script src="/toti/totiJs.js" nonce="${nonce}"></script> --%>
	<script src="/js/totiImages.js" ></script>
	<script src="/js/totiUtils.js" ></script>
	<script src="/js/totiSortedMap.js" ></script>
	<script src="/js/totiTranslations.js" ></script> 
	<script src="/js/totiStorage.js" ></script>
	<script src="/js/totiLang.js" ></script>
	<script src="/js/totiLoad.js" ></script>
	<script src="/js/totiAuth.js" ></script> 
	<script src="/js/totiDisplay.js" ></script>
	<script src="/js/totiControl.js" ></script>
	
	<script src="/js/totiGridCustomTemplate.js" ></script>
	<script src="/js/totiGridDefaultTemplate.js" ></script>
	<script src="/js/totiGrid.js" ></script>
	
</head>
<body>

	<h1>Grid example</h1>

	<div id="flash"></div>

	
	<div>
		<t:control name="grid">
			<div>
				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_action"><!-- title --></div>
					</div>
					<div><toti checkbox="main"></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_id"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="id" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="id" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="id" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_text"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="text" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="text" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="text" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_number"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="number" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="number" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="number" class="someClass" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_range"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="range" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="range" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="range" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_select_col"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="select_col" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="select_col" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="select_col" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_datetime_col"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="datetime_col" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="datetime_col" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="datetime_col" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_date_col"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="date_col" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="date_col" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="date_col" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_time_col"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="time_col" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="time_col" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="time_col" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_month"><!-- title --></div>
						<div style="color: red; display:inline-block;" totiSort="onSort" name="month" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" totiSort="onSort" name="month" class="down"><strong>D</strong></div>
					</div>
					<div><toti filter="month" /></div>
				</div>

				<div style="display:inline-block;">
					<div>
						<div style="display:inline-block;" id="toti-grid-title_buttons"><!-- title --></div>
					</div>
					<div><!-- filter --></div>
				</div>
			</div>
			<div>
				<!-- rows -->
			</div>
		</t:control>

		<script type="text/javascript">
			function onSort(element, sortOrder) {
				var parent = element.parentNode;
				var imgUp = parent.querySelector('.up');
				var imgDown = parent.querySelector('.down');
				switch(sortOrder) {
					case null:
						imgDown.style.color = "blue";
						imgUp.style.color = "red";
						break;
					case false:
						imgDown.style.color = "blue";
						imgUp.style.color = "grey";
						break;
					case true:
						imgDown.style.color = "grey";
						imgUp.style.color = "red";
						break;
				}
			}
		</script>
	</div>

</body>
</html>