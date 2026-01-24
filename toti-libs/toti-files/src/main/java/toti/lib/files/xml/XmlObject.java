package toti.lib.files.xml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.MapDictionary;

public class XmlObject {

	private final String name;
	private final MapDictionary<String> attributes;

	private final List<Object> content;

	private final StringBuilder value;

	private final List<XmlObject> references;
	private final Map<String, List<XmlObject>> referencesSearch;

	public XmlObject(String name) {
		this.name = name;
		this.attributes = MapDictionary.hashMap();
		this.content = new LinkedList<>();

		this.references = new LinkedList<>();
		this.referencesSearch = new HashMap<>();
		this.value = new StringBuilder();
	}
	
	public String getName() {
		return name;
	}

	protected List<Object> getContent() {
		return content;
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
		if (list == null || list.isEmpty()) {
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
	
	public XmlObject addValue(Object value) {
		content.add(value);
		this.value.append(value);
		return this;
	}
	
	public XmlObject addAtribute(String key, Object value) {
		this.attributes.put(key, value == null ? null : value.toString());
		return this;
	}

	public XmlObject addReference(XmlObject reference) {
		this.references.add(reference);
		if (!referencesSearch.containsKey(reference.getName())) {
			referencesSearch.put(reference.getName(), new LinkedList<>());
		}
		referencesSearch.get(reference.getName()).add(reference);
		content.add(reference);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result + ((references == null) ? 0 : references.hashCode());
		result = prime * result + ((referencesSearch == null) ? 0 : referencesSearch.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		XmlObject other = (XmlObject) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.toString().equals(other.value.toString())) {
			return false;
		}
		if (attributes == null) {
			if (other.attributes != null) {
				return false;
			}
		} else if (!attributes.equals(other.attributes)) {
			return false;
		}
		if (references == null) {
			if (other.references != null) {
				return false;
			}
		} else if (!references.equals(other.references)) {
			return false;
		}
		if (referencesSearch == null) {
			if (other.referencesSearch != null) {
				return false;
			}
		} else if (!referencesSearch.equals(other.referencesSearch)) {
			return false;
		}
		return true;
	}


	
}
