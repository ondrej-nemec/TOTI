package toti.ui.validation.rules;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;
import toti.ui.validation.rule.FileAllowedTypesRule;
import toti.ui.validation.rule.FileMaxSizeRule;
import toti.ui.validation.rule.FileMinSizeRule;

public class FileRules {
		
	private Optional<Integer> fileMaxSize = Optional.empty();
	private Function<Translator, String> onFileMaxSizeError = (t)->"";
	private Optional<Integer> fileMinSize = Optional.empty();
	private Function<Translator, String> onFileMinSizeError = (t)->"";
	private Optional<Collection<Object>> allowedFileTypes = Optional.empty();
	private Function<Translator, String> onAllowedFileTypesError = (t)->"";

	public FileRules setFileMaxSize(Integer fileMaxSize) {
		return setFileMaxSize(fileMaxSize, (t)->t.translate(
			"toti.validation.file-size-can-be-max", 
			new MapInit<String, Object>().append("fileMaxSize", fileMaxSize).toMap()
		)); // "File size can be max " + fileMaxSize + "b"
	}

	public FileRules setFileMaxSize(Integer fileMaxSize, Function<Translator, String> onFileMaxSizeError) {
		if (this.fileMaxSize.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		/*this.fileMaxSize = Optional.of(fileMaxSize);
		this.onFileMaxSizeError = onFileMaxSizeError;
		*/
		new FileMaxSizeRule(fileMaxSize, onFileMaxSizeError);
		return this;
	}

	public FileRules setFileMinSize(Integer fileMinSize) {
		return setFileMinSize(fileMinSize, (t)->t.translate(
			"toti.validation.file-size-must-be-at-least", 
			new MapInit<String, Object>().append("fileMinSize", fileMinSize).toMap()
		)); // "File size must be at least " + fileMinSize + "b"
	}

	public FileRules setFileMinSize(Integer fileMinSize, Function<Translator, String> onFileMinSizeError) {
		if (this.fileMinSize.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		/*this.fileMinSize = Optional.of(fileMinSize);
		this.onFileMinSizeError = onFileMinSizeError;*/
		new FileMinSizeRule(fileMinSize, onFileMinSizeError);
		return this;
	}

	public FileRules setAllowedFileTypes(Collection<Object> allowedFileTypes) {
		return setAllowedFileTypes(allowedFileTypes, (t)->t.translate(
			"toti.validation.file-type-is-not-allowed", 
			new MapInit<String, Object>().append("allowedFileTypes", allowedFileTypes).toMap()
		)); // "File type is not allowed. Allowed: " + allowedFileTypes
	}

	public FileRules setAllowedFileTypes(Collection<Object> allowedFileTypes, Function<Translator, String> onAllowedFileTypesError) {
		if (this.allowedFileTypes.isPresent()) {
			throw new LogicException("You cannot set an already set value");
		}
		/*this.allowedFileTypes = Optional.of(allowedFileTypes);
		this.onAllowedFileTypesError = onAllowedFileTypesError;*/
		new FileAllowedTypesRule(allowedFileTypes, onAllowedFileTypesError);
		return this;
	}
	
}
