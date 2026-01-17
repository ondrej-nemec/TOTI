package toti.extension.validation.rules;

import java.util.Collection;
import java.util.function.Function;

import toti.application.extensions.Translator;
import toti.lib.tcpip.structures.UploadedFile;

public class FileAllowedTypesRule extends SimpleRule<Collection<Object>> {

	public FileAllowedTypesRule(Collection<Object> value, Function<Translator, String> onError) {
		super(value, onError);
	}

	@Override
	protected boolean isErrorToShow(Collection<Object> type, Object o) {
		UploadedFile file = (UploadedFile)o;
		return !type.contains(file.getFileBom());
	}
}
