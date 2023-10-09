package toti.logging;

public class FileName {

	private final String name;
	private final boolean create;
	
	public FileName(String name, boolean create) {
		this.name = name;
		this.create = create;
	}

	public String getName() {
		return name;
	}

	public boolean isCreate() {
		return create;
	}
	
	public boolean isUsed() {
		return name != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (create ? 1231 : 1237);
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
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
		FileName other = (FileName) obj;
		if (create != other.create) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "FileName [name=" + name + ", create=" + create + "]";
	}
	
}
