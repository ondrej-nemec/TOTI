package text.xml;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import text.BufferedReaderFactory;
import text.xml.structures.XmlObject;

@RunWith(JUnitParamsRunner.class)
public class XmlLoaderTest {
	
	private String consumerActual = "";
	private String file = "/text/xml/read-xmlobject.xml";
	private String specialFile = "/text/xml/read-special.xml";

	@Test
	@Parameters
	public void testReadConsumerWorks(String path, String expected) throws FileNotFoundException, IOException, XMLStreamException {
		try(BufferedReader br = BufferedReaderFactory.buffer(getClass().getResourceAsStream(path))){
			XmlLoader loader = new XmlLoader(br);
			Consumer<XMLStreamReader> consumer = (in)->{
				if(in.getEventType() == XMLStreamConstants.START_ELEMENT){
					for(int i = 0; i<in.getAttributeCount(); i++){
						setConsumerActual(in.getAttributeLocalName(i) + ": " +in.getAttributeValue(i));
					}
				}
			};
			loader.read(consumer);
			assertEquals(expected, consumerActual);
		}
	}
	
	public Collection<List<Object>> parametersForTestReadConsumerWorks() {
		return Arrays.asList(
				Arrays.asList(
					file,
					"class: class1\n"
					+ "class: class2\n"
					+ "target: blank\n"
				),
				Arrays.asList(specialFile, "")
			);
	}

	@Test
	@Parameters
	public void testReadXmlObjectWorks(String path, XmlObject expected) throws XMLStreamException, FileNotFoundException, IOException {
		try(BufferedReader br = BufferedReaderFactory.buffer(getClass().getResourceAsStream(path))){
			XmlLoader loader = new XmlLoader(br);
			assertEquals(expected, loader.read());
		}
	}
	
	public Collection<List<Object>> parametersForTestReadXmlObjectWorks() {
		return Arrays.asList(
					Arrays.asList(file, getXmlObject()),
					Arrays.asList(
						specialFile,
						new XmlObject("element", Arrays.asList(new XmlObject("element", "Value")))
					)
				);
	}
	
	public XmlObject getXmlObject(){
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
	
	private void setConsumerActual(String add){
		consumerActual += add + "\n";
	}
	
}
