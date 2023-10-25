package toti.ui.validation.rule;

import java.util.function.Function;

import ji.socketCommunication.http.structures.UploadedFile;
import ji.translator.Translator;

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
