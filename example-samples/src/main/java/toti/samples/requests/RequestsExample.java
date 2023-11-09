package toti.samples.requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.structures.UploadedFile;
import ji.translator.Translator;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.application.Module;
import toti.application.Task;
import toti.application.register.Register;

/**
 * This controller shows various ways of receiving parameters.
 * <strong>Validators parameters are showed in another example.</strong>
 * @author Ondřej Němec
 *
 */
@Controller("requests")
public class RequestsExample implements Module  {
	
	/**
	 * Receive primitive paramters or "primitive" parameters like String, Integer,..
	 * @return 
	 * // all <br>
	 * http://localhost:8080/examples-requests/requests/primitives?someText=aaaa&someNumber=12&anotherNumber=21&bool=true <br>
	 * // some can be null <br>
	 * http://localhost:8080/examples-requests/requests/primitives?someText=aaaa&someNumber=12 <br>
	 * // others are not accepted <br>
	 * http://localhost:8080/examples-requests/requests/primitives?someText=aaaa&someNumber=12&anotherNumber=21&bool=true&myNumber=12.3
	 */
	@Action(path="primitives")
	// TODO maybe move query params to path params
	public ResponseAction primitives() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate("/response.jsp", new MapInit<String, Object>(
					"params",
					new MapInit<>()
					.append("some text", req.getBodyParam("someText").getString())
					.append("some number", req.getBodyParam("someNumber").getInteger())
					.append("another number", req.getBodyParam("anotherNumber").getInteger())
					.append("boolean", req.getBodyParam("bool").getBoolean())
					.toMap()
				).toMap());
		});
	}
	
	/**
	 * Receive URL list
	 * @return http://localhost:8080/examples-requests/requests/list?names[]=smith.john&names[]=doe.jane&names[]=my.name
	 */
	@Action(path="list")
	public ResponseAction list() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate(
				"/response.jsp",
				new MapInit<String, Object>("params", req.getBodyParam("names").getList()).toMap()
			);
		});
	}
	
	/**
	 * Receive URL map
	 * @return http://localhost:8080/examples-requests/requests/map?rate[0]=273.15&rate[26.85]=300&rate[100]=373.15
	 */
	@Action(path="map")
	public ResponseAction map() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate(
				"/response.jsp",
				new MapInit<String, Object>("params", req.getBodyParam("rate").getMap()).toMap()
			);
		});
	}

	/**
	 * Receive URL map in list
	 * <strong>THIS IS NOT WORKING CORRECTLY</strong>
	 * @return http://localhost:8080/examples-requests/requests/mapInList?list[][name]=smith.john&list[][name]=doe.jane&list[][name]=my.name
	 */
	/*@Action(path="mapInList")
	public ResponseAction mapInList(@Param("list") List<Object> mapInList) {
		return Response.OK().getTemplate("/response.jsp", new MapInit<String, Object>("params", mapInList).toMap());
	}*/
	
	/**
	 * Receive URL list in map
	 * @return http://localhost:8080/examples-requests/requests/listInMap?map[list][]=273.15&map[list][]=300&map[100]=373.15
	 */
	@Action(path="listInMap")
	public ResponseAction listInMap() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate(
				"/response.jsp",
				new MapInit<String, Object>("params", req.getBodyParam("map").getMap()).toMap()
			);
		});
	}
	
	
	/**
	 * Receive parameter in URL
	 * @return http://localhost:8080/examples-requests/requests/url/42
	 */
	@Action(path="url")
	public ResponseAction url(Integer id) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate("/response.jsp", new MapInit<String, Object>("params", id).toMap());
		});
	}
	
	/**
	 * Show form for file uploading
	 * @return http://localhost:8080/examples-requests/requests/file
	 */
	@Action(path="file", methods=HttpMethod.GET)
	public ResponseAction fileIndex() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate("fileForm.jsp", new HashMap<>());
		});
	}
	
	/**
	 * Upload file
	 * @return
	 */
	@Action(path="file", methods=HttpMethod.POST)
	public ResponseAction fileUpload() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			UploadedFile file = req.getBodyParams().getUploadedFile("fileToUpload");
			// file.save(path); // save file on given path
			return Response.OK().getTemplate("/response.jsp", new MapInit<String, Object>(
				"params",
				new MapInit<>()
				.append("type", file.getContentType())
				.append("file name", file.getFileName())
				.toMap()
			).toMap());
		});
	}
	
	/**
	 * Accept all parameters, available in RequestParameters
	 * @return
	 *  http://localhost:8080/examples-requests/requests/all?age=42&name=my_name&list[]=a&list[]=b&map[a]=aa&map[b]=bb
	 */
	@Action(path="all")
	public ResponseAction allParams() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate(
				"/response.jsp",
				new MapInit<String, Object>("params", req.getBodyParams()).toMap()
			);
		});
	}
	
	/**
	 * Parameters parsed to RequestEntity
	 * @return
	 *  http://localhost:8080/examples-requests/requests/entity?age=42&name=my_name&list[]=a&list[]=b&map[a]=aa&map[b]=bb
	 */
	@Action(path="entity")
	public ResponseAction entity() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate(
				"/response.jsp",
				new MapInit<String, Object>("params", req.getBodyParams().parse(RequestEntity.class)).toMap()
			);
		});
	}
	
	/**
	 * RequestParameters, Entity, URL parameters and GET/POST parameters can be together
	 * @return http://localhost:8080/examples-requests/requests/combined/my_name?age=42&list[]=a&list[]=b&map[a]=aa&map[b]=bb
	 */
	@Action(path="combined")
	public ResponseAction combined(String name) {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate("/response.jsp", new MapInit<String, Object>(
				"params",
				new MapInit<>()
				.append("name and age", name + " " + req.getBodyParam("age").getInteger())
				.append("list", req.getBodyParam("list").getList())
				.append("parameters", req.getBodyParams())
				.append("entity", req.getBodyParams().parse(RequestEntity.class))
				.toMap()
			).toMap());
		});
	}
	
	/**
	 * Request body contains binary data or plain text data (xml, json, ...)
	 * Request header must contains content-type header
	 * Do this request via Postman or similar program
	 * @return
	 *  http://localhost:8080/examples-requests/requests/body
	 */
	@Action(path="body")
	public ResponseAction byteBody() {
		return ResponseBuilder.get().createRequest((req, translator, identity)->{
			return Response.OK().getTemplate(
				"/response.jsp",
				new MapInit<String, Object>("params", new String(req.getBody())).toMap()
			);
		});
	}

	@Override
	public String getName() {
		return "examples-requests";
	}
	
	@Override
	public String getTemplatesPath() {
		return "examples/samples/templates/requests";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		register.addFactory(RequestsExample.class, ()->new RequestsExample());
		return Arrays.asList();
	}

}
