package toti.validation.rules;

import java.util.function.Function;

import toti.extensions.Translator;
import toti.http.UploadedFile;

public class FileMaxSizeRule extends SimpleRule<Integer> {

	public FileMaxSizeRule(Integer value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Integer maxSize, Object o) {
		UploadedFile file = (UploadedFile)o;
		return file.getContent().length > maxSize;
	}

}
