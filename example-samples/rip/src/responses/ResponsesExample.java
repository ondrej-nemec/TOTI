package toti.samples.responses;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import ji.common.functions.Env;
import ji.common.functions.FileExtension;
import ji.common.structures.MapInit;
import ji.database.Database;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.WebSocket;
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
	
	@Action(path="index")
	@Deprecated // no more required
	public ResponseAction getIndex() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getFile("samples/examples/responses/index.html");
		});
	}

	/**
	 * Returns given file, file can be from any path from disk
	 * Path can be relative or absolute
	 * Path can be from directory tree or classpath
	 * @return http://localhost:8080/examples-responses/responses/file
	 */
	@Action(path="file")
	public ResponseAction getFile() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			//	String fileName = "samples/plainTextFile.txt"; // Plain text file. Browser probably display instead of downloading.
			String fileName = "samples/binaryFile.odt"; // Binary file. Browser starts downloading
			return Response.OK().getFileDownload(
				fileName,
				"fileToDownload_" + new Date().getTime() + "." + new FileExtension(fileName).getExtension()
			);
		});
	}

	/**
	 * Returns new file generated inside method
	 * @return http://localhost:8080/examples-responses/responses/generate
	 */
	@Action(path="generate")
	public ResponseAction getGenerated() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				bout.write("Generated".getBytes());
				bout.write("\n".getBytes());
				bout.write("File content :-D".getBytes());
			} catch (IOException e) {
				// never happends
			}
			return Response.OK().getFileDownload("generatedFileToDownload_" + new Date().getTime() + ".odt", bout.toByteArray());
		});
	}

	/**
	 * Returns data as JSON. See https://ondrej-nemec.github.io/JI/?file=files-json.html for more about Object->JSON
	 * @return http://localhost:8080/examples-responses/responses/json
	 */
	@Action(path="json")
	public ResponseAction getJson() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			Map<String, Object> json = new MapInit<String, Object>()
					.append("first", "value")
					.append("second", false)
					.toMap();
				
				return Response.OK().getJson(json);
		});
	}

	/**
	 * Returns text as response
	 * @return http://localhost:8080/examples-responses/responses/text
	 */
	@Action(path="text")
	public ResponseAction getText() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getText("Working");
		});
	}

	/**
	 * Parse given template with paramters to HTML
	 * @return http://localhost:8080/examples-responses/responses/template
	 */
	@Action(path="template")
	public ResponseAction getTemplate() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			Map<String, Object> params = new MapInit<String, Object>()
					.append("title", "Page title")
					.append("number", 42)
					.toMap();
				String template = "/template.jsp";
				return Response.OK().getTemplate(template, params);
		});
	}

	/**
	 * Redirect to given relative URL.
	 * @return http://localhost:8080/examples-responses/responses/redirect
	 */
	@Action(path="redirect")
	public ResponseAction getRedirect() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.create(StatusCode.TEMPORARY_REDIRECT).getRedirect("/examples/responses/text");
		});
	}

	/**
	 * Redirect to given URL. <strong>Open redirection vulnerability</strong>
	 * @return http://localhost:8080/examples-responses/responses/open-redirect
	 */
	@Action(path="open-redirect")
	public ResponseAction getOpenRedirect() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.create(StatusCode.TEMPORARY_REDIRECT).getRedirect("https://github.com/", true);
		});
	}

	/**
	 * Create websocket connection
	 * @return http://localhost:8080/examples-responses/responses/websocket
	 */
	@Action(path="websocket")
	public ResponseAction getWebsocket(WebSocket websocket) {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			 // websocket can be null - means this request is not valid websocket request
			if (websocket != null) {
				task.setWebsocket(websocket);
				return Response.getWebsocket(websocket, task.onMessage(), task.onError(), (x)->task.removeWebsocket());
			}
			return Response.OK().getFile("samples/examples/responses/websockets.html");
		});
	}

	@Override
	public String getName() {
		return "examples-responses";
	}
	
	@Override
	public String getTemplatesPath() {
		return "templates/responses";
	}

	@Override
	public List<Task> initInstances(Env env, Translator translator, Register register, Link link, Database database, Logger logger)
			throws Exception {
		TaskExample task = new TaskExample(logger);
		register.addController(ResponsesExample.class, ()->new ResponsesExample(task));
		return Arrays.asList(task);
	}
}
