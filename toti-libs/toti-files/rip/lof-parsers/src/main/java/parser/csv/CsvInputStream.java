package parser.csv;

import java.io.InputStream;

import exceptions.ParserSyntaxException;
import parser.ParserInputStream;

public class CsvInputStream extends ParserInputStream{
	
	private final char separator;

	private char previousChar = '\u0000';
	private boolean isInQuots = false;
	private int countOfQuots = 0;
	
	private int line = 0;
	private String value = "";
		
	public CsvInputStream(final InputStream stream) {
		super(stream);
		this.separator = ',';
	}	
		
	public CsvInputStream(final InputStream stream, final char separator) {
		super(stream);
		this.separator = separator;
	}
	
	public String getValue() {
		return value;
	}

	public int getLine() {
		return line;
	}	
	
	@Override
	protected boolean parse(final char car) {
		// if string start with " and not stopped before file end, this return true
		boolean result = true;
		
		if (previousChar == '\n' && !isInQuots) {
			line++;
			value = "";
		} else if (previousChar == separator && !isInQuots) {
			value = "";
		}
		
		if (car != '"' && countOfQuots > 0) {
			if (!isInQuots && countOfQuots == 1 && value.isEmpty()) // start quot
				isInQuots = true;
			else if (isInQuots && countOfQuots %2 == 1) // end quot
				isInQuots = false;
			else if (countOfQuots % 2 == 0) {}// ignoring				
			else
				throw new ParserSyntaxException(
						"CSV",
						"You have syntax problem with double quots on line " 
						+ line + " near " + previousChar + car
				);
			countOfQuots = 0;
		}
		
		if (car == separator) {
			if (!isInQuots) {
				result = false;
			} else {
				value += car;
			}	
		} else {
			switch (car) {
			case '"':
				countOfQuots++;
				if (previousChar == '"') {
					value += car;
				}
				break;
			case '\n':
				if (isInQuots) {
					value += car;
				} else {
					result = false;
				}
				break;
			case '\r':
				if (isInQuots)
					value += car;
				break;
			default:
				value += car;
			}
		}		
		
		if (car == '"' && previousChar == '"')
			previousChar = '\u0000';
		else if ( ! (!isInQuots && car == '\r') ) 
			previousChar = car;
		
		return result;
	}

}
