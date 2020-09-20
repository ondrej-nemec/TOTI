package mvc.authentication.storage;

public class NullStorage implements Storage {

	@Override
	public boolean autoRefresh() {
		return false;
	}

	@Override
	public boolean writeContentToToken() {
		return true;
	}

	@Override
	public void saveToken(String id, long expirationTime, String content) {}

	@Override
	public void deleteToken(String id) {}

	@Override
	public void updateToken(String id, long expirationTime, String content, Boolean append) {}

}
