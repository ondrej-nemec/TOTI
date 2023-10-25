package toti.ui.validation.rule;

import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import ji.translator.Translator;

public class MaxValueRule extends SimpleRule<Integer> {

	public MaxValueRule(Integer value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Integer maxValue, Object o) {
		try {
			Number value = new DictionaryValue(o).getNumber();
			return value == null || maxValue.doubleValue() < value.doubleValue();
		} catch (NullPointerException | ClassCastException | NumberFormatException e) {
			return true;
		}
	}
}
