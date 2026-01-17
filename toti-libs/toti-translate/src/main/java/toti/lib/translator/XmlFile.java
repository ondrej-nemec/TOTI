package toti.lib.translator;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import toti.lib.common.structures.DictionaryValue;
import toti.lib.files.text.Text;
import toti.lib.files.xml.XmlObject;
import toti.lib.files.xml.XmlReader;

public class XmlFile implements MessagesFile {
	
	private final XmlObject xml;

	// TODO test it
	public XmlFile(String file) throws Exception {
		this.xml = Text.get().read((readText)->{
			try {
				return new XmlReader().read(readText.getBufferedReader());
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}, file);
	}

	@Override
	public String getMessage(String key) {
		String[] split = key.split("\\.");
		XmlObject result = xml;
		for (String part : split) {
			if (result.containsReference(part)) {
				result = result.getReference(part);
				// TODO imporovement - more references - count
			} else {
				return null;
			}
		}
		DictionaryValue message = result.getValue();
		if (message.isPresent()) {
			return message.getString();
		}
		return null;
	}

}
