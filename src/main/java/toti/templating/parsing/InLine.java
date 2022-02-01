package toti.templating.parsing;

public class InLine {

	private StringBuilder content = new StringBuilder();
	
	public InLine() {}

	public StringBuilder getContent() {
		return content;
	}

	public void addVariable(VariableParser var) {
		addContent(var.getCalling());
	}

	public void addContent(Object data) {
		content.append(data);
	}
	
}
