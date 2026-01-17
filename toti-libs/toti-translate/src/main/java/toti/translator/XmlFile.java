package toti.translator;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import ji.files.text.Text;
import ji.xml.XmlObject;
import ji.xml.XmlReader;
import toti.common.structures.DictionaryValue;

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
