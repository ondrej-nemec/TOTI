Parametrized URL in build
Button.create(
					Link.get()
					.addUrlParam("{id}")
					.addGetParam("name", "{text}")
					.create(getClass(), c->c.syncButtonLink(0, null)),
					"sync"
)


## How to do

### Login in JS

First of all, to all pages add this script:

```
totiAuth.onLoad();
```

**Only for asynchronious log in.**¨

After success login, call this:

```
totiAuth.login(response, {
	logout: {
		url: "url-for-log-out",
		method: "log-out-http-method"
	},
	refresh: {
		url: "url-for-refresh",
		method: "refresh-http-method"
	}
});
// optionaly
location.reload();
```

### Change language
