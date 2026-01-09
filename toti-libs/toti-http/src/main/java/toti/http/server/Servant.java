package toti.http.server;

import java.io.IOException;
import java.net.Socket;

public interface Servant {

	void serve(Socket socket, String charset) throws IOException;

}
