/* TOTI Auth version 2.1.1 */
var totiAuth = {
	variableToken: "authenticationToken",
    variableConfig: "authenticationConfig",
    getToken: function() {
        return totiStorage.getVariable(totiAuth.variableToken);
    },
    logout: function(url = null, method = "post", then = null) {
    	var onLogout = function() {
	        totiStorage.removeVariable(totiAuth.variableConfig);
	        totiStorage.removeVariable(totiAuth.variableToken);
	        if (then !== null) {
				then();
			}
    	};
        totiAuth.isRefreshActive = false;
    	if (url === null) {
    		onLogout();
    	} else {
    		totiLoad.load(url, method)
    		.then(onLogout)
    		.catch(function(err) {
    			/* TODO display error */
    			console.error('Augh, there was an error!', err);
    		});
    	}
    },
    login: function(token, config = null) {
        totiStorage.saveVariable(totiAuth.variableToken, token);
        totiAuth.setRefreshConfig(config);
    },
    isRefreshActive: false, /* internal */
    refresh: function(url, method, expiredIn) {
        totiAuth.isRefreshActive = true;
    	totiLoad.load(url, method)
        .then((response)=>{
            totiAuth.customRefreshHandler(response);
            return response;
        })
        .then(function(response) {
            return totiUtils.sleep(expiredIn * 2 / 3);
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
        var expiredIn = token.expires_in - new Date().getTime();
        totiAuth.refresh(config.url, config.method, expiredIn);
        return true;
    },
    customRefreshHandler: function () {},
    setRefreshConfig: function(config) {
         if (config !== null) {
            totiStorage.saveVariable(totiAuth.variableConfig, config);
            totiAuth.startTokenRefresh();
        }
     }
};
totiAuth.startTokenRefresh();