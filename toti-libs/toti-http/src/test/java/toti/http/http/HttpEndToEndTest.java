package toti.http.http;

import static org.junit.Assert.fail;

import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Logger;

import ji.common.Log4j2LoggerTestImpl;
import ji.common.structures.MapDictionary;
import ji.common.structures.MapInit;
import ji.common.structures.Tuple2;
import toti.http.Server;
import toti.http.SslCredentials;
import toti.http.http.structures.Protocol;
import toti.http.http.structures.Request;
import toti.http.http.structures.Response;

public class HttpEndToEndTest {

	public static void main(String[] args) {
		Logger logger = new Log4j2LoggerTestImpl(null);
		String charset = "";
		Optional<SslCredentials> ssl = Optional.empty();
		int port = 10123;
		Integer maxBodySize = null;
		
		Map<String, Tuple2<Request, Response>> data = getData();
		
		try {
			HttpClient client = new HttpClient("localhost", port, Protocol.HTTP_1_1, ssl, 60000, maxBodySize, logger);
			
			/*Server server = Server.createWebServer(
				port, 1, 60000, ssl, maxBodySize, charset, logger
			);*/
			
			HttpServer restApi = new HttpServer(maxBodySize, logger);
			//*/
			restApi.addApplication((req, ipAddress, websocket)->{
				Tuple2<Request, Response> res = data.get(req.getPlainUri());
				if (res != null) {
					if (!req.equals(res._1())) {
						logger.error("Request Failure: " + req.getPlainUri());
						logger.error(" Expected:");
						logger.error(res._1());
						logger.error(" But was:");
						logger.error(req);
					} else {
						logger.info("Request Success: " + req.getPlainUri());
					}
					return res._2();
				}
				logger.error("Request Failure (null): " + req.getPlainUri());
				return new Response(StatusCode.BAD_REQUEST, req.getProtocol());
			}, "localhost", "example.com");
			//*/
			Server server = restApi.createWebServer(port, 1, 60000, ssl, charset);
			
			server.start();
			
			try {
				data.forEach((uri, tuple)->{
					logger.info("---------------------------");
					logger.info("Run test: " + uri);
					try {
						Response res = client.send(tuple._1());
						if (!res.equals(tuple._2())) {
							logger.error("Response Failure: " + uri);
							logger.error(" Expected:");
							logger.error(tuple._2());
							logger.error(" But was");
							logger.error(res);
						} else {
							logger.info("Response Success: " + uri);
						}
					} catch (Exception e) {
						logger.error("Failure: " + uri);
						e.printStackTrace();
					}
				});
			} catch(Throwable t) { t.printStackTrace(); }
			
			server.stop();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		System.exit(0);
	}
	
	public static Map<String, Tuple2<Request, Response>> getData() {
		// TODO url, request, response
		/*
		return (request, ipAddress, websocket)->{
			switch (request.getPlainUri()) {
				case "/empty":
				case "/headers":
				case "/plain":
				case "/binary":
					Response response = new Response(StatusCode.OK, request.getProtocol());
					response.setBody(binaryData.toByteArray());
					return response;
				case "/urlencode":
				case "/form":
				case "/websocket":
				default:
					return new Response(StatusCode.BAD_REQUEST, request.getProtocol());
			}
		};
		
		ByteArrayOutputStream binaryData = new ByteArrayOutputStream();
		try {
			try (InputStream is = new FileInputStream("index/icon.png")) {
				int i;
				while((i = is.read()) != -1) {
					binaryData.write(i);
				}
			}
		} catch (IOException e) {
			fail();
		}
		
		*/
		// String protocol = "HTTP1/1";
		String uri = "/example/uri";
		return new MapInit<String, Tuple2<Request, Response>>()
			.append("/plain", getPlain(uri, Protocol.HTTP_1_1))
		.toMap();
	}
	
	private static Tuple2<Request, Response> getPlain(String uri, Protocol protocol) {
		Request request = new Request(HttpMethod.POST, uri, protocol);
		request.setUriParams(uri, MapDictionary.hashMap());
		request.setBody(new byte[] {});
		
		Response response = new Response(StatusCode.OK, protocol);
		response.setBody(new byte[] {});
		
		return new Tuple2<>(request, response);
	}
	
}
