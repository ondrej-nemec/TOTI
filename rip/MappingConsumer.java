package toti.url;

import ji.socketCommunication.http.HttpMethod;

public interface MappingConsumer {

	void accept(MappedUrl mappedUrl, String url, HttpMethod[] methods);
	
}
