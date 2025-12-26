package text.xml;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import exceptions.StreamCouldNotBeClosedException;
import text.xml.structures.XmlObject;

public class XmlCreatorTest {
	//TODO add some end-to-end tests?
	
	@Test
	public void testWriteConsumerWorks() throws StreamCouldNotBeClosedException, XMLStreamException{	
		XmlCreator creator = new XmlCreator(null); //BufferedWriter not used in this test
		Consumer<XMLStreamWriter> consumer = (out)->{
				try {
					out.writeStartElement("element");
					out.writeCharacters("Value");
					out.writeEndElement();
				} catch (Exception e) {
					e.printStackTrace();
					fail("XmlStreamException");
				}
			};
		
		XMLStreamWriter out = mock(XMLStreamWriter.class);			
		creator.write(out, consumer);
		
		verify(out).writeStartDocument();
		verify(out).writeStartElement("element");
		verify(out, times(1)).writeStartElement(Mockito.anyString());
		
		verify(out).writeCharacters("Value");
		verify(out, times(1)).writeCharacters(Mockito.anyString());
		
		verify(out).writeEndElement();
		verify(out).writeEndDocument();
	}
	
	
	@Test
	public void testWriteXmlObjectWorks() throws XMLStreamException, StreamCouldNotBeClosedException{
		XmlCreator creator = new XmlCreator(null); // BufferedWriter not used in this test
		XmlObject xmlObjec = getObject();
		
		XMLStreamWriter out = mock(XMLStreamWriter.class);
		creator.write(out, xmlObjec);
		
		verify(out).writeStartDocument();
		
		verify(out).writeStartElement("element");
		verify(out).writeStartElement("first");
		verify(out).writeStartElement("second");
		verify(out).writeStartElement("subelement");
		verify(out, times(4)).writeStartElement(Mockito.anyString());
		
		verify(out).writeAttribute("class", "class1");
		verify(out).writeAttribute("class", "class2");
		verify(out).writeAttribute("target", "blank");
		verify(out, times(3)).writeAttribute(Mockito.anyString(), Mockito.anyString());
		
		verify(out).writeCharacters("Value of element");
		verify(out).writeCharacters("Sub-element");
		verify(out, times(2)).writeCharacters("");
		verify(out, times(4)).writeCharacters(Mockito.anyString());
		
		verify(out, times(4)).writeEndElement();
		verify(out).writeEndDocument();
	}
	
	private XmlObject getObject(){
		Map<String, String> firstAttributes = new HashMap<>();
		firstAttributes.put("class", "class1");
		XmlObject first = new XmlObject("first", "Value of element", firstAttributes, new ArrayList<>());
		
		Map<String, String> secondAttributes = new HashMap<>();
		secondAttributes.put("class", "class2");
		secondAttributes.put("target", "blank");
		XmlObject second = new XmlObject("second", Arrays.asList(new XmlObject("subelement", "Sub-element")));
		second.setAttributes(secondAttributes);
		
		return new XmlObject("element", Arrays.asList(first, second));
	}
}
