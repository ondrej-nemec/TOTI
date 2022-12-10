package toti.tutorial1.entity;

import ji.common.annotations.MapperIgnored;
import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;
import toti.application.Entity;

public class Device implements Entity {

	@MapperIgnored(value="database")
	private Integer id;
	@MapperParameter(@MapperType("name"))
	private String name;
	@MapperParameter(@MapperType("ip"))
	private String ip;
	@MapperParameter(@MapperType("is_running"))
	private boolean isRunning;
	
	public Integer getId() {
		return id;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
}
