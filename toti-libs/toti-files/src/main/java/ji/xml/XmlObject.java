package ji.xml;

import java.util.LinkedList;
import java.util.List;

import ji.common.structures.DictionaryValue;
import ji.common.structures.MapDictionary;

public class XmlObject {

	private final String name;
	
	private final StringBuilder value;
	private final MapDictionary<String> attributes;
	private final List<XmlObject> references;

	public XmlObject(String name) {
		this.name = name;
		this.attributes = MapDictionary.hashMap();
		this.references = new LinkedList<>();
		this.value = new StringBuilder();
	}
	
	public String getName() {
		return name;
	}
	
	public DictionaryValue getValue() {
		if (value.toString().isEmpty()) {
			return new DictionaryValue(null);
		}
		return new DictionaryValue(value.toString());
	}
	
	public MapDictionary<String> getAttributes() {
		return attributes;
	}
	
	public XmlObject getReference(int index) {
		return references.get(index);
	}
	
	public List<XmlObject> getReferences() {
		return references;
	}
	
	public XmlObject addValue(String value) {
		this.value.append(value);
		return this;
	}
	
	public XmlObject addAtribute(String key, String value) {
		this.attributes.put(key, value);
		return this;
	}
	
	public XmlObject addReference(XmlObject reference) {
		this.references.add(reference);
		return this;
	}
	
}
