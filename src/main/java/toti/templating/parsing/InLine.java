package toti.templating.parsing;

public class InLine {

	private String pre = "";
	private StringBuilder content = new StringBuilder();
	
	public InLine() {}

	public String getPre() {
		return pre;
	}

	public StringBuilder getContent() {
		return content;
	}

	public void addVariable(VariableParser var) {
		this.pre = var.getDeclare();
		addContent(var.getCalling());
	}

	public void addContent(Object data) {
		content.append(data);
	}
	
}
