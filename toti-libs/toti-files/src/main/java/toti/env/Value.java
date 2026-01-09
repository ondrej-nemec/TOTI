package toti.env;

import java.util.List;
import java.util.Map;

public class Value {

    enum Mode {
        VALUE, OBJECT, LIST
    }

	private Object value;
	private Map<Object, Value> section;
    private List<Value> list;
    private final Mode mode;

    public Value(Object value) {
        this.value = value;
        this.mode = Mode.VALUE;
    }

    public Value(Map<Object, Value> section) {
        this.section = section;
        this.mode = Mode.OBJECT;
    }

    public Value(List<Value> list) {
        this.list = list;
        this.mode = Mode.LIST;
    }

    public List<Value> getList() {
        return list;
    }

    public Env getSection() {
        return new Env(section);
    }

    public Map<Object, Value> getMap() {
        return section;
    }

    public Object getValue() {
        return value;
    }

    public boolean isSection() {
        return mode == Mode.OBJECT;
    }

    public boolean isList() {
        return mode == Mode.LIST;
    }

    public boolean isValue() {
        return mode == Mode.VALUE;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((section == null) ? 0 : section.hashCode());
        result = prime * result + ((list == null) ? 0 : list.hashCode());
        result = prime * result + ((mode == null) ? 0 : mode.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Value other = (Value) obj;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (section == null) {
            if (other.section != null)
                return false;
        } else if (!section.equals(other.section))
            return false;
        if (list == null) {
            if (other.list != null)
                return false;
        } else if (!list.equals(other.list))
            return false;
        if (mode != other.mode)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return switch (mode) {
            case LIST -> "Value (List) " + list;
            case OBJECT -> "Value (Section) " + section;
            case VALUE -> "Value (Val) {" + value + "}";
            default -> "Value (NULL)";
        };
    }

    public String toString(String prefix) {
        StringBuilder res = new StringBuilder();
        switch (mode) {
            case LIST -> {
                res.append("[");
                list.forEach(v->{
                    res.append(String.format("\n%s  %s", prefix, v.toString(prefix + "  ")));
                });
                res.append(String.format("\n%s]", prefix));
            }
            case OBJECT -> {
                res.append("{");
                section.forEach((k, v)->{
                    res.append(String.format("\n%s  %s: %s", prefix, k, v.toString(prefix + "  ")));
                });
                res.append(String.format("\n%s}", prefix));
            }
            case VALUE -> {
                res.append(value.toString());
            }
            default -> {}
        };
        return res.toString();
    }

}
