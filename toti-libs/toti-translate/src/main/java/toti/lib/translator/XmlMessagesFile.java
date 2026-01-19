package toti.lib.translator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import toti.lib.common.functions.InputStreamLoader;
import toti.lib.files.env.XmlEnvSource;
import toti.lib.files.text.Text;
import toti.lib.files.xml.XmlObject;
import toti.lib.files.xml.XmlReader;

public class XmlMessagesFile implements MessagesFile {

	@Override
	public Map<Object, Object> getMessages(String filename) throws Exception {
		XmlObject xml = Text.get().read(br->{
			try {
				return new XmlReader().read(br.getBufferedReader());
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}, InputStreamLoader.createInputStream(XmlEnvSource.class, filename));
		Map<Object, Object> result = new HashMap<>();
		xml.getReferences().forEach(sub->{
			parse(sub, result, null);
		});
		return result;
	}

	private void parse(XmlObject xml, Map<Object, Object> result, String preKey) {
		String key = (preKey == null ? "" : preKey + ".") + xml.getName();
		if (xml.getValue().isPresent()) {
			result.put(key, xml.getValue().getValue());
		}
		xml.getReferences().forEach(sub->{
			parse(sub, result, key);
		});
	}

	@Override
	public String getExtension() {
		return "xml";
	}
	

}
