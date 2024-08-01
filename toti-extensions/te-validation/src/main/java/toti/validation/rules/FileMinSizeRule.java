package toti.validation.rules;

import java.util.function.Function;

import toti.extensions.Translator;
import toti.http.structures.UploadedFile;

public class FileMinSizeRule extends SimpleRule<Integer> {

	public FileMinSizeRule(Integer value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Integer minSize, Object o) {
		UploadedFile file = (UploadedFile)o;
		return file.getContent().length < minSize;
	}
}
