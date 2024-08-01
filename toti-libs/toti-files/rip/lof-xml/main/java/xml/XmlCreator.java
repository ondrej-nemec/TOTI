package text.xml;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


import exceptions.StreamCouldNotBeClosedException;
import text.xml.structures.XmlObject;

public class XmlCreator {
	
	private final BufferedWriter bw;
	
	public XmlCreator(final BufferedWriter bw) {
		this.bw = bw;
	}
	
	public void write(final Consumer<XMLStreamWriter> consumer) throws XMLStreamException, StreamCouldNotBeClosedException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		write(factory.createXMLStreamWriter(bw), consumer);
	}
	
	protected void write(final XMLStreamWriter out, Consumer<XMLStreamWriter> consumer) throws XMLStreamException, StreamCouldNotBeClosedException {
		try {
			out.writeStartDocument();
			consumer.accept(out);
			out.writeEndDocument();
			out.flush();
		} finally {
			try {
				if(out != null)
					out.close();
			} catch (Exception e) {
				throw new StreamCouldNotBeClosedException();
			}
		}
	}

	public boolean write(final XmlObject object) throws XMLStreamException, StreamCouldNotBeClosedException {
		XMLOutputFactory factory = XMLOutputFactory.newInstance();
		return write(factory.createXMLStreamWriter(bw), object);
	}
	
	protected boolean write(final XMLStreamWriter out, final XmlObject object) throws XMLStreamException, StreamCouldNotBeClosedException {
		try {
			out.writeStartDocument();
			writeLevel(out, object);
			out.writeEndDocument();
			out.flush();
		} finally {
			try {
				if(out != null)
					out.close();
			} catch (Exception e) {
				throw new StreamCouldNotBeClosedException();
			}
		}
		return true;
	}
	
	private void writeLevel(final XMLStreamWriter out, final XmlObject object) throws XMLStreamException {
		out.writeStartElement(object.getName());
		writeAtribute(out, object.getAttributes());
		writeValue(out, object.getValue());
		writeReferences(out, object.getReferences());
		out.writeEndElement();
	}
	
	private void writeValue(final XMLStreamWriter out, String value) throws XMLStreamException {
		if(value != null)
			out.writeCharacters(value);
	}
	
	private void writeAtribute(final XMLStreamWriter out, final Map<String, String> attributes) throws XMLStreamException {
		if(attributes != null){
			Set<String> set = attributes.keySet();
			for (String key : set) {
				out.writeAttribute(key, attributes.get(key));
			}
		}
	}
	
	private void writeReferences(XMLStreamWriter out, List<XmlObject> references) throws XMLStreamException {
		if(references != null)
			for(int i = 0; i<references.size();i++){
				writeLevel(out, references.get(i));
			}
	}
	
}
