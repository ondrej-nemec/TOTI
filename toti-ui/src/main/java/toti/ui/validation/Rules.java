package toti.ui.validation;

import java.util.function.BiFunction;

import ji.translator.Translator;
import toti.ui.validation.collections.AlphaNumbericRules;
import toti.ui.validation.collections.BaseRules;
import toti.ui.validation.collections.NumberRules;
import toti.ui.validation.collections.StructureRules;

public class Rules {
	
	// typ
	// struktura

	public Rules(String name, Boolean required, BiFunction<Translator, String, String> onRequiredError) {
		// TODO Auto-generated constructor stub
	}
	
	public BaseRules setBooleanType() {
		return null;
	}
	
	public NumberRules setNumberType(Class<? extends Number> clazz) {
		return null;
	}
	
	public AlphaNumbericRules setType(Class<?> clazz) {
		return null;
	}
	
	public StructureRules setMapSpecification(Validator validator) {
		return null;
	}
	
	public StructureRules setListSpecification(Validator validator) {
		return null;
	}
	
	public StructureRules setSortedMapSpecification(Validator validator) {
		return null;
	}
	
}
