package com.jge.server.net;

public enum LoginResponse {
	LOGIN_SUCCESS((byte)1),
	LOGIN_FAILED((byte)2),
	LOGIN_NOT_OK_WRONG_EMAIL((byte)3);
	
	private byte id;
	
	private LoginResponse(byte id) {
		this.id = id;
	}
	
	public byte getId() {
		return id;
	}
}
