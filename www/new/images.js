var totiImages = {
	/* https://www.iconfinder.com/icons/186407/up_arrow_icon */
	"arrowUp": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xOC4yMjEsNy4yMDZsOS41ODUsOS41ODVjMC44NzksMC44NzksMC44NzksMi4zMTcsMCwzLjE5NWwtMC44LDAuODAxYy0wLjg3NywwLjg3OC0yLjMxNiwwLjg3OC0zLjE5NCwwICBsLTcuMzE1LTcuMzE1bC03LjMxNSw3LjMxNWMtMC44NzgsMC44NzgtMi4zMTcsMC44NzgtMy4xOTQsMGwtMC44LTAuODAxYy0wLjg3OS0wLjg3OC0wLjg3OS0yLjMxNiwwLTMuMTk1bDkuNTg3LTkuNTg1ICBjMC40NzEtMC40NzIsMS4xMDMtMC42ODIsMS43MjMtMC42NDdDMTcuMTE1LDYuNTI0LDE3Ljc0OCw2LjczNCwxOC4yMjEsNy4yMDZ6IiBmaWxsPSIjNTE1MTUxIi8+PC9zdmc+",
	/* https://www.iconfinder.com/icons/186411/down_arrow_icon */
	"arrowDown": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0xNC43NywyMy43OTVMNS4xODUsMTQuMjFjLTAuODc5LTAuODc5LTAuODc5LTIuMzE3LDAtMy4xOTVsMC44LTAuODAxYzAuODc3LTAuODc4LDIuMzE2LTAuODc4LDMuMTk0LDAgIGw3LjMxNSw3LjMxNWw3LjMxNi03LjMxNWMwLjg3OC0wLjg3OCwyLjMxNy0wLjg3OCwzLjE5NCwwbDAuOCwwLjgwMWMwLjg3OSwwLjg3OCwwLjg3OSwyLjMxNiwwLDMuMTk1bC05LjU4Nyw5LjU4NSAgYy0wLjQ3MSwwLjQ3Mi0xLjEwNCwwLjY4Mi0xLjcyMywwLjY0N0MxNS44NzUsMjQuNDc3LDE1LjI0MywyNC4yNjcsMTQuNzcsMjMuNzk1eiIgZmlsbD0iIzUxNTE1MSIvPjwvc3ZnPg==",
	/* https://www.iconfinder.com/icons/186389/delete_remove_icon */
	"cross": "data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiA/PjwhRE9DVFlQRSBzdmcgIFBVQkxJQyAnLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4nICAnaHR0cDovL3d3dy53My5vcmcvR3JhcGhpY3MvU1ZHLzEuMS9EVEQvc3ZnMTEuZHRkJz48c3ZnIGVuYWJsZS1iYWNrZ3JvdW5kPSJuZXcgMCAwIDMyIDMyIiBoZWlnaHQ9IjMycHgiIGlkPSJMYXllcl8xIiB2ZXJzaW9uPSIxLjEiIHZpZXdCb3g9IjAgMCAzMiAzMiIgd2lkdGg9IjMycHgiIHhtbDpzcGFjZT0icHJlc2VydmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiPjxwYXRoIGQ9Ik0yMC4zNzcsMTYuNTE5bDYuNTY3LTYuNTY2YzAuOTYyLTAuOTYzLDAuOTYyLTIuNTM5LDAtMy41MDJsLTAuODc2LTAuODc1Yy0wLjk2My0wLjk2NC0yLjUzOS0wLjk2NC0zLjUwMSwwICBMMTYsMTIuMTQyTDkuNDMzLDUuNTc1Yy0wLjk2Mi0wLjk2My0yLjUzOC0wLjk2My0zLjUwMSwwTDUuMDU2LDYuNDVjLTAuOTYyLDAuOTYzLTAuOTYyLDIuNTM5LDAsMy41MDJsNi41NjYsNi41NjZsLTYuNTY2LDYuNTY3ICBjLTAuOTYyLDAuOTYzLTAuOTYyLDIuNTM4LDAsMy41MDFsMC44NzYsMC44NzZjMC45NjMsMC45NjMsMi41MzksMC45NjMsMy41MDEsMEwxNiwyMC44OTZsNi41NjcsNi41NjYgIGMwLjk2MiwwLjk2MywyLjUzOCwwLjk2MywzLjUwMSwwbDAuODc2LTAuODc2YzAuOTYyLTAuOTYzLDAuOTYyLTIuNTM4LDAtMy41MDFMMjAuMzc3LDE2LjUxOXoiIGZpbGw9IiM1MTUxNTEiLz48L3N2Zz4="
};