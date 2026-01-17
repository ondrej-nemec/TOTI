package toti.files.xml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.MapDictionary;

public class XmlObject {

	private final String name;
	
	private final StringBuilder value;
	private final MapDictionary<String> attributes;
	private final List<XmlObject> references;
	private final Map<String, List<XmlObject>> referencesSearch;

	public XmlObject(String name) {
		this.name = name;
		this.attributes = MapDictionary.hashMap();
		this.references = new LinkedList<>();
		this.referencesSearch = new HashMap<>();
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
	
	public boolean containsAttribute(String key) {
		return attributes.containsKey(key);
	}
	
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	public MapDictionary<String> getAttributes() {
		return attributes;
	}
	
	public boolean containsReference(String key) {
		return referencesSearch.containsKey(key);
	}
	
	public XmlObject getReference(String name) {
		List<XmlObject> list = referencesSearch.get(name);
		if (list == null || list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	public List<XmlObject> getReferences(String name) {
		List<XmlObject> list = referencesSearch.get(name);
		if (list == null) {
			return null;
		}
		return list;
	}
	
	public XmlObject getReference(int index) {
		return references.get(index);
	}
	
	public List<XmlObject> getReferences() {
		return references;
	}

	public Map<String, List<XmlObject>> getReferencesSearch() {
		return referencesSearch;
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
		if (!referencesSearch.containsKey(reference.getName())) {
			referencesSearch.put(reference.getName(), new LinkedList<>());
		}
		referencesSearch.get(reference.getName()).add(reference);
		return this;
	}
	
	 @Override
	public String toString() {
		return toString("");
	}
	
	protected String toString(String prefix) {
		StringBuilder res = new StringBuilder();
		res.append(String.format("%sXML(%s) [%s]", prefix, name, value));
		attributes.forEach((key, attr)->{
			res.append(String.format("\n%s  %s: %s", prefix, key, attr));
		});
		references.forEach(ref->{
			res.append(String.format("\n%s  * %s", prefix, ref.toString(prefix + "  ")));
		});
		return res.toString();
	}
}
