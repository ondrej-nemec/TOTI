package toti.ui.validation.collections;

import java.util.Collection;
import java.util.function.Function;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.translator.Translator;
import toti.ui.validation.rules.FileAllowedTypesRule;
import toti.ui.validation.rules.FileMaxSizeRule;
import toti.ui.validation.rules.FileMinSizeRule;

public class FileRules {
	
	private FileMaxSizeRule maxSizeRule;
	private FileMinSizeRule minSizeRule;
	private FileAllowedTypesRule allowedTypesRule;
	
	public FileRules setFileMaxSize(Integer fileMaxSize) {
		return setFileMaxSize(fileMaxSize, (t)->t.translate(
			"toti.validation.file-size-can-be-max", 
			new MapInit<String, Object>().append("fileMaxSize", fileMaxSize).toMap()
		)); // "File size can be max " + fileMaxSize + "b"
	}

	public FileRules setFileMaxSize(Integer fileMaxSize, Function<Translator, String> onFileMaxSizeError) {
		if (this.maxSizeRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.maxSizeRule = new FileMaxSizeRule(fileMaxSize, onFileMaxSizeError);
		return this;
	}

	public FileRules setFileMinSize(Integer fileMinSize) {
		return setFileMinSize(fileMinSize, (t)->t.translate(
			"toti.validation.file-size-must-be-at-least", 
			new MapInit<String, Object>().append("fileMinSize", fileMinSize).toMap()
		)); // "File size must be at least " + fileMinSize + "b"
	}

	public FileRules setFileMinSize(Integer fileMinSize, Function<Translator, String> onFileMinSizeError) {
		if (this.minSizeRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.minSizeRule = new FileMinSizeRule(fileMinSize, onFileMinSizeError);
		return this;
	}

	public FileRules setAllowedFileTypes(Collection<Object> allowedFileTypes) {
		return setAllowedFileTypes(allowedFileTypes, (t)->t.translate(
			"toti.validation.file-type-is-not-allowed", 
			new MapInit<String, Object>().append("allowedFileTypes", allowedFileTypes).toMap()
		)); // "File type is not allowed. Allowed: " + allowedFileTypes
	}

	public FileRules setAllowedFileTypes(Collection<Object> allowedFileTypes, Function<Translator, String> onAllowedFileTypesError) {
		if (this.allowedTypesRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.allowedTypesRule = new FileAllowedTypesRule(allowedFileTypes, onAllowedFileTypesError);
		return this;
	}
	
}
