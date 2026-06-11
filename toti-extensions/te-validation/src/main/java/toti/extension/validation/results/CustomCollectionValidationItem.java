package toti.extension.validation.results;

import java.util.Collection;

public interface CustomCollectionValidationItem {

	void addError(String error);

	Object getValue(String name);

	void removeItem(String propertyName);

	void setItem(String propertyName, Object value);

	Collection<String> getItemsNames();



}
