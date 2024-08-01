## Nezapomenout na date z databaze

## Date from String with pattern
public Date convertTime(String time) throws ParseException{
   SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
   return format.parse(time);
}

## From LONG to DATE to String
public String convertTime(long time){
    Date date = new Date(time);
    Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    return format.format(date);
}

## LocalDateTime
LocalDateTime startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(item.getEventTime())
	.toInstant()
	.atZone(ZoneId.systemDefault())
	.toLocalDateTime();

## Calendar from string
public String parseDateTime(String dateTime, boolean date) {
	Calendar c = Calendar.getInstance();
    try {
        c.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime));
    } catch (ParseException e) {
        logger.warn("Wrong date time format: " + dateTime, e);
        return dateTime;
    }
	if (date) {
        return String.format(
			"%4d-%02d-%02d",
			c.get(Calendar.YEAR),
			c.get(Calendar.MONTH)+1, // january is 0
			c.get(Calendar.DAY_OF_MONTH)
		);
	} else {
        return String.format(
			"%02d:%02d:%02d",
			c.get(Calendar.HOUR_OF_DAY),
			c.get(Calendar.MINUTE),
			c.get(Calendar.SECOND)
		);
	}
}