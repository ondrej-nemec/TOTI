package toti.validation;

import common.structures.DictionaryValue;

public class ParseObject {
	
	public static Object parse(Class<?> clazz, Object object) {
		DictionaryValue value = new DictionaryValue(object);
		/*value.addMapCallback((v)->{
			try {
				return new JsonReader().read(v);
			} catch (JsonStreamException e) {
				throw new RuntimeException(e);
			}
		});
		value.addListCallback((v)->{
			try {
				return new JsonReader().read(v);
			} catch (JsonStreamException e) {
				throw new RuntimeException(e);
			}
		});*/
		return value.getValue(clazz);
	}
	
}
