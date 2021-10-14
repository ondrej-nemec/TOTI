/* TOTI Profiler version 0.0.3 */
var totiProfiler = {
	pageId: null,
	interval: null,
	data: [],
	getProfilerHeader: function() {
		var pageId = totiProfiler.pageId;
		if (pageId === null) {
			return {};
		}
		return {
			"PageId": pageId
		};
	},
	getImage: function(id, src, onClick, width) {
		var img = document.createElement("img");
		img.setAttribute("src", src);
		img.setAttribute("width", width);
		img.setAttribute("alt", "");
		var container = document.createElement("div");
		container.appendChild(img);
		container.onclick = onClick;
		container.setAttribute("id", id);
		return container;
	},
	getDataItem: function(text, image, imageTitle) {
		var span = document.createElement("span");
		span.innerText = text;
		var image = totiProfiler.getImage("", image, function(){}, 20);
		image.style.display = "inline-block";
		var space = document.createElement("span");
		space.innerHTML = "&nbsp;";

		var td = document.createElement("td");
		td.setAttribute("title", imageTitle);
		td.appendChild(image);
		td.appendChild(space);
		td.appendChild(span);
		return td;
	},
	getDataRow: function(data) {
		var tr = document.createElement("tr");
		tr.appendChild(totiProfiler.getDataItem(
			data.rendering.render + "ms",
			totiImages.time,
			"Server processing time"
		));
		tr.appendChild(totiProfiler.getDataItem(
			data.requestInfo.method + ": " + data.requestInfo.url + " " + Object.entries(data.params).length, 
			totiImages.web, 
			"Request: [methood]: [url] [parametersCount]"
		));
		tr.appendChild(totiProfiler.getDataItem(
			data.trans.locale.lang + " (" + (data.trans.locale.isLeftToRight === true ? "LTR" : "RTL") + ")",
			totiImages.lang, 
			"Localization"
		));
		tr.appendChild(totiProfiler.getDataItem(
			data.identity.isApiAllowed === true ? "API" : "NotApi",
			totiImages.security, 
			"Security Level"
		));
		tr.appendChild(totiProfiler.getDataItem(
			data.identity.hasOwnProperty("user") ? data.identity.user.id : "---", 
			totiImages.user, 
			"User"
		));
		tr.appendChild(totiProfiler.getDataItem(
			Object.entries(data.queries).length, 
			totiImages.database, 
			"Database queries"
		));
		return tr;
	},
	getDetailPanel: function(rows) {
		var table = document.createElement("table");
		rows.forEach(function(row) {
			table.appendChild(totiProfiler.getDataRow(row));
		});
		table.setAttribute("style", "background-color: white; width: 100%;");

		var container = document.createElement("div");
		container.setAttribute("id", "toti-profiler-detail");
		container.setAttribute("style", "background-color: black; padding: 0.25em;");
		container.appendChild(table);
		return container;
	},
	addHeader: function(container, pageId) {
		var closeButton = document.createElement("td");
		closeButton.appendChild(totiProfiler.getImage("toti-profiler-close", totiImages.cross, function() {
			document.getElementById("toti-profiler").remove();
			document.getElementById("toti-profiler-bottom").remove();
			var detail = document.getElementById("toti-profiler-detail");
			if (detail !== null) {
				detail.remove();
			}
			if (totiProfiler.interval != null) {
				clearInterval(totiProfiler.interval);
			}
		}, 30));
		closeButton.style.width = 30;
		closeButton.style["background-color"] = "red";
		
		var titleLink = document.createElement("a");
		titleLink.setAttribute("href", "/toti/profiler#" + pageId);
		titleLink.setAttribute("target", "_blank");
		titleLink.setAttribute("style", "color: black; text-decoration: none;");
		titleLink.innerText = "TOTI Profiler";
		var title = document.createElement("td");
		title.setAttribute("style", "text-align: center; color: white; background-color: #3366ff; width: 7em; margin:auto;");
		title.appendChild(titleLink);

		var showButton = document.createElement("div");
		var hideButton = document.createElement("div");
		hideButton.setAttribute("id", "toti-profiler-hide-button");
		hideButton.style.display = "none";
		showButton.appendChild(totiProfiler.getImage("toti-profiler-show", totiImages.arrowUp, function() {
			totiProfiler.showDetail(showButton.getBoundingClientRect().left);

			showButton.setAttribute("style", "display: none");
			hideButton.setAttribute("style", "");
		}, 15));
		hideButton.appendChild(totiProfiler.getImage("toti-profiler-hide", totiImages.arrowDown,function() {
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
		requestCount.setAttribute("style", "width: 2em;");

		container.appendChild(closeButton);
		container.appendChild(title);
		container.appendChild(requestCount);
		container.appendChild(showHide);
		container.appendChild(document.createElement("td"));
	},
	showDetail: function(left) {
		var detail = document.createElement("div");
		detail.setAttribute("id", "toti-profiler-detail");
		document.body.appendChild(detail);

		detail.style.display = "block";
		detail.style.position = "fixed";
		detail.style.bottom = "32px";
		detail.style.right = "0px";
		detail.style.overflow = "auto";
		detail.style["max-height"] = "50%";
		detail.style.left = left + "px";

		detail.appendChild(totiProfiler.getDetailPanel(totiProfiler.data));
	},
	print: function() {
		var pageId = totiProfiler.pageId;

		var profiler = document.createElement("table");
		profiler.setAttribute("id", "toti-profiler");
		profiler.setAttribute(
			"style",
			 "height: 30px; width: 100%; position: fixed; bottom: 5px; background-color: #f5c6cb; z-index: 1000;"
		);
		totiProfiler.addHeader(profiler, pageId);

		var showData = function() {
			totiLoad.async("/toti/profiler", "post", { id: pageId }, {}, 
				function(res) {
					if (res.length === 0) {
						return;
					}
					totiProfiler.data = res;
					var detail = document.getElementById("toti-profiler-detail");
					if (detail !== null) {
						detail.remove();
						totiProfiler.showDetail(document.getElementById("toti-profiler-hide-button").getBoundingClientRect().left);
					}
					var requestCount = profiler.querySelector("#toti-profiler-requests-count");
					if (requestCount.innerText.length === 0) {
						for (const[a, row] of Object.entries(totiProfiler.getDataRow(res[0]).children)) {
							profiler.appendChild(row);
						}
					}
					requestCount.innerText = res.length;
				}, 
				function(xhr) {
					if (totiProfiler.interval !== null) {
						clearInterval(totiProfiler.interval);
					}
					console.error("Profiler refresh stopped");
				},
				false
			);
		};

		showData();
		totiProfiler.interval = setInterval(showData, 30000);
    	return profiler;
	}
}

document.addEventListener("DOMContentLoaded", function(event) {
	if (totiSettings.showProfiler) {
	    var div = document.createElement("div");
	    div.setAttribute("id", "toti-profiler-bottom");
	    div.style.height = totiProfiler.elementHeight;
	   /* div.style["background-color"] = "red";  */
	    document.body.appendChild(div);
	    document.body.appendChild(totiProfiler.print());
	}
});
totiProfiler.pageId = totiUtils.getCookie("PageId");