package samples.examples.responses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ji.common.Logger;
import ji.common.functions.Env;
import ji.common.functions.FileExtension;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.WebSocket;
import ji.translator.Translator;
import toti.HttpServer;
import toti.HttpServerFactory;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.application.Task;
import toti.register.Register;
import toti.response.Response;

/**
 * This example shows various responses and their usage
 * @author Ondřej Němec
 *
 */
@Controller("responses")
public class ResponsesExample implements Module {
	
	private TaskExample task;
	
	public ResponsesExample(TaskExample task) {
		this.task = task;
	}
	
	public ResponsesExample() {};
	
	@Action("index")
	public Response getIndex() {
		return Response.getFile("samples/examples/responses/index.html");
	}

	/**
	 * Returns given file, file can be from any path from disk
	 * Path can be relative or absolute
	 * Path can be from directory tree or classpath
	 * @return http://localhost:8080/examples/responses/file
	 */
	@Action("file")
	public Response getFile() {
	//	String fileName = "samples/plainTextFile.txt"; // Plain text file. Browser probably display instead of downloading.
		String fileName = "samples/binaryFile.odt"; // Binary file. Browser starts downloading
		return Response.getFileDownload(
			fileName,
			"fileToDownload_" + new Date().getTime() + "." + new FileExtension(fileName).getExtension()
		);
		// return Response.getFile(StatusCode.OK, fileName);
	}

	/**
	 * Returns new file generated inside method
	 * @return http://localhost:8080/examples/responses/generate
	 */
	@Action("generate")
	public Response getGenerated() {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			bout.write("Generated".getBytes());
			bout.write("\n".getBytes());
			bout.write("File content :-D".getBytes());
		} catch (IOException e) {
			// never happends
		}
		return Response.getFileDownload(StatusCode.OK, "generatedFileToDownload_" + new Date().getTime() + ".odt", bout.toByteArray());
	}

	/**
	 * Returns data as JSON. See https://ondrej-nemec.github.io/JI/?file=files-json.html for more about Object->JSON
	 * @return http://localhost:8080/examples/responses/json
	 */
	@Action("json")
	public Response getJson() {
		Map<String, Object> json = new MapInit<String, Object>()
			.append("first", "value")
			.append("second", false)
			.toMap();
		
		return Response.getJson(json);
		// return Response.getJson(StatusCode.ACCEPTED, json);
	}

	/**
	 * Returns text as response
	 * @return http://localhost:8080/examples/responses/text
	 */
	@Action("text")
	public Response getText() {
		return Response.getText("Working");
		// return Response.getText(StatusCode.OK, "Working");
	}

	/**
	 * Parse given template with paramters to HTML
	 * @return http://localhost:8080/examples/responses/template
	 */
	@Action("template")
	public Response getTemplate() {
		Map<String, Object> params = new MapInit<String, Object>()
			.append("title", "Page title")
			.append("number", 42)
			.toMap();
		String template = "/template.jsp";
		return Response.getTemplate(template, params);
		// return Response.getTemplate(StatusCode.OK, template, params);
	}

	/**
	 * Redirect to given relative URL.
	 * @return http://localhost:8080/examples/responses/redirect
	 */
	@Action("redirect")
	public Response getRedirect() {
		return Response.getRedirect("/examples/responses/text");
		// return Response.getRedirect(StatusCode.TEMPORARY_REDIRECT, "/examples/responses/text");
	}

	/**
	 * Redirect to given URL. <strong>Open redirection vulnerability</strong>
	 * @return http://localhost:8080/examples/responses/open-redirect
	 */
	@Action("open-redirect")
	public Response getOpenRedirect() {
		return Response.getRedirect("https://github.com/", true);
		// return Response.getRedirect(StatusCode.TEMPORARY_REDIRECT, "https://github.com/", true;
	}

	/**
	 * Create websocket connection
	 * @return http://localhost:8080/examples/responses/websocket
	 */
	@Action("websocket")
	public Response getWebsocket(WebSocket websocket) {
		 // websocket can be null - means this request is not valid websocket request
		if (websocket != null) {
			task.setWebsocket(websocket);
			return Response.getWebsocket(websocket, task.onMessage(), task.onError());
		}
		return Response.getFile("samples/examples/responses/websockets.html");
	}
	

	public static void main(String[] args) {
		List<Module> modules = Arrays.asList(new ResponsesExample());
		try {
			HttpServer server = new HttpServerFactory()
				.setPort(8080)
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
		return "samples/examples/responses";
	}
	
	@Override
	public String getTemplatesPath() {
		return "samples/examples/responses";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Database database, Logger logger)
			throws Exception {
		TaskExample task = new TaskExample(logger);
		register.addFactory(ResponsesExample.class, ()->new ResponsesExample(task));
		return Arrays.asList(task);
	}
}
