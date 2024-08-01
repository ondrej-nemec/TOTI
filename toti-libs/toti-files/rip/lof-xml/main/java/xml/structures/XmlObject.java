package text.xml.structures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class XmlObject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String name;
	
	private String value;
	
	private Map<String, String> attributes;
	
	private List<XmlObject> references;

	/*** SEPARATOR ***/
	
	public XmlObject(final String name) {
		this.name = name;
		this.value = "";
		this.attributes = new HashMap<>();
		this.references = new ArrayList<>();
	}
	
	public XmlObject(final String name, final String value) {
		this.name = name;
		this.value = value;
		this.attributes = new HashMap<>();
		this.references = new ArrayList<>();
	}
	
	public XmlObject(final Map<String, String> attributes, final String name) {
		this.name = name;
		this.value = "";
		this.attributes = attributes;
		this.references = new ArrayList<>();
	}
	
	public XmlObject(final String name, final List<XmlObject> references) {
		this.name = name;
		this.value = "";
		this.attributes = new HashMap<>();
		this.references = references;
	}
	
	public XmlObject(
			final String name,
			final Map<String, String> attributes,
			final List<XmlObject> references) {
		this.name = name;
		this.value = "";
		this.attributes = attributes;
		this.references = references;
	}
	
	public XmlObject(
			final String name,
			final String value,
			final Map<String, String> attributes,
			final List<XmlObject> references){
		this.name = name;
		this.value = value;
		this.attributes = attributes;
		this.references = references;
	}
	
	/*** SEPARATOR ***/
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public List<XmlObject> getReferences() {
		return references;
	}
	
	/*** SEPARATOR ***/
	
	public void setName(final String name){
		this.name = name;
	}
	
	public void setValue(final String value){
		this.value = value;
	}
	
	public void setAttributes(final Map<String, String> atributes){
		this.attributes = atributes;
	}
	
	public void setReferences(final List<XmlObject> references){
		this.references = references;
	}
	
	/*** SEPARATOR ***/
	
	@Override
	public String toString() {
		return toString("");
	}
	
	public String toString(String tab){
		String aux =tab;
		aux += name;
		//value
		aux += " [" + value + "]";
		//atributes
		aux += attributes;
		//references
		for(int i = 0; i<references.size();i++){
			aux +="\n" + tab + references.get(i).toString(tab + "\t");
		}
		return aux;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof XmlObject))
			return false;
		XmlObject aux = (XmlObject)o;
		if(!name.equals(aux.getName()))
			return false;
		
		if(!value.equals(aux.getValue()))
			return false;
		
		if(!attributes.equals(aux.getAttributes()))
			return false;

		if(!references.equals(aux.getReferences()))
			return false;
		return true;
	}
	
}
