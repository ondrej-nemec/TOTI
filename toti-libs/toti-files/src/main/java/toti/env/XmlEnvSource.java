package toti.env;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import ji.files.text.Text;
import ji.xml.XmlObject;
import ji.xml.XmlReader;
import toti.common.functions.InputStreamLoader;

public class XmlEnvSource {

	public static Map<Object, Value> parse(String path) throws IOException {
		XmlObject xml = Text.get().read(br->{
			try {
				return new XmlReader().read(br.getBufferedReader());
			} catch (XMLStreamException e) {
				throw new IOException(e);
			}
		}, InputStreamLoader.createInputStream(XmlEnvSource.class, path));
		return parse(xml).getMap();
	}

	private static Value parse(XmlObject object) {
		if (object.getReferences().isEmpty() && object.getAttributes().isEmpty()) {
			return new Value(object.getValue().getValue());
		} else {
			if (object.getAttributes().size() > 0 || object.getReferencesSearch().size() > 1) {
				Value result = new Value(new HashMap<>());
				object.getReferencesSearch().forEach((key, values)->{
					if (values.size() == 1) {
						result.getMap().put(key, parse(values.get(0)));
					} else if (values.size() > 1) {
						Value val = new Value(new LinkedList<>());
						values.forEach(value->{
							val.getList().add(parse(value));
						});
						result.getMap().put(key, val);
					}
				});
				object.getAttributes().forEach((key, value)->{
					result.getMap().put(key, new Value(value.getValue()));
				});
				return result;
			} else {
				Value result = new Value(new LinkedList<>());
				object.getReferences().forEach(val->{
					result.getList().add(parse(val));
				});
				return result;
			}
		}
	}

}
