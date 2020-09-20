package mvc.authentication.storage;

public class FileStorage implements Storage {

	@Override
	public boolean autoRefresh() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean writeContentToToken() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveToken(String id, long expirationTime, String content) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteToken(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateToken(String id, long expirationTime, String content, Boolean append) {
		// TODO Auto-generated method stub
		
	}

}
