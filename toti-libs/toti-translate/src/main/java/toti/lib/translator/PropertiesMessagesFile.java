package toti.lib.translator;

import java.util.Map;

import toti.lib.common.functions.PropertiesLoader;

public class PropertiesMessagesFile implements MessagesFile {

	@Override
	public Map<Object, Object> getMessages(String filename) throws Exception {
		return PropertiesLoader.loadProperties(filename);
	}

	@Override
	public String getExtension() {
		return "properties";
	}
	
	

}
