package toti.lib.files.xml;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import toti.lib.common.structures.MapDictionary;

public class XmlWritter {

	public String write(XmlObject data) throws XMLStreamException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		write(data, new BufferedWriter(new OutputStreamWriter(bos)));
		return new String(bos.toByteArray());
	}

	public void write(XmlObject data, BufferedWriter bw) throws XMLStreamException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		XMLStreamWriter out = factory.createXMLStreamWriter(bw);
		try {
			out.writeStartDocument();
			writeLevel(out, data);
			out.writeEndDocument();
			out.flush();
		} finally {
			try {
				if(out != null)
					out.close();
			} catch (Exception e) {
				throw new XMLStreamException(e);
			}
		}
	}
	
	private void writeLevel(final XMLStreamWriter out, final XmlObject object) throws XMLStreamException {
		out.writeStartElement(object.getName());
		writeAtribute(out, object.getAttributes());
		for (Object obj : object.getContent()) {
			if (obj == null) {
				// ignore
			} else if (obj instanceof XmlObject sub) {
				writeLevel(out, sub);
			} else {
				out.writeCharacters(obj.toString());
			}
		}
		out.writeEndElement();
	}
	
	private void writeAtribute(final XMLStreamWriter out, final MapDictionary<String> attributes) throws XMLStreamException {
		if(attributes != null){
			Set<String> set = attributes.keySet();
			for (String key : set) {
				Object value = attributes.get(key);
				out.writeAttribute(key, value == null ? "" : value.toString());
			}
		}
	}
}
