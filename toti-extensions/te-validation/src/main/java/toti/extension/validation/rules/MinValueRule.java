package toti.extension.validation.rules;

import java.util.function.Function;

import toti.extensions.Translator;
import toti.lib.common.structures.DictionaryValue;

public class MinValueRule extends SimpleRule<Number> {

	public MinValueRule(Number value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Number minValue, Object o) {
		try {
			Number value = new DictionaryValue(o).getNumber();
			return value == null || minValue.doubleValue() > value.doubleValue();
		} catch (NullPointerException | ClassCastException | NumberFormatException e) {
			return true;
		}
	}
}
