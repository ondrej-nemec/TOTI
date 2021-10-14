/* TOTI Auth version 0.0.4 */
var totiAuth = {
	variableName: "authentication",
	getAuthHeader: function(access = true) {
		var token = totiStorage.getVariable(totiAuth.variableName);
		if (token === null) {
			return {};
		}
		return {
			"Authorization": token.token_type + " " + (access ? token.access_token : token.refresh_token)
		};
	},
	/* for public use */
	getToken: function() {
		var token = totiStorage.getVariable(totiAuth.variableName);
		if (token !== null) {
			delete token.config;
		}
		return token;
	},
	isRefreshActive: false,
	setTokenRefresh: function(token = null, period = -1) {
		if (totiAuth.isRefreshActive) {
			console.log("Another refresh is running");
			return false;
		}
		token = token || totiStorage.getVariable(totiAuth.variableName);
		if (!token) {
			console.log("No saved token");
			return false;
		}
		totiAuth.isRefreshActive = true;
		totiStorage.saveVariable(totiAuth.variableName, token);
		if (period < 0) {
			period = token.expires_in * 2 / 3;
		}
		var profiler = (typeof totiProfiler === 'undefined' ? {} : totiProfiler.getProfilerHeader());
		setTimeout(function() {
			totiLoad.async(
				token.config.refresh.url,
				token.config.refresh.method, 
				{}, 
				{
					...totiAuth.getAuthHeader(false),
					...profiler
				}, 
				function(gettedToken) {
					totiAuth.isRefreshActive = false;
					gettedToken.config = token.config;
					totiAuth.setTokenRefresh(gettedToken, -1);
				}, 
				function(xhr) {
					totiAuth.isRefreshActive = false;
					/*if (period < 5000) {*/
						totiStorage.removeVariable(totiAuth.variableName);
					/*} else {
						totiAuth.setTokenRefresh(token, period / 2);
					}	*/
					location.reload();			
				}
			);
		}, period);
		return true;
	},
	logout: function() {
		if (totiStorage.getVariable(totiAuth.variableName) === null) {
			console.log("No token");
			return;
		}
		var token = totiStorage.getVariable(totiAuth.variableName);
		totiLoad.async(
			token.config.logout.url,
			token.config.logout.method, 
			{}, 
			{
				...totiAuth.getAuthHeader(),
				...totiProfiler.getProfilerHeader()
			}, 
			function(res) {}, 
			function(xhr, a, error) {
				console.log(xhr, a, error);
			}
		);
		totiStorage.removeVariable(totiAuth.variableName);
	},
	login: function(token, config) {
		token.config = config;
		totiAuth.setTokenRefresh(token);
	},
	onLoad: function() {
		totiAuth.setTokenRefresh(null, 0);
	}
};
