package com.wolandsoft.dial.client.rest;

public class RestException extends Exception {

	private static final long serialVersionUID = 1L;
	private String responseMessage;
	private int responseCode;

	public RestException(int responseCode, String responseMessage) {
		super();
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

}
