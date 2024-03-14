package toti.templating.parsing.structures;

public class TagParserParam {

	private final String name;
	private final String value;
	private final char quote;
	private final boolean isTag;
	
	public TagParserParam(String name) {
		this(name, null, '\u0000');
	}
	
	public TagParserParam(String name, String value, char quote) {
		if (name.startsWith("t:")) {
			this.name = name.substring(2);
			this.isTag = true;
		} else {
			this.name = name;
			this.isTag = false;
		}
		this.value = value;
		this.quote = quote;
	}
	
	public TagParserParam(String name, String value, char quote, boolean isTag) {
		this.name = name;
		this.isTag = isTag;
		this.value = value;
		this.quote = quote;
	}

	public String getName() {
		return name;
	}

	public boolean isValue() {
		return value != null;
	}
	
	public String getValue() {
		return value;
	}

	public char getQuote() {
		return quote;
	}

	public boolean isTag() {
		return isTag;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TagParserParam other = (TagParserParam) obj;
		if (isTag != other.isTag)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (quote != other.quote)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TagParserParam [name=" + name + ", value=" + value + ", quote=" + quote + ", isTag=" + isTag + "]";
	}
	
}

