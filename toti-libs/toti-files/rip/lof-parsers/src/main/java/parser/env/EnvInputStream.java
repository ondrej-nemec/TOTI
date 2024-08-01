package parser.env;

import java.io.InputStream;

import exceptions.ParserSyntaxException;
import parser.ParserInputStream;

public class EnvInputStream extends ParserInputStream{

	private boolean isEscaped = false;	
	private boolean onKey = true;
	private boolean isComment = false;
	private boolean isIndDouble = false;
	private boolean isInSingle = false;
	private boolean clear = false;
	
	private String value = "";
	private String key = "";
	
	public EnvInputStream(final InputStream stream) {
		super(stream);
	}
		
	public String getKey() {
		return key.trim();
	}
	
	public String getValue() {
		return value.trim();
	}
	
	@Override
	protected boolean parse(final char car) {		
		if (clear ) {
			clear = false;
			value = "";
			key = "";
		}
		
		if (car == '#')
			isComment = true;
		
		else if (car == '\n') {
			if (!key.isEmpty() && onKey)
				throw new ParserSyntaxException(".env", "Separator (=) missing.");
			if (isIndDouble || isInSingle)
				throw new ParserSyntaxException(".env", "Quotes must be closed. Current value is " + value);			
			onKey = true;
			isComment = false;
			clear = true;
			if ( ! (value.isEmpty() && key.isEmpty()))
				return false;
			
		} else if (!isComment) {
			switch(car) {
			case '"':
				if (onKey)
					throw new ParserSyntaxException(".env", "Quotes could not be in key.");
				if (isEscaped || isInSingle)
					value += "\\" + car;
				else if (!isIndDouble && !value.isEmpty())
					value += "\\" + car;
				else if (!isIndDouble)
					isIndDouble = true;
				else 
					isIndDouble = false;
				break;
			case '\'':
				if (onKey)
					throw new ParserSyntaxException(".env", "Quotes could not be in key.");
				if (isEscaped || isIndDouble)
					value += "\\" + car;
				else if (!isInSingle && !value.isEmpty())
					value += "\\" + car;
				else if (!isInSingle)
					isInSingle = true;
				else 
					isInSingle = false;
				break;
			case '\\':
				if (onKey)
					throw new ParserSyntaxException(".env", "Backslashes (\\) could not be in key.");
				if (isEscaped) {
					value += car;
					isEscaped = false;
				} else
					isEscaped = true;
				break;
			case '=':
				if (onKey && key.equals(""))
					throw new ParserSyntaxException(".env", "Key missing.");
				if (onKey) {
					onKey = false;
				} else {
					if (isEscaped)
						value += car;
					else
						throw new ParserSyntaxException(".env", "Too more separators (=).");
				}
				break;
			case '\r':
				break;
			case 'n':
				if (isEscaped && isIndDouble)
					value += "\n";
				else if (isEscaped)
					value += "\\" + car;
				else
					value += car;
				break;
			case ' ':
				if (!onKey && value.isEmpty())
					return true;
			default:
				if (onKey)
					key += car;
				else
					value += car;
			}
		}
		
		if (isEscaped && car != '\\')
			isEscaped = false;
		
		return true;
	}	
}
