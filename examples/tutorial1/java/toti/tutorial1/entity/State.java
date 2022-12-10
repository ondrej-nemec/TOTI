package toti.tutorial1.entity;

import ji.common.annotations.MapperIgnored;
import ji.common.annotations.MapperParameter;
import ji.common.annotations.MapperType;
import toti.application.Entity;

public class State implements Entity {
	
	@MapperIgnored("database")
	private Integer id;
	@MapperParameter({@MapperType("device_id")})
	private int deviceId;
	@MapperParameter({@MapperType("is_connected")})
	private boolean isConnected;
	@MapperParameter({@MapperType("detail")})
	private String detail;
	
	public State() {}
	
	public State(int deviceId, boolean isConnected, String detail) {
		this.deviceId = deviceId;
		this.isConnected = isConnected;
		this.detail = detail;
	}

}
