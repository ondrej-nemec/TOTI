package mvc.authentication.storage;

import java.util.HashMap;
import java.util.Map;

import common.structures.Tuple2;

public class MemoryStorage implements Storage {

	private final Map<String, Tuple2<String, Long>> storage;
	private final boolean autoRefresh;
	
	public MemoryStorage(boolean autoRefresh) {
		this.storage = new HashMap<>();
		this.autoRefresh = autoRefresh;
	}
	
	@Override
	public boolean autoRefresh() {
		return autoRefresh;
	}

	@Override
	public boolean writeContentToToken() {
		return false;
	}

	@Override
	public void saveToken(String id, long expirationTime, String content) {
		storage.put(id, new Tuple2<>(content, expirationTime));
	}

	@Override
	public void deleteToken(String id) {
		storage.remove(id);
	}

	@Override
	public void updateToken(String id, long expirationTime, String content, Boolean append) {
		String origin = storage.get(id)._1();
		if (append == null) {
			storage.put(id, new Tuple2<>(origin, expirationTime));
		} else {
			storage.put(id, new Tuple2<>((append ? origin + content : content), expirationTime));
		}
	}

}
