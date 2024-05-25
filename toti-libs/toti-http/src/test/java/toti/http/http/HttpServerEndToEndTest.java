package toti.http.http;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

import ji.common.Log4j2LoggerTestImpl;
import ji.common.structures.IntegerBuilder;
import toti.http.Server;
import toti.http.SslCredentials;
import toti.http.http.profiler.HttpServerProfiler;
import toti.http.http.profiler.HttpServerProfilerEvent;
import toti.http.http.structures.Request;
import toti.http.http.structures.Response;
import toti.http.http.structures.UploadedFile;
import toti.http.http.structures.WebSocket;

public class HttpServerEndToEndTest {
	private static final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor();

	public static void main(String[] args) {
		try {
			Logger logger = new Log4j2LoggerTestImpl(null);
			/*RestApiServer.PROFILER = new HttpServerProfiler() {
				@Override
				public void log(Map<HttpServerProfilerEvent, Long> events) {
					logger.debug("Profiler");
					for (HttpServerProfilerEvent event : HttpServerProfilerEvent.values()) {
						logger.debug(" -> " + event + ": " + events.get(event));
					}
				}
			};*/
			
			HttpServer restApi = new HttpServer(10 * 1000 * 1000, logger);
			restApi.addApplication(apiResponse(logger), "localhost", "example.com");
			/*
			Optional<SslCredentials> ssl = Optional.empty();
			int port = 80;
			/*/
			SslCredentials cred = new SslCredentials();
			cred.setCertificateStore("certificates/cert.p12", "betasecret", "PKCS12");
			Optional<SslCredentials> ssl = Optional.of(cred);
			int port = 443;
			//*/
			Server server = restApi.createWebServer(port, 5, 60000, ssl, "UTF-8");
			
			server.start();
			for (int i = 0; i < 30; i++) {
	            Thread.sleep(i * 10000);
	        //    console.out("Server running");
	        }
	        server.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected static ResponseFactory apiResponse(Logger logger) {
		return new ResponseFactory() {
			@Override
			public Response accept(Request request, String ipAddress, Optional<WebSocket> websocket) throws IOException {
				switch (request.getPlainUri()) {
					case "/image.png": 
					case "/favicon.ico": return getFileResponse(request, "icon.png", "image/icon");
					case "/": // return getTextResponse(request, "OK");
					case "/index": return getFileResponse(request, "index.html", "text/html");
					case "/form": return getFileResponse(request, "form.html", "text/html");
					case "/fileform": return getFileResponse(request, "fileform.html", "text/html");
					case "/websocket": return getFileResponse(request, "websockets.html", "text/html");
					case "/redirect":
						Response redirect = new Response(StatusCode.TEMPORARY_REDIRECT, request.getProtocol());
						redirect.addHeader("Location", "/params");
						return redirect;
					case "/savefile":
						UploadedFile file = request.getBodyInParameters().getUploadedFile("fileToUpload");
						file.save("binary");
						Response fileResponse = getTextResponse(request,"File: " + file.getFileName() + " (" + file.getContent().length + "b)");
						return fileResponse;
					case "/params":
						StringBuilder params = new StringBuilder();
						params
							.append("URI params:")
							.append("<br>")
							.append(request.getQueryParameters())
							.append("<br><hr><br>")
							.append("Body parameters:")
							.append("<br>")
							.append(request.getBodyInParameters())
							.append("<br>")
							.append(request.getBody());
						Response paramsResponse = getTextResponse(request, params.toString());
						paramsResponse.addHeader("Content-Type", "text/html");
						return paramsResponse;
					case "/ws":
						if (websocket.isPresent()) {
							IntegerBuilder i = new IntegerBuilder();
							ScheduledFuture<?> task = pool.scheduleWithFixedDelay(()->{
								//System.err.println("Task start");
								if (Thread.currentThread().isInterrupted()) {
									System.err.println("Task interrupted");
									return;
								}
								if (websocket.get().isClosed()) {
									System.err.println("Websocket closed");
									return;
								}
								if (i.get() > 20) {
									websocket.get().close();
									System.err.println("Task closed");
									return;
								}
								if (!websocket.get().isRunning()) {
									System.err.println("Not start yet");
									return;
								}
								try {
									websocket.get().send("Message #" + i.get());
									System.err.println("Message sent");
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								i.add(1);
							}, 0, 5, TimeUnit.SECONDS);
							Response wbRes = new Response(StatusCode.SWITCHING_PROTOCOL, request.getProtocol());
							websocket.get().accept(
								(isBinary, message)->{
									String text = new String(message.toByteArray());
									try {
										if (text.equals("end")) {
											websocket.get().close();
										} else {
											websocket.get().send("Response: " + (isBinary ? " BINARY " : "") + text);
										}
									} catch (IOException e1) {
										e1.printStackTrace();
									}
								},
								(e)->{
									e.printStackTrace();
								}, (m)->{
									System.err.println("On close");
									task.cancel(true);
								}
							);
							return wbRes;
						}
					default:
						return new Response(StatusCode.BAD_REQUEST, request.getProtocol());
				}
			}
			
			@Override
			public HttpServerProfiler getProfiler() {
				return new HttpServerProfiler() {
					@Override
					public void log(Map<HttpServerProfilerEvent, Long> events) {
						logger.debug("Profiler");
						for (HttpServerProfilerEvent event : HttpServerProfilerEvent.values()) {
							logger.debug(" -> " + event + ": " + events.get(event));
						}
					}
				};
			}
		};
	}
	
	private static Response getTextResponse(Request request, String data) {
		Response response = new Response(StatusCode.OK, request.getProtocol());
		response.setBody(data.getBytes());
		return response;
	}
	
	private static Response getFileResponse(Request request, String file, String contentType) throws IOException {
		Response response = new Response(StatusCode.OK, request.getProtocol());
		response.addHeader("Content-Type", contentType);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (InputStream is = new FileInputStream("index/" + file)) {
			int i;
			while((i = is.read()) != -1) {
				os.write(i);
			}
		}
		response.setBody(os.toByteArray());
		return response;
	}
	
}
