package toti.extension.validation.rules;

import java.util.function.Supplier;

import toti.extension.validation.results.CheckResult;
import toti.extension.validation.results.ValidationItem;
import toti.lib.tcpip.structures.UploadedFile;

public class FileMinSizeRule implements Rule {

	private final Integer minSize;
	private final Supplier<String> onError;

	public FileMinSizeRule(Integer minSize, Supplier<String> onError) {
		this.minSize = minSize;
		this.onError = onError;
	}

	@Override
	public CheckResult check(ValidationItem item) {
		UploadedFile file = (UploadedFile)item.getRawValue();
		if (file != null && file.getContent().length < minSize) {
			item.addError(onError.get());
		}
		return new CheckResult(true);
	}

}
