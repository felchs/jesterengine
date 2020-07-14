package com.jge.server.test;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Properties;

import com.jge.server.net.AppContext;
import com.jge.server.net.AppListener;
import com.jge.server.net.ClientSessionListener;
import com.jge.server.net.LoginResponse;
import com.jge.server.net.LogoutReason;
import com.jge.server.net.session.ClientSession;

public class InitializerTest implements AppListener, Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * {@inheritDoc}
	 */
	public void initialize(Properties props) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	public ClientSessionListener loggedIn(ClientSession session) {
		new ClientSessionListener() {
			
			public void receivedMessage(ByteBuffer message) {
			}
			
			public void disconnected(boolean graceful) {
			}
			
			public void reconnect() {				
			}
		};
		return null;
	}
	
	public void loggedOut(ClientSession session, LogoutReason reason) {		
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void logginFailure(ClientSession session) {		
	}
	
	/**
	 * {@inheritDoc}
	 * @return 
	 */
	public LoginResponse authenticateOnLogin(ClientSession session, ByteBuffer message) {
		return LoginResponse.LOGIN_SUCCESS;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) {
		InitializerTest initializer = new InitializerTest();
		AppContext.get().init(initializer);
	}
}