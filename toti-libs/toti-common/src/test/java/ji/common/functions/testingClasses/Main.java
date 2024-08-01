package ji.common.functions.testingClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ji.common.structures.MapInit;

public class Main {
	private double first;
	private String second;
	private List<Object> list; 
	private Map<String, Object> map;
	private Sub sub1;
	private ArrayList<Sub> subs;
	
	public Main() {
		this.list = new ArrayList<>();
		this.map = new HashMap<>();
	}
	
	public Main(boolean init) {
		this.first = 420;
		this.second = "Hello World!";
		this.sub1 = new Sub();
		this.list = Arrays.asList(
			"aaa",
			95/*,
			new Sub()*/
		);
		this.subs = new ArrayList<>();
		subs.add(new Sub());
		this.map = new MapInit<String, Object>()
			.append("item1", 123)
			.append("item2", true)
			.toMap();
	}
	
	@Override 
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}
		
	@Override
	public String toString() {
		return "Main [first=" + first + ", second=" + second + ", list=" + list + ", map=" + map + ", sub1=" + sub1
				+ ", subs=" + subs + "]";
	}

	public void setFirst(double first) {
		this.first = first * 10;
	}
	
	/*
	public void putMap(String key, Object value) {
		map.put(key, value);
	}
	
	public void addList(Object o) {
		list.add(o);
	}
	
	public void addSubs(Sub sub) {
		subs.add(sub);
	}
	*/
}
