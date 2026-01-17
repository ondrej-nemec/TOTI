package toti.samples.application.controllers;

import toti.application.annotations.Action;
import toti.application.annotations.Controller;
import toti.application.answers.action.ResponseAction;
import toti.application.answers.response.Response;
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
		return (request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET");
		};
	}

	/**
	 * Request with URI parameter
	 * @return http://localhost:8080/application/request/basic/{parameter}
	 */
	@Action(path="basic")
	public ResponseAction get(String parameter) {
		return (request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET " + parameter);
		};
	}

	/**
	 * Request with different HTTP method
	 * @return http://localhost:8080/application/request/post
	 */
	@Action(path="post", methods=HttpMethod.POST)
	public ResponseAction post() {
		return (request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: POST");
		};
	}

	/**
	 * Serving more HTTP methods
	 * @return http://localhost:8080/application/request/get-or-post
	 */
	@Action(path="get-or-post", methods= {HttpMethod.POST, HttpMethod.GET})
	public ResponseAction getOrPost() {
		return (request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET or POST " + request.getMethod());
		};
	}
	
	/**
	 * Parsing query parameters
	 * @return http://localhost:8080/application/request/query
	 */
	@Action(path="query")
	public ResponseAction query() {
		return (request, translator, identity)->{
			return Response.create(StatusCode.OK).getJson(request.getQueryParams().toMap());
		};
	}
	
	/**
	 * Accepting only selected body type
	 * @return http://localhost:8080/application/request/body-type
	 */
	// TODO set body type json
	@Action(path="body-type")
	public ResponseAction bodyType() {
		return (request, translator, identity)->{
			return Response.create(StatusCode.OK).getJson(request.getBodyParams());
		};
	}
	
	/**
	 * Request contains file
	 * <strong>Required parameter name: <i>file</></strong>
	 * @return http://localhost:8080/application/request/file
	 */
	@Action(path="file")
	public ResponseAction file() {
		return (request, translator, identity)->{
			UploadedFile file = request.getBodyParams().getUploadedFile("file");
			return Response.create(StatusCode.OK).getText("Uploaded file: " + file.toString());
		};
	}

}
