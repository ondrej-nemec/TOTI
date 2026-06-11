package toti.extension.validation.rules;

import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;
import toti.lib.tcpip.structures.UploadedFile;

public class FileMaxSizeRule implements Rule {

	private final Integer maxSize;
	private final Supplier<String> onError;

	public FileMaxSizeRule(Integer maxSize, Supplier<String> onError) {
		this.maxSize = maxSize;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		UploadedFile file = (UploadedFile)item.getRawValue();
		if (file != null && file.getContent().length > maxSize) {
			item.addError(onError.get());
		}
		return new CheckResult(true);
	}

}
