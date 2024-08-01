package ji.common.functions;

/**
 * Splits file name to name and extension
 * 
 * @author Ondřej Němec
 *
 */
public class FileExtension {
	
	private final String name;
	
	private final String extension;
	
	/**
	 * Create new instance with file path to parse
	 * 
	 * @param filePath String file path to parse
	 */
	public FileExtension(String filePath) {
		int i = filePath.lastIndexOf('.');
		
		if (i == -1) {
			this.name = filePath;
			this.extension = "";
		} else if (i == 0) {
			this.name = "";
			this.extension = filePath.substring(i+1);
		} else if (i > 0) {
			this.name = filePath.substring(0, i);
			this.extension = filePath.substring(i+1);
		} else {
			this.name = "";
			this.extension = "";
		}
	}
	
	/**
	 * Returns name of given file without extension
	 * 
	 * @return String name of file or empty String if name is missing (like .gitignore)
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns file extension.
	 * 
	 * @return String extension of emptry String if extension is missing
	 */
	public String getExtension() {
		return extension;
	}

}
