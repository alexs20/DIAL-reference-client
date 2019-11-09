package com.wolandsoft.dial.client.rest;

public class ApplicationQueryResponce {

	private String dialVer = null;
	private String name = null;
	private Boolean allowStop = null;
	private String state = null;

	public String getDialVer() {
		return dialVer;
	}

	protected void setDialVer(String dialVer) {
		this.dialVer = dialVer;
	}

	public String getName() {
		return name;
	}

	public Boolean getAllowStop() {
		return allowStop;
	}

	public String getState() {
		return state;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected void setAllowStop(Boolean allowStop) {
		this.allowStop = allowStop;
	}

	protected void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "ApplicationQueryResponce [dialVer=" + dialVer + ", name=" + name + ", allowStop=" + allowStop + ", state=" + state + "]";
	}

}
