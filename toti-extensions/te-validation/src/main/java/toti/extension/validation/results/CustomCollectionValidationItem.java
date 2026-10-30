package toti.extension.validation.results;

import java.util.Collection;

import toti.lib.common.structures.dictionary.ScalarStructure;

public interface CustomCollectionValidationItem extends ScalarStructure<String> {

	void addError(String error);

	Object getValue(String name);

	void removeItem(String propertyName);

	void setItem(String propertyName, Object value);

	Collection<String> getItemsNames();



}
