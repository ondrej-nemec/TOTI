/* TOTI Auth version 2.0.0 */
var totiAuth = {
	variableToken: "authenticationToken",
    variableConfig: "authenticationConfig",
    getToken: function() {
        return totiStorage.getVariable(totiAuth.variableToken);
    },
    logout: function(url = null, method = "post") {
    	var onLogout = function() {
	        totiStorage.removeVariable(totiAuth.variableConfig);
	        totiStorage.removeVariable(totiAuth.variableToken);
    	};
    	if (url === null) {
    		onLogout();
    	} else {
    		totiLoad.load(url, method)
    		.then(onLogout)
    		.catch(function() {
    			/* TODO display error */
    			console.error('Augh, there was an error!', err);
    		});
    	}
    },
    login: function(token, config = null) {
        totiStorage.saveVariable(totiAuth.variableToken, token);
        if (config !== null) {
            totiStorage.saveVariable(totiAuth.variableConfig, config);
            totiAuth.startTokenRefresh();
        }
    },
    isRefreshActive: false, /* internal */
    refresh: function(url, method, expiredIn) {
        totiAuth.isRefreshActive = true;
    	totiLoad.load(url, method)
        .then(totiAuth.customRefreshHandler)
        .then(totiUtils.sleep(expiredIn * 2 / 3))
        .then(function(response) {
        	totiAuth.isRefreshActive = false;
        	return response;
        })
        .then(function() {
        	if (totiAuth.isRefreshActive) {
        		totiAuth.refresh(url, method, expiredIn);
        	}
        })
        .catch(function() {
            totiAuth.isRefreshActive = false;
            totiAuth.logout();
            location.reload(); 
       	});
    },
    startTokenRefresh: function() {
        if (totiAuth.isRefreshActive) {
             console.log("Another refresh is running");
             return false;
        }
        var config = totiStorage.getVariable(totiAuth.variableConfig);
        var token = totiStorage.getVariable(totiAuth.variableToken);
        if (!config) {
             console.log("No saved config");
             return false;
        }
        totiAuth.refresh(config.url, config.method, token.expires_in);
        return true;
    },
    customRefreshHandler: function () {}
};
totiAuth.startTokenRefresh();