package samples.examples.requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.socketCommunication.http.structures.UploadedFile;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.annotations.Method;
import toti.annotations.Param;
import toti.annotations.ParamUrl;
import toti.annotations.Params;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;

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
	 * http://localhost:8080/examples/requests/primitives?someText=aaaa&someNumber=12&anotherNumber=21&bool=true <br>
	 * // some can be null <br>
	 * http://localhost:8080/examples/requests/primitives?someText=aaaa&someNumber=12 <br>
	 * // others are not accepted <br>
	 * http://localhost:8080/examples/requests/primitives?someText=aaaa&someNumber=12&anotherNumber=21&bool=true&myNumber=12.3
	 */
	@Action("primitives")
	public Response primitives(
			@Param("someText") String text, 
			@Param("someNumber") int number1, // cannot be null
			@Param("anotherNumber") Integer number2, // can be null
			@Param("bool") Boolean bool
			) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>(
			"params",
			new MapInit<>()
			.append("some text", text)
			.append("some number", number1)
			.append("another number", number2)
			.append("boolean", bool)
			.toMap()
		).toMap());
	}
	
	/**
	 * Receive URL list
	 * @return http://localhost:8080/examples/requests/list?names[]=smith.john&names[]=doe.jane&names[]=my.name
	 */
	@Action("list")
	public Response list(@Param("names") List<String> names) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>("params", names).toMap());
	}
	
	/**
	 * Receive URL map
	 * @return http://localhost:8080/examples/requests/map?rate[0]=273.15&rate[26.85]=300&rate[100]=373.15
	 */
	@Action("map")
	public Response map(@Param("rate") Map<String, String> exchangeRateList) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>("params", exchangeRateList).toMap());
	}

	/**
	 * Receive URL map in list
	 * <strong>THIS IS NOT WORKING CORRECTLY</strong>
	 * @return http://localhost:8080/examples/requests/mapInList?list[][name]=smith.john&list[][name]=doe.jane&list[][name]=my.name
	 */
	@Action("mapInList")
	public Response mapInList(@Param("list") List<Object> mapInList) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>("params", mapInList).toMap());
	}
	
	/**
	 * Receive URL list in map
	 * @return http://localhost:8080/examples/requests/listInMap?map[list][]=273.15&map[list][]=300&map[100]=373.15
	 */
	@Action("listInMap")
	public Response listInMap(@Param("map") Map<String, Object> listInMap) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>("params", listInMap).toMap());
	}
	
	
	/**
	 * Receive parameter in URL
	 * @return http://localhost:8080/examples/requests/url/42
	 */
	@Action("url")
	public Response url(@ParamUrl("id") Integer id) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>("params", id).toMap());
	}
	
	/**
	 * Show form for file uploading
	 * @return http://localhost:8080/examples/requests/file
	 */
	@Action("file")
	@Method({HttpMethod.GET})
	public Response fileIndex() {
		return Response.getTemplate("fileForm.jsp", new HashMap<>());
	}
	
	/**
	 * Upload file
	 * @return
	 */
	@Action("file")
	@Method({HttpMethod.POST})
	public Response fileUpload(@Param("fileToUpload") UploadedFile file) {
		// file.save(path); // save file on given path
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>(
			"params",
			new MapInit<>()
			.append("type", file.getContentType())
			.append("file name", file.getFileName())
			.toMap()
		).toMap());
	}
	
	/**
	 * Accept all parameters, available in RequestParameters
	 * @return
	 *  http://localhost:8080/examples/requests/all?age=42&name=my_name&list[]=a&list[]=b&map[a]=aa&map[b]=bb
	 */
	@Action("all")
	public Response allParams(@Params RequestParameters parameters) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>("params", parameters).toMap());
	}
	
	/**
	 * Parameters parsed to RequestEntity
	 * @return
	 *  http://localhost:8080/examples/requests/entity?age=42&name=my_name&list[]=a&list[]=b&map[a]=aa&map[b]=bb
	 */
	@Action("entity")
	public Response entity(@Params RequestEntity entity) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>("params", entity).toMap());
	}
	
	/**
	 * RequestParameters, Entity, URL parameters and GET/POST parameters can be together
	 * @return http://localhost:8080/examples/requests/combined/my_name?age=42&list[]=a&list[]=b&map[a]=aa&map[b]=bb
	 */
	@Action("combined")
	public Response combined(
			@ParamUrl("name") String name, 
			@Param("age") Integer age, 
			@Param("list") List<Object> list,
			@Params RequestParameters parameters,
			@Params RequestEntity entity) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>(
				"params",
				new MapInit<>()
				.append("name and age", name + " " + age)
				.append("list", list)
				.append("parameters", parameters)
				.append("entity", entity)
				.toMap()
			).toMap());
	}
	
	/**
	 * Request body contains binary data or plain text data (xml, json, ...)
	 * Request header must contains content-type header
	 * Do this request via Postman or similar program
	 * @return
	 *  http://localhost:8080/examples/requests/body
	 */
	@Action("body")
	public Response byteBody(byte[] body) {
		return Response.getTemplate("/response.jsp", new MapInit<String, Object>("params", new String(body)).toMap());
	}
	

	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new RequestsExample());
		try {
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
				.setMaxRequestBodySize(1024 * 10) // 10 kB - !! for all body
				.get(modules, null, null);
			
			/* start */
			server.start();
	
			// sleep for 2min before automatic close
			try { Thread.sleep(2 * 60 * 1000); } catch (InterruptedException e) { e.printStackTrace(); }
			
			/* stop */
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	@Override
	public String getName() {
		return "examples";
	}

	@Override
	public String getControllersPath() {
		return "samples/examples/requests";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/requests";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		register.addFactory(RequestsExample.class, ()->new RequestsExample());
		return Arrays.asList();
	}

}
