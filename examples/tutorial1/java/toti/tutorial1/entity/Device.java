package toti.tutorial1.entity;

import ji.common.annotations.MapperIgnored;
import toti.application.Entity;

public class Device implements Entity {

	@MapperIgnored(value="database")
	private Integer id;
	
	private String name;
	
}
