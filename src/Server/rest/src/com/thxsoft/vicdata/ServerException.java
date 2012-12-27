package com.thxsoft.vicdata;

public class ServerException extends Exception {
	private static final long serialVersionUID = -4858292904375131342L;

	public ServerException(String json) {
		super(json);
	}
}
