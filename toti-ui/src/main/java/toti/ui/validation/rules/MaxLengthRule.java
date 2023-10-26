package toti.ui.validation.rules;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import ji.translator.Translator;

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
