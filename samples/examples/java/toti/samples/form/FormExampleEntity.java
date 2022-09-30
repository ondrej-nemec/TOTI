package toti.samples.form;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;
import toti.application.Entity;

public class FormExampleEntity implements Entity {

	@MapperParameter(@MapperType("text"))
	private String text;
	@MapperParameter(@MapperType("textarea"))
	private String textArea;
	@MapperParameter(@MapperType("email"))
	private String email;
	@MapperParameter(@MapperType("color"))
	private String color;
	@MapperParameter(@MapperType("checkbox"))
	private Boolean checkBox;
	@MapperParameter(@MapperType("number"))
	private Double number;
	@MapperParameter(@MapperType("password"))
	private String password;
	@MapperParameter(@MapperType("radiolist"))
	private FormEnum radioList;
	@MapperParameter(@MapperType("range"))
	private Double range;
	@MapperParameter(@MapperType("select"))
	private Object select;
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
	
	public FormExampleEntity() {}

	public FormExampleEntity(
			String text, String textArea, 
			String email, String color, Boolean checkBox,
			Double number, String password, FormEnum radioList,
			Double range, Object select, LocalDateTime datetime,
			LocalDate date, LocalTime time, String month, String week) {
		this.text = text;
		this.textArea = textArea;
		this.email = email;
		this.color = color;
		this.checkBox = checkBox;
		this.number = number;
		this.password = password;
		this.radioList = radioList;
		this.range = range;
		this.select = select;
		this.datetime = datetime;
		this.date = date;
		this.time = time;
		this.month = month;
		this.week = week;
	}
	
}
