<html>
<head>
	<title>Grid example</title>
	<script src="/toti/totiJs.js" nonce="${nonce}"></script>
	
</head>
<body>

	<h1>Grid example</h1>

	<div id="flash"></div>

	
	<div>
		<t:control name="grid" toti-selected-row-color="lightblue">
			<h4 toti-grid="caption"></h4>
			<div>
				<div style="display:inline-block; width: 2%"></div>
				
				<div style="display:inline-block; width: 2.5%">
					<div>
						<div style="display:inline-block;" toti-grid-title="main"><!-- title --></div>
					</div>
					<div><t:grid checkbox="main"></div>
				</div>

				<div style="display:inline-block; width: 3.5%">
					<div>
						<div style="display:inline-block;" toti-grid-title="id"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="id" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="id" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="id" /></div>
				</div>

				<div style="display:inline-block; width: 7%">
					<div>
						<div style="display:inline-block;" toti-grid-title="text"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="text" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="text" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="text" /></div>
				</div>

				<div style="display:inline-block; width: 7%">
					<div>
						<div style="display:inline-block;" toti-grid-title="number"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="number" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="number" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="number" class="someClass" /></div>
				</div>

				<div style="display:inline-block; width: 16%">
					<div>
						<div style="display:inline-block;" toti-grid-title="range"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="range" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="range" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="range" /></div>
				</div>

				<div style="display:inline-block; width: 5%">
					<div>
						<div style="display:inline-block;" toti-grid-title="select_col"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="select_col" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="select_col" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="select_col" /></div>
				</div>

				<div style="display:inline-block; width: 12%">
					<div>
						<div style="display:inline-block;" toti-grid-title="datetime_col"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="datetime_col" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="datetime_col" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="datetime_col" /></div>
				</div>

				<div style="display:inline-block; width: 9%">
					<div>
						<div style="display:inline-block;" toti-grid-title="date_col"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="date_col" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="date_col" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="date_col" /></div>
				</div>

				<div style="display:inline-block; width: 9%">
					<div>
						<div style="display:inline-block;" toti-grid-title="time_col"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="time_col" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="time_col" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="time_col" /></div>
				</div>

				<div style="display:inline-block; width: 11%">
					<div>
						<div style="display:inline-block;" toti-grid-title="month"><!-- title --></div>
						<div style="color: red; display:inline-block;" toti-grid-sort="onSort" name="month" class="up"><strong>U</strong></div>
						<div style="color: blue; display:inline-block;" toti-grid-sort="onSort" name="month" class="down"><strong>D</strong></div>
					</div>
					<div><t:grid filter="month" /></div>
				</div>

				<div style="display:inline-block; width: 14.3%">
					<div>
						<div style="display:inline-block;" toti-grid-title="buttons"><!-- title --></div>
					</div>
					<div><t:grid buttons="buttons"></div>
				</div>
			</div>
			<div toti-grid="rows">
				<!-- rows -->
				<template>
					<div style="width: 100%">
						<span toti-grid-cell="id_parent" style="display: inline-block; width: 2%" toti-tree="moveChild">
							<div style="color: red; display:inline-block;" toti-tree="expanded">
								<strong>V</strong>
							</div>
							<div style="color: green; display:inline-block;" toti-tree="collapsed">
								<strong>&gt;</strong>
							</div>
							<div style="color: orange; display:inline-block;" toti-tree="child">
								<strong>=</strong>
							</div>
						</span>
						
						<span toti-grid-cell="main" style="display: inline-block; width: 2.5%">
							<t:grid checkbox="main">
						</span>
						<span toti-grid-cell="id" style="display: inline-block; width: 3.5%"></span>
						<span toti-grid-cell="text" style="display: inline-block; width: 7%"></span>
						<span toti-grid-cell="number" style="display: inline-block; width: 7%"></span>
						<span toti-grid-cell="range" style="display: inline-block; width: 16%"></span>
						<span toti-grid-cell="select_col" style="display: inline-block; width: 5%"></span>
						<span toti-grid-cell="datetime_cl" style="display: inline-block; width: 12%"></span>
						<span toti-grid-cell="date_col" style="display: inline-block; width: 9%"></span>
						<span toti-grid-cell="time_col" style="display: inline-block; width: 9%"></span>
						<span toti-grid-cell="month" style="display: inline-block; width: 11%"></span>
						<span toti-grid-cell="buttons" style="display: inline-block; width: 14.3%"></span>
					</div>
				</template>
			</div>
			<div>
				<div toti-grid="page-buttons" style="width: 30%; display: inline-block;">
					<template>
						<button toti-grid-page-button="title"></button>
					</template>
				</div>
				
				<div style="width: 10%; display: inline-block;">
					<select toti-grid="page-size">
						<option value="10">10</option>
						<option value="20">20</option>
						<option value="30">30</option>
						<option value="50">50</option>
					</select>
				</div>
			</div>
		</t:control>

		<script type="text/javascript">
			function moveChild(cell, row, level) {
				row.style['margin-left'] = (level * 0.9) + "em";
			}
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