package toti.lib.common.functions.testingClasses;

import java.util.List;

import toti.lib.common.annotations.MapperIgnored;
import toti.lib.common.annotations.MapperParameter;
import toti.lib.common.annotations.MapperType;

public class Parse {

	private String first = "first value";
	
	@MapperParameter({@MapperType(value = "second", key = "used")})
	private int second;
	
	@MapperParameter({@MapperType(value = "--list--", key = "not-used")})
	private List<String> list;
	
	@MapperIgnored
	private double ignored;
	
	@MapperParameter({@MapperType("realName")})
	private String anotherName;
	
	public Parse() {}

	public Parse(String first, int second, String anotherName) {
		this.first = first;
		this.second = second;
		this.anotherName = anotherName;
	}

	public String getFirst() {
		return first;
	}

	public int getSecond() {
		return second;
	}

	public List<String> getList() {
		return list;
	}

	public double getIgnored() {
		return ignored;
	}

	public String getAnotherName() {
		return anotherName;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public void setIgnored(double ignored) {
		this.ignored = ignored;
	}

	public void setAnotherName(String anotherName) {
		this.anotherName = anotherName;
	}

	@Override
	public String toString() {
		return "Parse [first=" + first + ", second=" + second + ", list=" + list + ", ignored=" + ignored
				+ ", anotherName=" + anotherName + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}
	
}
