package toti.examples.demo.modules.core;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.response.Response;
import toti.lib.tcpip.enums.HttpMethod;
import toti.lib.tcpip.enums.StatusCode;
import toti.lib.tcpip.structures.UploadedFile;

/**
 * This example shows how different type of request are served
 * @author Ondřej Němec
 *
 */
@Controller("request")
public class RequestController {

	/**
	 * Request on URI only
	 * @return http://localhost:8080/application/request/basic
	 */
	@Action(path="basic")
	public ResponseAction get() {
		return (request, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET");
		};
	}

	/**
	 * Request with URI parameter
	 * @return http://localhost:8080/application/request/basic/{parameter}
	 */
	@Action(path="basic")
	public ResponseAction get(String parameter) {
		return (request, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET " + parameter);
		};
	}

	/**
	 * Request with different HTTP method
	 * @return http://localhost:8080/application/request/post
	 */
	@Action(path="post", methods=HttpMethod.POST)
	public ResponseAction post() {
		return (request, identity)->{
			return Response.create(StatusCode.OK).getText("Working: POST");
		};
	}

	/**
	 * Serving more HTTP methods
	 * @return http://localhost:8080/application/request/get-or-post
	 */
	@Action(path="get-or-post", methods= {HttpMethod.POST, HttpMethod.GET})
	public ResponseAction getOrPost() {
		return (request, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET or POST " + request.getMethod());
		};
	}
	
	/**
	 * Parsing query parameters
	 * @return http://localhost:8080/application/request/query
	 */
	@Action(path="query")
	public ResponseAction query() {
		return (request, identity)->{
			return Response.create(StatusCode.OK).getJson(request.getQueryParams().toMap());
		};
	}
	
	/**
	 * Accepting only selected body type
	 * @return http://localhost:8080/application/request/body
	 */
	@Action(path="body")
	public ResponseAction bodyType() {
		return (request, identity)->{
			return Response.create(StatusCode.OK)
			.addHeader("content-type", "text/html")
			.getText(
				"Binary body: " + (request.getBody() == null ? "NULL" : new String(request.getBody()))
				+ "<br>" +
				"Parsed: " + request.getBodyParams()
			);
		};
	}
	
	/**
	 * Request contains file
	 * <strong>Required parameter name: <i>file</></strong>
	 * @return http://localhost:8080/application/request/file
	 */
	@Action(path="file")
	public ResponseAction file() {
		return (request, identity)->{
			UploadedFile file = request.getBodyParams().getUploadedFile("file");
			return Response.create(StatusCode.OK).getText("Uploaded file: " + file.toString());
		};
	}

}
