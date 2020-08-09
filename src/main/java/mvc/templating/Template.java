package mvc.templating;

public interface Template {

	long getLastModification();
	
	String create() throws Exception;
	
}
