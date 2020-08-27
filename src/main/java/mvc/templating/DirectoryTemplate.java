package mvc.templating;

import java.io.File;
import java.util.Map;

import translator.Translator;

public class DirectoryTemplate implements Template {
	
	private final File[] files;
	
	private final String path;
	
	public DirectoryTemplate(File[] files, String path) {
		this.files = files;
		this.path = path;
	}

	@Override
	public long getLastModification() {
		return 0;
	}

	@Override
	public String create(TemplateFactory templateFactory, Map<String, Object> variables, Translator translator)
			throws Exception {
		StringBuilder builder = new StringBuilder();
		
		builder.append("Folder: <br>");
		for(File file : files) {
			// TODO icon
			builder.append(String.format("<a href='%s'>%s</a>", path + file.getName(), file.getName()));			
			builder.append("<br>");
		}
		
		return builder.toString();
	}

}