package toti.samples.application;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

import ji.common.functions.FileExtension;
import ji.common.structures.MapInit;
import ji.socketCommunication.http.StatusCode;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.response.Response;
import toti.samples.responses.TaskExample;

@Controller("response")
public class ResponseController {
	
	private final TaskExample task;
	
	public ResponseController(TaskExample task) {
		this.task = task;
	}

	// TODO
	// posilani headers
	
	// websocket
	// xml
	// redirect - in app
	// redirect - out of app


	/**
	 * Returns response without body
	 * @return http://localhost:8080/application/response/empty
	 */
	@Action(path="empty")
	public ResponseAction emptyResponse() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getEmpty();
		});
	}

	/**
	 * Returns text as response
	 * @return http://localhost:8080/application/response/text
	 */
	@Action(path="text")
	public ResponseAction textResponse() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			return Response.create(StatusCode.OK).getText("Working text response");
		});
	}

	/**
	 * Returns given file, file can be from any path from disk
	 * Path can be relative or absolute
	 * Path can be from directory tree or classpath
	 * @return http://localhost:8080/examples-responses/responses/file
	 */
	@Action(path="file")
	public ResponseAction fileReponse() {
		return ResponseBuilder.get()
		.createResponse((request, translator, identity)->{
			String fileName = "toti/samples/application/plainTextFile.txt"; // Plain text file. Browser probably display instead of downloading.
			// String fileName = "toti/samples/application/binaryFile.odt"; // Binary file. Browser starts downloading
			return Response.create(StatusCode.OK).getFile(fileName);
		});
	}

	/**
	 * Returns given file, file can be from any path from disk
	 * Path can be relative or absolute
	 * Path can be from directory tree or classpath
	 * @return http://localhost:8080/examples-responses/responses/file-download
	 */
	@Action(path="file-download")
	public ResponseAction downloadFile() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			// String fileName = "toti/samples/application/plainTextFile.txt"; // Plain text file. Browser probably display instead of downloading.
			String fileName = "toti/samples/application/binaryFile.odt"; // Binary file. Browser starts downloading
			return Response.create(StatusCode.OK).getFileDownload(
				fileName,
				"fileToDownload_" + new Date().getTime() + "." + new FileExtension(fileName).getExtension()
			);
		});
	}

	/**
	 * Returns new file generated inside method
	 * @return http://localhost:8080/examples-responses/responses/file-generate
	 */
	@Action(path="file-generate")
	public ResponseAction fileFromBinaryContent() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			try {
				bout.write("Generated".getBytes());
				bout.write("\n".getBytes());
				bout.write("File content :-D".getBytes());
			} catch (IOException e) {
				// never happends
			}
			return Response.create(StatusCode.OK).getFileDownload(
				"generatedFileToDownload_" + new Date().getTime() + ".odt",
				bout.toByteArray()
			);
		});
	}

	/**
	 * Returns data as JSON. See https://ondrej-nemec.github.io/JI/?file=files-json.html for more about Object->JSON
	 * @return http://localhost:8080/examples-responses/responses/json
	 */
	@Action(path="json")
	public ResponseAction jsonResponse() {
		return ResponseBuilder.get().createResponse((req, translator, identity)->{
			Map<String, Object> json = new MapInit<String, Object>()
					.append("first", "value")
					.append("second", false)
					.toMap();
				
				return Response.OK().getJson(json);
		});
	}

	/**
	 * Parse given template with paramters to HTML
	 * @return http://localhost:8080/examples-responses/responses/template
	 */
	@Action(path="template")
	public ResponseAction templateResponse() {
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
			return Response.create(StatusCode.TEMPORARY_REDIRECT).getRedirect("/application/response/text");
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
}
