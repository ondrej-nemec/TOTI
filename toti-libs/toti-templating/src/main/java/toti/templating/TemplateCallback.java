package toti.templating;

public interface TemplateCallback {

	void call(TemplateParameters parameters, TemplateContainer data) throws Exception;
	
}
