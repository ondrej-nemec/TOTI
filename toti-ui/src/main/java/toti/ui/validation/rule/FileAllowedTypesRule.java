package toti.ui.validation.rule;

import java.util.Collection;
import java.util.function.Function;

import ji.socketCommunication.http.structures.UploadedFile;
import ji.translator.Translator;

public class FileAllowedTypesRule extends SimpleRule<Collection<Object>> {

	public FileAllowedTypesRule(Collection<Object> value, Function<Translator, String> onError) {
		super(value, onError);
	}
	// Collection<Object>

	@Override
	protected boolean isErrorToShow(Collection<Object> type, Object o) {
		UploadedFile file = (UploadedFile)o;
		return !type.contains(file.getFileBom());
	}
}
