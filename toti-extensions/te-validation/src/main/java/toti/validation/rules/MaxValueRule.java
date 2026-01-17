package toti.validation.rules;

import java.util.function.Function;

import toti.common.structures.DictionaryValue;
import toti.extensions.Translator;

public class MaxValueRule extends SimpleRule<Number> {

	public MaxValueRule(Number value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Number maxValue, Object o) {
		try {
			Number value = new DictionaryValue(o).getNumber();
			return value == null || maxValue.doubleValue() < value.doubleValue();
		} catch (NullPointerException | ClassCastException | NumberFormatException e) {
			return true;
		}
	}
}
