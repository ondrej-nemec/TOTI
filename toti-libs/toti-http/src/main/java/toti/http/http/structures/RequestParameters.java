package toti.http.http.structures;

import java.util.HashMap;
import java.util.Map;

import ji.common.structures.MapDictionary;
import toti.http.http.structures.RequestParameters;
import toti.http.http.structures.UploadedFile;

public class RequestParameters extends MapDictionary<String> {
	
	public RequestParameters() {
		super(new HashMap<>());
	}
	
	public RequestParameters(Map<String, Object> map) {
		super(map);
	}
	
	public UploadedFile getUploadedFile(String key) {
		return UploadedFile.class.cast(getValue(key));
	}
	
	@Override
    public RequestParameters put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    @Override
    public RequestParameters putAll(Map<String, Object> values) {
        super.putAll(values);
        return this;
    }
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public String toString() {
		return String.format("RequestParameters [%s]", super.toString());
	}
	
}
