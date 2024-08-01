package ji.migration.resources;

import ji.common.functions.FileExtension;

public class IdSeparator {
	
	private final String id;
	
	private final String desc;
	
	public IdSeparator(String name, String separator) {
		if (!name.contains(separator)) {
			throw new RuntimeException("File name is in incorrect format: " + name + ", separator is required: " + separator);
		}
		String[] aux = new FileExtension(name).getName().split(separator);
		if (aux.length == 2) {
			this.id = aux[0];
			this.desc = aux[1];
		} else {
			throw new RuntimeException(
				"File name is in incorrect format: " + name + ", required format: "
				+ String.format("[<module>%s]<id>%s<description>", separator, separator)
			);
		}
		
	}

	public String getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}
	
}
