package toti.lib.templating.structures;

public record TemplateFile(String moduleName, String className, String namespace, String fullClassName, long lastModification, String tempFileRelativeFolder, String templateFullPath) {

	@Override
	public String toString() {
		return String.format( 
			"TemplateFile {\n\tModule=%s\n\tClassName=%s\n\tNamespace=%s\n\tFullName=%s\n\tModificatin=%s\n\tTempFolder=%s\n\tFullPath=%s\n}",
			moduleName, className, namespace, fullClassName, lastModification, tempFileRelativeFolder, templateFullPath
		);
	}

	@Override
	// modificationTime is removed - not works in tests
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemplateFile other = (TemplateFile) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (namespace == null) {
			if (other.namespace != null)
				return false;
		} else if (!namespace.equals(other.namespace))
			return false;
		if (fullClassName == null) {
			if (other.fullClassName != null)
				return false;
		} else if (!fullClassName.equals(other.fullClassName))
			return false;
		if (tempFileRelativeFolder == null) {
			if (other.tempFileRelativeFolder != null)
				return false;
		} else if (!tempFileRelativeFolder.equals(other.tempFileRelativeFolder))
			return false;
		if (moduleName == null) {
			if (other.moduleName != null)
				return false;
		} else if (!moduleName.equals(other.moduleName))
			return false;
		if (templateFullPath == null) {
			if (other.templateFullPath != null)
				return false;
		} else if (!templateFullPath.equals(other.templateFullPath))
			return false;
		return true;
	}

}
