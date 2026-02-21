package toti.lib.translator;

import java.util.Map;

import toti.lib.files.access.FileUtils;

public class PropertiesMessagesFile implements MessagesFile {

	@Override
	public Map<Object, Object> getMessages(String filename) throws Exception {
		return FileUtils.loadProperties(filename);
	}

	@Override
	public String getExtension() {
		return "properties";
	}
	
	

}
