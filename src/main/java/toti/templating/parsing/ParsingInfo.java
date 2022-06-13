package toti.templating.parsing;

public class ParsingInfo {
	
	private final String filename;
    private final String moduleName;
    private String filePath;
    private int line = 1;

    public ParsingInfo(String moduleName, String filename) {
        this.moduleName = moduleName;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getLine() {
        return line;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void addLine() {
        this.line++;
    }

    @Override
    public String toString() {
        return String.format(" in %s %s (%s) at line %s.", moduleName, filename, filePath, line);
    }
    
}
