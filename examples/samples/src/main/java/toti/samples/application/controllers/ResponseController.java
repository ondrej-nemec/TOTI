package toti.samples.application.controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import ji.common.functions.FileExtension;
import ji.common.structures.MapInit;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.WebSocket;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;
import toti.answers.router.Link;
import toti.samples.application.TaskExample;

@Controller("response")
public class ResponseController {
	
	private final TaskExample task;
	private final Link link;
	
	public ResponseController(TaskExample task, Link link) {
		this.task = task;
		this.link = link;
	}

	/**
	 * Returns given file, file can be from any path from disk
	 * Path can be relative or absolute
	 * Path can be from directory tree or classpath
	 * @return http://localhost:8080/appication/response/file
	 */
	@Action(path="file")
	public ResponseAction getFile() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			//	String fileName = "samples/plainTextFile.txt"; // Plain text file. Browser probably display instead of downloading.
			String fileName = "toti/samples/binaryFile.odt"; // Binary file. Browser starts downloading
			return Response.OK().getFileDownload(
				fileName,
				"fileToDownload_" + new Date().getTime() + "." + new FileExtension(fileName).getExtension()
			);
		});
	}

	/**
	 * Returns new file generated inside method
	 * @return http://localhost:8080/appication/response/generate
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
	 * @return http://localhost:8080/appication/response/json
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
	 * @return http://localhost:8080/appication/response/text
	 */
	@Action(path="text")
	public ResponseAction getText() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.OK().getText("Working");
		});
	}

	/**
	 * Parse given template with paramters to HTML
	 * @return http://localhost:8080/appication/response/template
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
	 * @return http://localhost:8080/appication/response/redirect
	 */
	@Action(path="redirect")
	public ResponseAction getRedirect() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.create(StatusCode.TEMPORARY_REDIRECT).getRedirect(link.create(getClass(), c->c.getText()));
		});
	}

	/**
	 * Redirect to given URL. <strong>Open redirection vulnerability</strong>
	 * @return http://localhost:8080/appication/response/open-redirect
	 */
	@Action(path="open-redirect")
	public ResponseAction getOpenRedirect() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			return Response.create(StatusCode.TEMPORARY_REDIRECT).getRedirect("https://github.com/", true);
		});
	}

	/**
	 * Create websocket connection
	 * @return http://localhost:8080/appication/response/websocket
	 */
	@Action(path="websocket")
	public ResponseAction getWebsocket(WebSocket websocket) {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			 // websocket can be null - means this request is not valid websocket request
			if (websocket != null) {
				task.setWebsocket(websocket);
				return Response.getWebsocket(websocket, task.onMessage(), task.onError(), (x)->task.removeWebsocket());
			}
			return Response.OK().getFile("templates/application/response/websockets.html");
		});
	}
}
