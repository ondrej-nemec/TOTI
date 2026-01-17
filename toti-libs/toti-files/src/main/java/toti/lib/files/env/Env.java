package toti.lib.files.env;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import toti.lib.common.functions.FileExtension;
import toti.lib.common.structures.dictionary.Scalar;
import toti.lib.common.structures.dictionary.ScalarStructure;

public class Env implements ScalarStructure<Object> {

	private final Map<Object, Value> data;

	public static Env empty() {
		return new Env(new HashMap<>());
	}

	public static Env load(String fileName) throws IOException {
		FileExtension ext = new FileExtension(fileName);
		Map<Object, Value> result = switch (ext.getExtension()) {
			case "json" -> JsonEnvSource.parse(fileName);
			case "xml" -> XmlEnvSource.parse(fileName);
			case "properties" -> PropertiesEnvSource.parse(fileName);
			default -> throw new RuntimeException("Unsupported extension type: " + ext.getExtension());
		};
		return new Env(result);
	}

	protected Env(Map<Object, Value> data) {
		this.data = data;
	}

	public boolean hasSection(String name) {
		Value value = data.get(name);
		return value != null && value.isSection();
	}

	public Env getSection(Object name) {
		Value value = data.get(name);
		if (value == null || !value.isSection()) {
			throw new RuntimeException("'" + name + "' is not a section");
		}
		return value.getSection();
	}

	public <T> List<T> getList(String name, Function<Scalar, T> process) {
		List<T> result = new LinkedList<>();
		getList(name).forEach(v->{
			result.add(process.apply(()->v.getValue()));
		});
		return result;
	}

	public List<Value> getList(String name) {
		Value value = data.get(name);
		if (value == null || value.isValue()) {
			throw new RuntimeException("'" + name + "' is null or value");
		}
		if (value.isSection()) {
			return Arrays.asList(value);
		}
		return value.getList();
	}

	public void iterate(BiConsumer<String, Value> callback) {
		data.forEach((key, val)->{
			callback.accept(key.toString(), val);
		});
	}

	@Override
	public Object getValue(Object name) {
		Value value = data.get(name);
		if (value == null) {
			return null;
		}
		if (!value.isValue()) {
			throw new RuntimeException("'" + name + "' is not a value");
		}
		return value.getValue();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ENV {");
		data.forEach((n, v)->{
			result.append(String.format("\n\t%s: %s", n, v));
		});
		return result.toString();
	}

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.data);
        return hash;
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
        final Env other = (Env) obj;
        return Objects.equals(this.data, other.data);
    }
	
}
