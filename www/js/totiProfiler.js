/* TOTI Profiler version 0.0.1 */
var totiProfiler = {
	elementHeight: "40px",
	getProfilerHeader: function(access = true) {
		var pageId = totiUtils.getCookie("PageId");
		if (pageId === null) {
			return {};
		}
		return {
			"PageId": pageId
		};
	},
	print: function() {
		var pageId = "Page_06770915709143117"; // TODO page id from identity

		var div = document.createElement("div");
		div.style.height = totiProfiler.elementHeight;
		div.style.width = "100%";
		div.style.position = "fixed";
		div.style.bottom = "0";

    	div.style["background-color"] = "green";
		// page id -> link

		var link = document.createElement("a");
		link.innerText = pageId;
		link.setAttribute("href", "/profiler.html#" + pageId);

		var interval = null;
		var showData = function() {
			totiLoad.async(
				"/toti/profiler/" + pageId,
				"get",
				{}, 
				{}, 
				function(res) {
					if (res.length === 0) {
						return;
					}
					/* first log is inigial */
					// user, lang
					// -> url, query count

					console.log(res);
				}, 
				function(xhr) {
					if (interval != null) {
						clearInterval(interval);
					}
					console.log(xhr);
				},
				false
			);
		};

		showData();
		interval = setInterval(showData, 500);
    	

    	return div;
	}
}

document.addEventListener("DOMContentLoaded", function(event) {
    var div = document.createElement("div");
    div.style.height = totiProfiler.elementHeight;
  /*  div.style["background-color"] = "red";  */
    document.body.appendChild(div);
 //   document.body.appendChild(totiProfiler.print());
});