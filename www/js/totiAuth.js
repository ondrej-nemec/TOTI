/* TOTI Auth version 1.0.0 */
var totiAuth = {
	variableToken: "authentication",
	variableConfig: "authentication",
	getAuthHeader: function() {
		var token = totiStorage.getVariable(totiAuth.variableToken);
		if (token === null) {
			return {};
		}
		return {
			"Authorization": "bearer " + token.access_token
		};
	},
	/* for public use */
	getToken: function() {
		return totiStorage.getVariable(totiAuth.variableToken);
	},
	logout: function(url, method) {
		totiStorage.removeVariable(totiAuth.variableConfig);
		totiStorage.removeVariable(totiAuth.variableToken);
		totiLoad.async(
			url,
			method, 
			{}, 
			totiLoad.getHeaders(), 
			function(res) {}, 
			function(xhr, a, error) {
				console.log(xhr, a, error);
			}
		);
	},
	login: function(token, config = null) {
		totiStorage.saveVariable(totiAuth.variableToken, token);
		if (config !== null) {
			totiStorage.saveVariable(totiAuth.variableConfig, config);
			totiAuth.setTokenRefresh();
		}
	},
	isRefreshActive: false,
	setTokenRefresh: function() {
		if (totiAuth.isRefreshActive) {
			console.log("Another refresh is running");
			return false;
		}
		var config = totiStorage.getVariable(totiAuth.variableConfig);
		if (!config) {
			console.log("No saved config");
			return false;
		}
		totiAuth.isRefreshActive = true;
		setTimeout(function() {
			totiLoad.async(
				config.url,
				config.method, 
				{}, 
				totiLoad.getHeaders(), 
				function() {
					totiAuth.isRefreshActive = false;
					totiAuth.setTokenRefresh();
				}, 
				function(xhr) {
					totiAuth.isRefreshActive = false;
					totiAuth.logout();
					location.reload();			
				}
			);
		}, token.expires_in * 2 / 3);
		return true;
	}
};
totiAuth.setTokenRefresh();
