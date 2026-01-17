package toti.common.functions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * Convert {@link TemporalAccessor} to string using given format
 * 
 * <pre>
 * String pattern = ...;
 * TemporalAccessor dataTime = ...; // LocalDate, LocalTime, LocalDateTime, ZonedDateTime
 * // from given date
 * String dateTimeFormatted = DateTime.format(pattern, dateTime);
 * // formatted string with using LocalDateTime.now()
 * String now = DateTime.format(pattern);
 * </pre>
 * 
 * @author Ondřej Němec
 *
 */
public class DateTime {
	
	/**
	 * Convert current datetime to given format
	 * 
	 * @param pattern String
	 * @return String formated datetime
	 */
	public static String format(String pattern) {
		return format(pattern, LocalDateTime.now());
	}
	
	/**
	 * Convert given datetime
	 * 
	 * @param pattern
	 * @param date {@link TemporalAccessor}
	 * @return String formated datetime
	 */
	public static String format(String pattern, TemporalAccessor date) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		return format.format(date);// date.format(format);
	}
	
}
