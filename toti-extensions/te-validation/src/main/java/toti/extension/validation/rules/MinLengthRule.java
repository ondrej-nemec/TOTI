package toti.extension.validation.rules;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import toti.extensions.Translator;
import toti.lib.common.structures.DictionaryValue;

public class MinLengthRule extends SimpleRule<Integer> {

	public MinLengthRule(Integer value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Integer minLength, Object o) {
		DictionaryValue dicVal = new DictionaryValue(o);
		if (dicVal.is(Map.class)) {
			return minLength.intValue() > dicVal.getMap().size();
		}
		if (dicVal.is(List.class)) {
			return minLength.intValue() > dicVal.getList().size();
		}
		return minLength.intValue() > o.toString().length();
	}

}
