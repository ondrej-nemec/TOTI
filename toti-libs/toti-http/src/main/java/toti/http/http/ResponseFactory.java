package toti.http.http;

import java.io.IOException;
import java.util.Optional;

import toti.http.http.profiler.HttpServerProfiler;
import toti.http.http.structures.Request;
import toti.http.http.structures.Response;
import toti.http.http.structures.WebSocket;

public interface ResponseFactory {

	Response accept(Request request, String ipAddress, Optional<WebSocket> webSocket) throws IOException;
	
	default HttpServerProfiler getProfiler() {
		return null;
	}
	
}
