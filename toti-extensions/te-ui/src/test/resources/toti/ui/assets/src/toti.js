// TODO bude v main
const Toti = function () {
	var style = document.createElement('style');
	style.innerHTML = `
		/* include toti.css */
	`.replaceAll('\t', '').replaceAll('\n', '').replaceAll('\r', '').replaceAll('  ', '');
	document.head.appendChild(style);

	/* include SortedMap.js */
	/* include utils.js */
	/* include animations.js */
	const conf = {
		configuration: {},
		set: (configuration)=>{
			conf.configuration = configuration;
		},
		get: (key)=>{
			if (conf.configuration.hasOwnProperty(key)) {
				return conf.configuration[key];
			}
			return {};
		}
	};
	const callbacks = {};
	return {
		init(configuration) {
			conf.set(configuration);
		},
		registerCallback: (name, callback)=>{
			callbacks[name] = callback;
		},
		getCallback: (name)=>{
			if (callbacks.hasOwnProperty(name)) {
				return callbacks[name];
			}
			return null;
		},
		utils: totiUtils,
		images: {
			/* https://www.iconfinder.com/icons/186407/up_arrow_icon */
			"arrowUp": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xOC4yMjEsNy4yMDZsOS41ODUsOS41ODVjMC44NzksMC44NzksMC44NzksMi4zMTcsMCwzLjE5NWwtMC44LDAuODAxYy0wLjg3NywwLjg3OC0yLjMxNiwwLjg3OC0zLjE5NCwwICBsLTcuMzE1LTcuMzE1bC03LjMxNSw3LjMxNWMtMC44NzgsMC44NzgtMi4zMTcsMC44NzgtMy4xOTQsMGwtMC44LTAuODAxYy0wLjg3OS0wLjg3OC0wLjg3OS0yLjMxNiwwLTMuMTk1bDkuNTg3LTkuNTg1ICBjMC40NzEtMC40NzIsMS4xMDMtMC42ODIsMS43MjMtMC42NDdDMTcuMTE1LDYuNTI0LDE3Ljc0OCw2LjczNCwxOC4yMjEsNy4yMDZ6IiBmaWxsPSIjNTE1MTUxIi8+PC9zdmc+",
			/* https://www.iconfinder.com/icons/186411/down_arrow_icon */
			"arrowDown": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xNC43NywyMy43OTVMNS4xODUsMTQuMjFjLTAuODc5LTAuODc5LTAuODc5LTIuMzE3LDAtMy4xOTVsMC44LTAuODAxYzAuODc3LTAuODc4LDIuMzE2LTAuODc4LDMuMTk0LDAgIGw3LjMxNSw3LjMxNWw3LjMxNi03LjMxNWMwLjg3OC0wLjg3OCwyLjMxNy0wLjg3OCwzLjE5NCwwbDAuOCwwLjgwMWMwLjg3OSwwLjg3OCwwLjg3OSwyLjMxNiwwLDMuMTk1bC05LjU4Nyw5LjU4NSAgYy0wLjQ3MSwwLjQ3Mi0xLjEwNCwwLjY4Mi0xLjcyMywwLjY0N0MxNS44NzUsMjQuNDc3LDE1LjI0MywyNC4yNjcsMTQuNzcsMjMuNzk1eiIgZmlsbD0iIzUxNTE1MSIvPjwvc3ZnPg==",
			/* https://www.iconfinder.com/icons/186409/arrow_right_next_icon */
			"arrowRight": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0yNC4yOTEsMTQuMjc2TDE0LjcwNSw0LjY5Yy0wLjg3OC0wLjg3OC0yLjMxNy0wLjg3OC0zLjE5NSwwbC0wLjgsMC44Yy0wLjg3OCwwLjg3Ny0wLjg3OCwyLjMxNiwwLDMuMTk0ICBMMTguMDI0LDE2bC03LjMxNSw3LjMxNWMtMC44NzgsMC44NzgtMC44NzgsMi4zMTcsMCwzLjE5NGwwLjgsMC44YzAuODc4LDAuODc5LDIuMzE3LDAuODc5LDMuMTk1LDBsOS41ODYtOS41ODcgIGMwLjQ3Mi0wLjQ3MSwwLjY4Mi0xLjEwMywwLjY0Ny0xLjcyM0MyNC45NzMsMTUuMzgsMjQuNzYzLDE0Ljc0OCwyNC4yOTEsMTQuMjc2eiIgZmlsbD0iIzUxNTE1MSIvPjwvc3ZnPg==",
			/* https://www.iconfinder.com/icons/186410/arrow_left_previous_icon */
			"arrowLeft": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik03LjcwMSwxNC4yNzZsOS41ODYtOS41ODVjMC44NzktMC44NzgsMi4zMTctMC44NzgsMy4xOTUsMGwwLjgwMSwwLjhjMC44NzgsMC44NzcsMC44NzgsMi4zMTYsMCwzLjE5NCAgTDEzLjk2OCwxNmw3LjMxNSw3LjMxNWMwLjg3OCwwLjg3OCwwLjg3OCwyLjMxNywwLDMuMTk0bC0wLjgwMSwwLjhjLTAuODc4LDAuODc5LTIuMzE2LDAuODc5LTMuMTk1LDBsLTkuNTg2LTkuNTg3ICBDNy4yMjksMTcuMjUyLDcuMDIsMTYuNjIsNy4wNTQsMTZDNy4wMiwxNS4zOCw3LjIyOSwxNC43NDgsNy43MDEsMTQuMjc2eiIgZmlsbD0iIzUxNTE1MSIvPjwvc3ZnPg==",
		},
		animations: animations(conf.get('inputs')),
		createInput(attributes) {
			/* include inputs.js */
			return createInput(attributes, conf.get('inputs'));
		},
		Form: (conf)=>{
			/* include form.js */
			return createForm(conf);
		},
		Grid: (conf)=>{
			/* include grid.js */
			return createGrid(conf)
		},
		load(...configurations) {
			/* include load.js */
			return load(configurations);
		}
	};
}();