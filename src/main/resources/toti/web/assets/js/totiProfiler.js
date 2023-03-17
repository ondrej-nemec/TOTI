/* TOTI profiler version 2.0.0 */
class Profiler {

	constructor() {
		this.pageId = totiUtils.getCookie("PageId");
		this.data = [];
		this.loadFunction = null;
	}

	getPageId() {
		return this.pageId;
	}

	render() {
		var object = this;
		/* empty block on end of the page*/
		var div = document.createElement("div");
		div.setAttribute("id", "toti-profiler-bottom");
		div.style.height = "30px";
		document.body.appendChild(div);
		/* profiler itself */
		var profiler = document.createElement("table");
		profiler.setAttribute("id", "toti-profiler");
		profiler.setAttribute(
			"style",
			 "height: 30px; width: 100%; position: fixed; bottom: 5px; background-color: #7FC6CC; z-index: 1000; margin: 0;"
			 /*#f5c6cb*/
		);

		var closeButton = document.createElement("td");
		closeButton.appendChild(this.getImage("toti-profiler-close", totiImages.cross, function() {
			document.getElementById("toti-profiler").remove();
			document.getElementById("toti-profiler-bottom").remove();
			var detail = document.getElementById("toti-profiler-detail");
			if (detail !== null) {
				detail.remove();
			}
			object.unregister();
		}, 20));
		closeButton.style.width = 20;
		closeButton.style["background-color"] = "red";
		/*closeButton.width = "2em";*/
		
		var titleLink = document.createElement("a");
		titleLink.setAttribute("href", "/toti/profiler?page=" + this.pageId);
		titleLink.setAttribute("target", "_blank");
		titleLink.setAttribute("style", "color: #e3ffff; text-decoration: none;");
		titleLink.innerText = "TOTI Profiler";
		titleLink.setAttribute('title', "See full info");
		var title = document.createElement("td");
		title.setAttribute("style", "text-align: center; color: #e3ffff; background-color: #017CA5; width: 7em; margin:auto;");
		/* #3366ff */
		title.appendChild(titleLink);

		var showButton = document.createElement("div");
		var hideButton = document.createElement("div");
		hideButton.setAttribute("id", "toti-profiler-hide-button");
		hideButton.style.display = "none";
		showButton.appendChild(this.getImage("toti-profiler-show", totiImages.arrowUp, function() {
			showButton.setAttribute("style", "display: none");
			hideButton.setAttribute("style", "");

			var detailDiv = document.createElement("div");
			detailDiv.setAttribute("id", "toti-profiler-detail");

			var mainRow = profiler.querySelector('#mainRow');
			var bottom = document.body.getBoundingClientRect().bottom - profiler.getBoundingClientRect().top;

			detailDiv.style.width = mainRow.offsetWidth + "px";
			detailDiv.style.left = (mainRow.getBoundingClientRect().left - 10) + "px";
			detailDiv.style.padding = "5px";
			detailDiv.style['max-height'] = "250px";
			detailDiv.style.position = "fixed";
			detailDiv.style['background-color'] = '#017CA5';
			detailDiv.style.overflow = "auto";

			detailDiv.style.bottom = bottom + "px";

			document.body.appendChild(detailDiv);

			object.data.forEach((item, index)=>{
				if (index === 0) {
					return;
				}
				if (detailDiv.querySelector('#row-' + index) !== null) {
					return;
				}
				var container = document.createElement('div');
				container.style['background-color'] = '#7FC6CC';
				container.style.padding = "5px";
				container.setAttribute('id', "row-" + index);
				object.addRow(container, item);
				detailDiv.appendChild(container);
			});
		}, 15));
		hideButton.appendChild(this.getImage("toti-profiler-hide", totiImages.arrowDown,function() {
			showButton.setAttribute("style", "");
			hideButton.setAttribute("style", "display: none");
			document.getElementById("toti-profiler-detail").remove();
		}, 15));
		var showHide = document.createElement("td");
		showHide.setAttribute("style", "width: 1em");
		showHide.appendChild(showButton);
		showHide.appendChild(hideButton);

		var requestCount = document.createElement("td");
		requestCount.setAttribute("id", "toti-profiler-requests-count");
		requestCount.setAttribute("style", "width: 1em");

		profiler.appendChild(closeButton);
		profiler.appendChild(title);
		profiler.appendChild(showHide);
		profiler.appendChild(requestCount);

		var mainTd = document.createElement("td");
		mainTd.setAttribute('id', 'mainRow');
		profiler.appendChild(mainTd);

		document.body.appendChild(profiler);
	}

	getImage(id, src, onClick, width) {
		var img = document.createElement("img");
		img.setAttribute("src", src);
		img.setAttribute("width", width);
		img.setAttribute("alt", "");
		var container = document.createElement("div");
		container.appendChild(img);
		container.onclick = onClick;
		container.setAttribute("id", id);
		return container;
	}

	load() {
		var object = this;
		totiLoad.anonymous(
			"/toti/profiler",
			"post",
			{}, {},
			{ pageId: this.pageId }
		).then((res)=>{
			document.querySelector('#toti-profiler #toti-profiler-requests-count').innerText = res.length;
			var row = document.querySelector('#toti-profiler #mainRow');
			if (row.innerHTML.length === 0 && row !== null) {
				object.addRow(row, res[0]);
			}
			object.data = res;
		}).catch((xhr)=>{
			var row = document.querySelector('#toti-profiler #mainRow');
			var container = document.createElement('div');
			row.appendChild(container);

			container.innerText = xhr.responseText;
			container.style['background-color'] = "red";
			container.style.margin = "auto";
			container.style['max-width'] = "10em";
			container.style['text-align'] = "center";
		});
	}

	addRow(container, row) {
		container.appendChild(this.createMessage(
			row.request.method
			+ " "
			+ row.controller.module
			+ ":"
			+ row.controller.class
			+ ":"
			+ row.controller.method,
			totiImages.web,
			"Controller"
		));
		if (row.template !== null) {
			container.appendChild(this.createMessage(
				row.template.file,
				totiImages.template,
				"Template"
			));
		}
		var langContainer = this.createMessage(
			row.request.locale.lang,
			totiImages.lang,
			"Selected language"
		);
		if (row.translations.missingTranslations.length > 0) {
			var lang = document.createElement('span');
			lang.innerText = row.translations.missingTranslations.length;
			lang.setAttribute('style', 'background-color: red; padding: 0.25em; margin-right: 0.25em');
			lang.setAttribute('title', 'Missing translations');
			langContainer.appendChild(lang);
		}
		container.appendChild(langContainer);
		container.appendChild(this.createMessage(
			row.request.processTime + " ms",
			totiImages.time,
			"Process time"
		));
		container.appendChild(this.createMessage(
			row.queries.length,
			totiImages.database,
			"Database queries count"
		));
		if (row.hasOwnProperty("user")) {
			container.appendChild(this.createMessage(
				row.user.id,
				totiImages.user,
				"User"
			));
		}
	}

	createMessage(message, image, title) {
		var container = document.createElement('span');
		if (image !== null) {
			var img = document.createElement("img");
			img.setAttribute("src", image);
			img.setAttribute("width", 15);
			img.setAttribute("alt", "");
			img.setAttribute("style", "margin-right: 0.25em");
			container.appendChild(img);
		}
		var span = document.createElement('span');
		span.innerText = message;
		span.setAttribute("style", "margin-right: 0.25em");
		container.appendChild(span);
		container.setAttribute('title', title);
		return container;
	}

	register() {
		this.loadFunction = totiLoad.load;
		var object = this;
		totiLoad.load = function(url, method, headers = {}, urlData = {}, bodyData = {}) {
			return object.loadFunction(url, method, {
				...headers,
				PageId: object.pageId
			}, urlData, bodyData).then((res)=>{
				object.load();
				return res;
			});
		};
	}

	unregister() {
		totiLoad.load = this.loadFunction;
	}

}

var showTotiProfiler = true;
document.addEventListener("DOMContentLoaded", function(event) {
	if (showTotiProfiler) {
		var profiler = new Profiler();
		profiler.render();
		profiler.register();
		profiler.load();
	}
});