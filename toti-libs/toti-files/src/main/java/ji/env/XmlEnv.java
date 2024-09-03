package ji.env;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import ji.common.functions.Env;
import ji.common.functions.InputStreamLoader;
import ji.files.text.Text;
import ji.xml.XmlObject;
import ji.xml.XmlReader;

public class XmlEnv implements Env {
	
	private final XmlObject object;
	
	public static XmlEnv create(String path) throws XMLStreamException, IOException {
		String xml = Text.get().read(
			br->br.asString(),
			InputStreamLoader.createInputStream(XmlEnv.class, path)
		);
		return new XmlEnv(new XmlReader().read(xml));
	}
	
	private XmlEnv(XmlObject object) {
		this.object = object;
	}

	@Override
	public Object getValue(String key) {
		if (object.containsAttribute(key)) {
			return object.getAttribute(key);
		}
		if (object.containsReference(key)) {
			return object.getReference(key).getValue().getValue();
		}
		return null;
	}
	
	@Override
	public void clear() {
		// how?
	}

	@Override
	public Env getModule(String key) {
		return new XmlEnv(object.getReference(key));
	}

}
