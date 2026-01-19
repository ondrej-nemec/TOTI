package toti.lib.translator;

import java.util.Map;

public interface MessagesFile {

	Map<Object, Object> getMessages(String filename) throws Exception;

	String getExtension();
	
}
