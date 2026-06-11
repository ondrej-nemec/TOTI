package toti.extension.validation.rules;

import java.util.Collection;
import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;
import toti.lib.tcpip.structures.UploadedFile;

public class FileAllowedTypesRule implements Rule {

	private final Collection<Object> types;
	private final Supplier<String> onError;

	public FileAllowedTypesRule(Collection<Object> value, Supplier<String> onError) {
		this.types = value;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		UploadedFile file = (UploadedFile)item.getRawValue();
		if (file != null && !types.contains(file.getFileBom())) {
			item.addError(onError.get());
		}
		return new CheckResult(true);
	}

}
