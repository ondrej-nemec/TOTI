package ji.xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import ji.common.exceptions.LogicException;

public class XmlReader {

	public XmlObject read(String xml) throws XMLStreamException {
		ByteArrayInputStream is = new ByteArrayInputStream(xml.getBytes());
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			return read(br);
		} catch (IOException e) {
			// ignore - no reason for it
			throw new LogicException("Unexpected IOException with String " + e.getMessage());
		}
	}

	public XmlObject read(BufferedReader br) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader in = null;
		try {
			in = factory.createXMLStreamReader(br);
			in.next();
			return readLevel(in);
		} finally {
			try {
				in.close();
			} catch (Exception e) {
				throw new XMLStreamException(e);
			}
		}
	}
	
	private XmlObject readLevel(final XMLStreamReader in) throws XMLStreamException {
		//String name = in.getName().getLocalPart();
		XmlObject result = new XmlObject(in.getName().getLocalPart());

		for (int i = 0; i<in.getAttributeCount(); i++){
			result.addAtribute(
				in.getAttributeLocalName(i),
				in.getAttributeValue(i)
			);
		}
		while (in.hasNext() && in.getEventType()!=XMLStreamConstants.END_ELEMENT){
			in.next();
			if (in.getEventType() == XMLStreamConstants.CHARACTERS){
				String value = in.getText();
				Pattern p = Pattern.compile("[\t|\n]*"); //(.*)([\t|\n| ]*)
				Matcher m = p.matcher(value);
				while(m.find()){
					value = value.replace(m.group(), "");
				}
				result.addValue(value);
			} else if (in.getEventType() == XMLStreamConstants.START_ELEMENT){
				while(in.getEventType() != XMLStreamConstants.END_ELEMENT || in.getName().getLocalPart() != result.getName()){
					if (in.getEventType() == XMLStreamConstants.START_ELEMENT ){
						result.addReference(readLevel(in));
					} else /*if(in.hasNext())*/ {
						in.next();
					}
				}
			}
		}
		return result;
	}
	
}
