# Permissions

TODO
* @secured
* user factory
* identity
* authenticator vx authorizator

TOTI provide you simple way how secured controller's methods. There are two classes: `Authenticator` (check who the user is) and `Authorizator` (check if user has permissions to enter).

During parsing request, `Authenticator` is looking for authentication token. This token can be stored in Authorization header ("Higher token") or in Cookies ("Lower token"). If token is present, is not expired and is not corrupted, the unique identificator (speficied by you, f.e. username) is separated from token. Then `ThrowingFunction<String, User, Exception> userFactory` (remember, in `Application` or `HttpServerFactory`) is called with your identificator as parameter. 

##### Identity and User

`Identity` is information about connected client. Provide you information about IP address, all request headers, custom content stored in token and `User`. The `User` can be `NULL` if client is not logged. You can use `isPresent` method to check if `User` is present.

If client is logged, `User` created with `userFactory` is stored in `Identity`.

`User` constructor requires id, that can be anything you wish. As the second parameter, `User` needs `Permissions`. In `Permission` interface, there is `getRulesForDomain` method with String parameter. In this method, you can search for user permissions in database, get permission from memory or something else. You have to return `Rules`.

`Rules` is collection of `Rule`. `Rules` (collection) can contains privileged `Rule` (`NULL` means no privileged) and List of same-level `Rule`

##### Log in and out
// TODO + refresh

##### Secure methods
// TODO