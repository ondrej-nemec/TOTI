package toti.ui.validation.rule;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import ji.translator.Translator;

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
