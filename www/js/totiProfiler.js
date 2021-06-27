/* TOTI Profiler version 0.0.1 */
var totiProfiler = {
	getProfilerHeader: function(access = true) {
		var pageId = totiUtils.getCookie("PageId");
		if (pageId === null) {
			return {};
		}
		return {
			"PageId": pageId
		};
	},
}