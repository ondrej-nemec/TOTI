package toti.extension.validation.results;

import java.util.Collection;

import toti.lib.common.structures.Dictionary;

public interface CustomCollectionValidationItem extends Dictionary<String> {

	void addError(String error);

	Object getValue(String name);

	void removeValue(String propertyName);

	void setValue(String propertyName, Object value);

	Collection<String> getItemsNames();

}
