<html>
<head>
	<title></title>
<script src="https://code.jquery.com/jquery-3.5.1.min.js" integrity="sha256-9/aliU8dGd2tb6OSsuzixeV4y/faTqgFtohetphbbj0=" crossorigin="anonymous"></script>
<script src="/grid/control.js" crossorigin="anonymous"></script>
</head>
<body>
<nav>
	<ul>
		<a href="/control/list"><li>List</li></a>
		<a href="/security/index"><li>Login</li></a>
	</ul>

</nav>

<div id="logout"></div>
<script>
	totiAuth.onLoad();
	if (totiAuth.getToken() !== null) {
		var button = totiControl.inputs.button(function() {
			totiAuth.logout();
			location.reload();
		}, "Logout");
		$('#logout').html(button);
	}
</script>

<div id="lang"></div>
<script>
	var select = totiControl.inputs.select(
		[
			totiControl.inputs.option("en_gb", "English"),
			totiControl.inputs.option("cs_cz", "Czech")
		]
	);
	select.val(totiLang.getLang());
	select.change(function() {
		totiLang.changeLanguage($(this).val());
		location.reload();
	});
	$('#lang').html(select);
</script>


		<t:include block="content" />
	
	
	
	
<t:if cond="${pdf} != null">
	<div id="pdf">
	</div>
	
	<button id="cmd">Generate PDF</button>
	<div id="editor">
<!-- 
	<script src="/grid/polyfills.js" crossorigin="anonymous"></script>
	<script src="/grid/jspdf.js" crossorigin="anonymous"></script>
	
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.1.1/jspdf.umd.min.js"></script>
-->
		<script src="https://code.jquery.com/jquery-1.12.3.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/0.9.0rc1/jspdf.min.js"></script>
		<script> 
			var doc = new jsPDF();
			var specialElementHandlers = {
			    '#editor': function (element, renderer) {
			        return true;
			    },
			    'script': function (element, renderer) {
			        return true;
			    },
			    '#pdf-ignored': function (element, renderer) {
			        return true;
			    }
			};
	
			$('#cmd').click(function () {
				console.log($('#pdf').html());
			    doc.fromHTML($('#pdf').html(), 15, 15, {
			        'width': 170,
			            'elementHandlers': specialElementHandlers
			    });
			    doc.save('sample-file.pdf');
			});
			/*
			const doc = new jsPDF();
			var source = window.document.getElementsByTagName("body")[0]
			doc.fromHTML(source, 15, 15, {});
			doc.output("dataurlnewwindow");
			//doc.save("a4.pdf");
			*/
		</script>
	</div>
</t:if>
</body>
</html>