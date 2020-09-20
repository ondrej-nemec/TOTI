package mvc.authentication.storage;

public interface Storage {

	boolean autoRefresh();
	
	boolean writeContentToToken();
	
	void saveToken(String id, long expirationTime, String content);
	
	void deleteToken(String id);
	
	void updateToken(String id, long expirationTime, String content, Boolean append);
	
}
