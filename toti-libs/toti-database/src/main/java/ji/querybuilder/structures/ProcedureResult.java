package ji.querybuilder.structures;

import java.util.Objects;

import toti.lib.common.structures.DictionaryValue;
import toti.lib.common.structures.MapDictionary;

public class ProcedureResult {

	private final Object callResult;
	private final MapDictionary<String> outputs;
	
	public ProcedureResult(Object callResult) {
		this.callResult = callResult;
		this.outputs = MapDictionary.hashMap();
	}
	
	public void addOutput(String name, Object value) {
		this.outputs.put(name, value);
	}
	
	public Object getCallResult() {
		return callResult;
	}
	
	public DictionaryValue getOutput(String name) {
		return outputs.getDictionaryValue(name);
	}

	@Override
	public String toString() {
		return "ProcedureResult [callResult=" + callResult + ", outputs=" + outputs + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(callResult, outputs);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProcedureResult other = (ProcedureResult) obj;
		return Objects.equals(callResult, other.callResult) && Objects.equals(outputs, other.outputs);
	}

}
