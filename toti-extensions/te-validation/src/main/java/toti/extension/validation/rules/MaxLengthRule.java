package toti.extension.validation.rules;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.lib.common.structures.DictionaryValue;

public class MaxLengthRule extends SimpleRule<Integer> {

	public MaxLengthRule(Integer value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Integer maxLength, Object o) {
		DictionaryValue dicVal = new DictionaryValue(o);
		if (dicVal.is(Map.class)) {
			return maxLength.intValue() < dicVal.getMap().size();
		}
		if (dicVal.is(List.class)) {
			return maxLength.intValue() < dicVal.getList().size();
		}
		return maxLength.intValue() < o.toString().length();
	}

}
