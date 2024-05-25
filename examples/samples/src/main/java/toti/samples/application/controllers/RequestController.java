package toti.samples.application.controllers;

import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.BodyType;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;
import toti.http.http.HttpMethod;
import toti.http.http.StatusCode;
import toti.http.http.structures.UploadedFile;

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
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET");
		});
	}

	/**
	 * Request with URI parameter
	 * @return http://localhost:8080/application/request/basic/{parameter}
	 */
	@Action(path="basic")
	public ResponseAction get(String parameter) {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET " + parameter);
		});
	}

	/**
	 * Request with different HTTP method
	 * @return http://localhost:8080/application/request/post
	 */
	@Action(path="post", methods=HttpMethod.POST)
	public ResponseAction post() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: POST");
		});
	}

	/**
	 * Serving more HTTP methods
	 * @return http://localhost:8080/application/request/get-or-post
	 */
	@Action(path="get-or-post", methods= {HttpMethod.POST, HttpMethod.GET})
	public ResponseAction getOrPost() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working: GET or POST " + request.getMethod());
		});
	}
	
	/**
	 * Parsing query parameters
	 * @return http://localhost:8080/application/request/query
	 */
	@Action(path="query")
	public ResponseAction query() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getJson(request.getQueryParams().toMap());
		});
	}
	
	/**
	 * Accepting only selected body type
	 * @return http://localhost:8080/application/request/body-type
	 */
	@Action(path="body-type")
	public ResponseAction bodyType() {
		return ResponseBuilder.get(BodyType.JSON)
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getJson(request.getBodyParams());
		});
	}
	
	/**
	 * Request contains file
	 * <strong>Required parameter name: <i>file</></strong>
	 * @return http://localhost:8080/application/request/file
	 */
	@Action(path="file")
	public ResponseAction file() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			UploadedFile file = request.getBodyParams().getUploadedFile("file");
			return Response.create(StatusCode.OK).getText("Uploaded file: " + file.toString());
		});
	}

}
