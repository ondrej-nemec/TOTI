package toti.extension.validation.collections;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import toti.core.extensions.Translator;
import toti.extension.validation.rules.FileAllowedTypesRule;
import toti.extension.validation.rules.FileMaxSizeRule;
import toti.extension.validation.rules.FileMinSizeRule;
import toti.extension.validation.rules.Rule;
import toti.lib.common.exceptions.LogicException;
import toti.lib.common.structures.MapInit;
import toti.lib.tcpip.structures.UploadedFile;

public class FileRules extends AbstractBaseRules<FileRules> {

	private FileMaxSizeRule maxSizeRule;
	private FileMinSizeRule minSizeRule;
	private FileAllowedTypesRule allowedTypesRule;
	
	public FileRules(String name, boolean required, Function<String, String> onRequiredError, Translator translator) {
		super(name, required, onRequiredError, translator);
		_setType(UploadedFile.class);
	}
	
	public FileRules setFileMaxSize(Integer fileMaxSize) {
		return setFileMaxSize(fileMaxSize, ()->translator.translate(
			"toti.validation.file-size-can-be-max", 
			new MapInit<String, Object>().append("fileMaxSize", fileMaxSize).toMap()
		)); // "File size can be max " + fileMaxSize + "b"
	}

	public FileRules setFileMaxSize(Integer fileMaxSize, Supplier<String> onFileMaxSizeError) {
		if (this.maxSizeRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.maxSizeRule = new FileMaxSizeRule(fileMaxSize, onFileMaxSizeError);
		return this;
	}

	public FileRules setFileMinSize(Integer fileMinSize) {
		return setFileMinSize(fileMinSize, ()->translator.translate(
			"toti.validation.file-size-must-be-at-least", 
			new MapInit<String, Object>().append("fileMinSize", fileMinSize).toMap()
		)); // "File size must be at least " + fileMinSize + "b"
	}

	public FileRules setFileMinSize(Integer fileMinSize, Supplier<String> onFileMinSizeError) {
		if (this.minSizeRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.minSizeRule = new FileMinSizeRule(fileMinSize, onFileMinSizeError);
		return this;
	}

	public FileRules setAllowedFileTypes(Collection<Object> allowedFileTypes) {
		return setAllowedFileTypes(allowedFileTypes, ()->translator.translate(
			"toti.validation.file-type-is-not-allowed", 
			new MapInit<String, Object>().append("allowedFileTypes", allowedFileTypes).toMap()
		)); // "File type is not allowed. Allowed: " + allowedFileTypes
	}

	public FileRules setAllowedFileTypes(Collection<Object> allowedFileTypes, Supplier<String> onAllowedFileTypesError) {
		if (this.allowedTypesRule != null) {
			throw new LogicException("You cannot set an already set value");
		}
		this.allowedTypesRule = new FileAllowedTypesRule(allowedFileTypes, onAllowedFileTypesError);
		return this;
	}

	@Override
	protected FileRules getThis() {
		return this;
	}
	
	@Override
	public List<Rule> getRules() {
		List<Rule> rules = super.getRules();
		if (maxSizeRule != null) {
			rules.add(maxSizeRule);
		}
		if (minSizeRule != null) {
			rules.add(minSizeRule);
		}
		if (allowedTypesRule != null) {
			rules.add(allowedTypesRule);
		}
		return rules;
	}
	
}
