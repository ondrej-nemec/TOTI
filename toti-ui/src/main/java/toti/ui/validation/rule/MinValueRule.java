package toti.ui.validation.rule;

import java.util.function.Function;

import ji.common.structures.DictionaryValue;
import ji.translator.Translator;

public class MinValueRule extends SimpleRule<Integer> {

	public MinValueRule(Integer value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Integer minValue, Object o) {
		try {
			Number value = new DictionaryValue(o).getNumber();
			return value == null || minValue.doubleValue() > value.doubleValue();
		} catch (NullPointerException | ClassCastException | NumberFormatException e) {
			return true;
		}
	}
}
