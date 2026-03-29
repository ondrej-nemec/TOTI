package toti.lib.templating.parsing;

public class ParsingInfo {
	
	private final String filename;
    private String filePath;
    private int line = 1;

    public ParsingInfo(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
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
        return String.format(" in %s (%s) at line %s.", filename, filePath, line);
    }
    
}
