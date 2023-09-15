package toti.samples.grid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import ext.Entity;
import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;

public class GridExampleEntity implements Entity {

	@MapperParameter(@MapperType("id"))
	private int id;
	@MapperParameter(@MapperType("text"))
	private String text;
	@MapperParameter(@MapperType("number"))
	private double number;
	@MapperParameter(@MapperType("range"))
	private int range;
	@MapperParameter(@MapperType("select"))
	private Boolean select;
	@MapperParameter(@MapperType("datetime"))
	private LocalDateTime datetime;
	@MapperParameter(@MapperType("date"))
	private LocalDate date;
	@MapperParameter(@MapperType("time"))
	private LocalTime time;
	@MapperParameter(@MapperType("month"))
	private String month;
	@MapperParameter(@MapperType("week"))
	private String week;
	
	public GridExampleEntity(
			int id,
			String text, double number, int range, Boolean select,
			LocalDateTime datetime, LocalDate date,
			LocalTime time, String month, String week) {
		this.id = id;
		this.text = text;
		this.number = number;
		this.range = range;
		this.select = select;
		this.datetime = datetime;
		this.date = date;
		this.time = time;
		this.month = month;
		this.week = week;
	}
	
	public boolean equals(Map<String, Object> filters) {
		for (String key : filters.keySet()) {
			Object value = filters.get(key);
			if (!equals(key, value, toMap())) {
				return false;
			}
		}
		return true;
	}
	
	private boolean equals(String key, Object value, Map<String, Object> entity) {
		return entity.get(key).toString().contains(value.toString());
	}

	@Override
	public String toString() {
		return "GridExampleEntity [id=" + id + ", text=" + text + ", number=" + number + ", range=" + range
				+ ", select=" + select + ", datetime=" + datetime + ", date=" + date + ", time=" + time + ", month="
				+ month + ", week=" + week + "]";
	}
	
}
