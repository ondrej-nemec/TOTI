package toti.lib.templating;

public interface TemplateParameters {
	
	void addVariable(String name, Object value);
	
	Object getVariable(String name);
			
}
