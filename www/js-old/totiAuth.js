/* TOTI Auth version 1.0.3 */
var totiAuth = {
     variableToken: "authenticationToken",
     variableConfig: "authenticationConfig",
     onSuccessRefresh: function() {},
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
     logout: function(url = null, method = "post") {
         if (url !== null) {
              totiLoad.async(
                  url,
                  method,
                  {},
                  totiLoad.getHeaders(),
                  function(res) {},
                  function(xhr, a, error) {
                       console.log(xhr, a, error);
                  },
                  false
              );
         }
        totiStorage.removeVariable(totiAuth.variableConfig);
        totiStorage.removeVariable(totiAuth.variableToken);
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
         var token = totiStorage.getVariable(totiAuth.variableToken);
         if (!config) {
              console.log("No saved config");
              return false;
         }
         totiAuth.isRefreshActive = true;
         totiLoad.async(
              config.url,
              config.method,
              {},
              totiLoad.getHeaders(),
              function(response) {
                  totiAuth.isRefreshActive = false;
                  if (config.hasOwnProperty("onSuccess")) {
                       window[config.onSuccess](response);
                  }
                  setTimeout(function() {
                      totiAuth.setTokenRefresh();
                  }, token.expires_in * 2 / 3);
              },
              function(xhr) {
                  totiAuth.isRefreshActive = false;
                  totiAuth.logout();
                  location.reload();        
              }
         );
         return true;
     }
};
totiAuth.setTokenRefresh();