package toti.http.http.profiler;

import java.util.Map;

public interface HttpServerProfiler {

	void log(Map<HttpServerProfilerEvent, Long> events);
		
}
