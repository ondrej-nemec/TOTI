package example.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.annotations.MapperIgnored;
import common.annotations.MapperParameter;
import toti.application.Entity;

public class Example implements Entity {

	private int id;

	private String name;
	
	private int age;
	
	@MapperParameter("active")
	private boolean isActive;
	
	private String email;
	
	private String pasw;
	
	private int range;
	
	@MapperParameter("defvalue")
	private boolean defValue;
	
	private String sex;
	
	private int parent;
	
	private int select1;
	
	@MapperParameter("simple_date")
	private String simpleDate;
	
	@MapperParameter("dt_local")
	private String datetimeLocal;
	
	private String month;
	
	private String time;
	
	private String week;
	
	private String favorite_color;
	
	private String comment;
	
	private Map<Integer, Map<String, Object>> pairs;
	
	private Map<String, String> map;
	
	private List<String> list;
	
	
	
	@MapperIgnored
	private boolean toDbSerialize;
	
	public void setToDbSerialize(boolean toDbSerialize) {
		this.toDbSerialize = toDbSerialize;
	}
	
	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> res = Entity.super.toMap();
		if (toDbSerialize) {
			res.remove("id");
			res.remove("map");
			res.remove("list");
			res.remove("pairs");
		}
		return res;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public void setPairs(Map<Integer, Map<String, Object>> pairs) {
		this.pairs = pairs;
	}

	public void setPairs(List<Map<String, Object>> pairs) {
		this.pairs = new HashMap<>();
		for (int i = 0; i < pairs.size(); i++) {
			this.pairs.put(i, pairs.get(i));
		}
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public Map<Integer, Map<String, Object>> getPairs() {
		return pairs;
	}

	public Map<String, String> getMap() {
		return map;
	}

	public List<String> getList() {
		return list;
	}

	@Override
	public String toString() {
		return "Example [id=" + id + ", name=" + name + ", age=" + age + ", isActive=" + isActive + ", email=" + email
				+ ", pasw=" + pasw + ", range=" + range + ", defValue=" + defValue + ", sex=" + sex + ", parent="
				+ parent + ", select1=" + select1 + ", simpleDate=" + simpleDate + ", datetimeLocal=" + datetimeLocal
				+ ", month=" + month + ", time=" + time + ", week=" + week + ", favorite_color=" + favorite_color
				+ ", comment=" + comment + ", pairs=" + pairs + ", map=" + map + ", list=" + list + ", toDbSerialize="
				+ toDbSerialize + "]";
	}
}
