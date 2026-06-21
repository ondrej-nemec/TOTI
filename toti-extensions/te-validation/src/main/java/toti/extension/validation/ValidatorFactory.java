package toti.extension.validation;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.extension.validation.collections.RulesCollection;
import toti.extension.validation.collections.factory.DefaultRulesCollectionsFactory;
import toti.lib.common.structures.MapInit;

public class ValidatorFactory {

	private final Translator translator;

	public ValidatorFactory(Translator translator) {
		this.translator = translator;
	}

	public Validator create(boolean strictList) {
		return create(strictList, params->translator.translate(
			"toti.validation.not-expected-parameters",
			new MapInit<String, Object>().append("parameters", params).toMap()
		));
	}

	public Validator create(boolean strictList, Function<List<String>, String> onStrictListError) {
		return new Validator(strictList, Optional.empty(), onStrictListError, translator);
	}

	public Validator create(Function<DefaultRulesCollectionsFactory, RulesCollection> defaultRule) {
		return create(defaultRule, params->translator.translate(
			"toti.validation.parameter-not-match-default-rule",
			new MapInit<String, Object>().append("parameter", params).toMap()
		));
	}

	public Validator create(Function<DefaultRulesCollectionsFactory, RulesCollection> defaultRule, Function<List<String>, String> onStrictListError) {
		return new Validator(false, Optional.of(defaultRule), onStrictListError, translator);
	}
}
