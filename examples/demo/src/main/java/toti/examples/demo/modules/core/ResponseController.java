package toti.examples.demo.modules.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import toti.core.annotations.Action;
import toti.core.annotations.Controller;
import toti.core.answers.action.ResponseAction;
import toti.core.answers.action.WebSocket;
import toti.core.answers.response.Response;
import toti.core.answers.router.Link;
import toti.lib.common.structures.MapInit;
import toti.lib.files.access.FileUtils;
import toti.lib.tcpip.enums.StatusCode;

@Controller("response")
public class ResponseController {
	
	private final WebsocketTask task;
	private final Link link;
	
	public ResponseController(WebsocketTask task, Link link) {
		this.task = task;
		this.link = link;
	}

	/**
	 * Returns given file, file can be from any path from disk
	 * Path can be relative or absolute
	 * Path can be from directory tree or classpath
	 * @return http://localhost:8080/application/response/file
	 */
	@Action(path="file-plain")
	public ResponseAction getFilePlain() {
		return (req, identity)->{
			String fileName = "files/core/responses/plainTextFile.txt"; // Plain text file. Browser probably display instead of downloading.
			return Response.OK().getFileDownload(
				fileName,
				"fileToDownload_" + new Date().getTime() + "." + FileUtils.parseName(fileName).extension(),
				false
			);
		};
	}

	/**
	 * Returns given file, file can be from any path from disk
	 * Path can be relative or absolute
	 * Path can be from directory tree or classpath
	 * @return http://localhost:8080/application/response/file
	 */
	@Action(path="file-binary")
	public ResponseAction getFileBinary() {
		return (req, identity)->{
			String fileName = "files/core/responses/binaryFile.odt"; // Binary file. Browser starts downloading
			return Response.OK().getFileDownload(
				fileName,
				"fileToDownload_" + new Date().getTime() + "." + FileUtils.parseName(fileName).extension(),
				true
			);
		};
	}

	/**
	 * Returns new file generated inside method
	 * @return http://localhost:8080/application/response/generate
	 */
	@Action(path="generate")
	public ResponseAction getGenerated() {
		return (req, identity)->{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				bout.write("Generated".getBytes());
				bout.write("\n".getBytes());
				bout.write("File content :-D".getBytes());
			} catch (IOException e) {
				// never happends
			}
			return Response.OK().getFileDownload(
				"generatedFileToDownload_" + new Date().getTime() + ".odt",
				bout.toByteArray(),
				true
			);
		};
	}

	/**
	 * Returns data as JSON. See https://ondrej-nemec.github.io/JI/?file=files-json.html for more about Object->JSON
	 * @return http://localhost:8080/application/response/json
	 */
	@Action(path="json")
	public ResponseAction getJson() {
		return (req, identity)->{
			Map<String, Object> json = new MapInit<String, Object>()
					.append("first", "value")
					.append("second", false)
					.toMap();
				
				return Response.OK().getJson(json);
		};
	}

	/**
	 * Returns text as response
	 * @return http://localhost:8080/application/response/text
	 */
	@Action(path="text")
	public ResponseAction getText() {
		return (req, identity)->{
			return Response.OK().getText("Working");
		};
	}

	/**
	 * Parse given template with paramters to HTML
	 * @return http://localhost:8080/application/response/template
	 */
	@Action(path="template")
	public ResponseAction getTemplate() {
		return (req, identity)->{
			Map<String, Object> params = new MapInit<String, Object>()
					.append("title", "Page title")
					.append("number", 42)
					.toMap();
				String template = "/template.jsp";
				return Response.OK().getTemplate(template, params);
		};
	}

	/**
	 * Redirect to given relative URL.
	 * @return http://localhost:8080/application/response/redirect
	 */
	@Action(path="redirect")
	public ResponseAction getRedirect() {
		return (req, identity)->{
			return Response.create(StatusCode.TEMPORARY_REDIRECT).getRedirect(link.create(getClass(), c->c.getText()));
		};
	}

	/**
	 * Redirect to given URL. <strong>Open redirection vulnerability</strong>
	 * @return http://localhost:8080/application/response/open-redirect
	 */
	@Action(path="open-redirect")
	public ResponseAction getOpenRedirect() {
		return (req, identity)->{
			return Response.create(StatusCode.TEMPORARY_REDIRECT).getRedirect("https://github.com/", true);
		};
	}

	/**
	 * Create websocket connection
	 * @return http://localhost:8080/application/response/websocket
	 */
	@Action(path="websocket")
	public ResponseAction getWebsocket() {
		return (req, identity)->{
			try {
				// websocket can be empty - means this request is not valid websocket request
				if (req.getWebsocket().isPresent()) {
					WebSocket webSocket = req.getWebsocket().get();
					webSocket.accept(()->{
						// TODO on open
					}, task.onMessage(), task.onError(), (x)->task.removeWebsocket());
					task.setWebsocket(webSocket);
					return Response.getWebsocket(webSocket);
				}
				return Response.OK().getFile("files/core/responses/websocket.html");
			} catch (Exception e) {
				// return Response.INTERNAL_SERVER_ERROR().getEmpty();
				throw new RuntimeException(e);
			}
		};
	}
}
