/*
 * Jester Game Engine is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation and
 * distributed hereunder to you.
 *
 * Jester Game Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author: orochimaster
 * @email: orochimaster@yahoo.com.br
 */
package com.jge.server.net;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

import com.jge.server.net.session.ClientSession;

/**
 * Net processor extends {@link SessionMessageReceiver} it's the entry point for 
 * {@link ClientSession}'s messages.
 * @see SessionMessageReceiver check the incoming {@link ClientSession}'s messages
 */
public class NetProcessor implements SessionMessageReceiver {
	/**
	 * When receiving the messages it processes and send it to the application listener {@link AppListener}
	 */
	private AppListener appListener;
	
	/**
	 * When receiving messages they must be routed to the given {@link ClientSessionListener}
	 */
	private ConcurrentHashMap<String, ClientSessionListener> clientSessionListenerMap = new ConcurrentHashMap<String, ClientSessionListener>();

	/**
	 * Constructor passing parameter
	 * @param appListener the application listener
	 */
	public NetProcessor(AppListener appListener) {
		this.appListener = appListener;
	}
	
	/**
	 * Gets the {@link ClientSessionListener} with {@link ClientSession}
	 * @param session the {@link ClientSession} to retrieve his listener
	 * @return the {@link ClientSessionListener} with {@link ClientSession}
	 */
	public ClientSessionListener getClientSessionListener(ClientSession session) {
		ClientSessionListener clientSessionListener = clientSessionListenerMap.get(session.getHost());
		return clientSessionListener;
	}
	
	/**
	 * It maps a {@link ClientSessionListener} with a {@link ClientSession}
	 * @param session the {@link ClientSession} to be set as key on mapping
	 * @param incomingClientSessionListener the {@link ClientSessionListener} to be mapped
	 */
	private void mapClientSessionListener(ClientSession session, ClientSessionListener incomingClientSessionListener) {
		ClientSessionListener clientSessionListener = getClientSessionListener(session);
		if (clientSessionListener != null) {
			removeClientSessionListener(session, clientSessionListener);
		}

		clientSessionListenerMap.put(session.getHost(), incomingClientSessionListener);
	}

	/**
	 * It remove the mapping from a {@link ClientSessionListener} with a {@link ClientSession}
	 * @param session the {@link ClientSession} to be set as key on mapping
	 * @param incomingClientSessionListener the {@link ClientSessionListener} to remove the mapping
	 */	
	private void removeClientSessionListener(ClientSession session, ClientSessionListener clientSessionListener) {
		clientSessionListenerMap.remove(session.getHost());
		clientSessionListener.reconnect();
	}

	/**
	 * {@inheritDoc}
	 */
	public void onLoginRequest(ClientSession session, ByteBuffer message) {
		LoginResponse loginResponse = appListener.authenticateOnLogin(session, message);
		
		if (loginResponse == LoginResponse.LOGIN_SUCCESS) {
			loginSuccess(session, message);
		} else {
			loginFailure(session, message, loginResponse);
		}
	}

	/**
	 * When getting success maps the {@link ClientSession} and send login success
	 * @param session {@link ClientSession} that has logged in
	 * @param message any further message that came with login
	 */
	private void loginSuccess(ClientSession session, ByteBuffer message) {
		ClientSessionListener clientSessionListener = appListener.loggedIn(session);
		mapClientSessionListener(session, clientSessionListener);
		
		sendLoginSuccess(session);
	}

	/**
	 * Sends login success to a {@link ClientSession}
	 * @param session the {@link ClientSession} to send login success
	 */
	private void sendLoginSuccess(ClientSession session) {
		ByteBuffer message = ByteBuffer.allocate(3);
		message.put(SessionProtocol.LOGIN_SUCCESS.getId());
		message.rewind();
		session.send(message);
	}

	/**
	 * When getting failure maps the {@link ClientSession} and send login success
	 * @param session {@link ClientSession} that has login failure
	 * @param message any further message that came with login failure
	 * @param loginResponse 
	 */
	private void loginFailure(ClientSession session, ByteBuffer message, LoginResponse loginResponse) {
		appListener.logginFailure(session);
		ClientSessionListener clientSessionListener = getClientSessionListener(session);
		removeClientSessionListener(session, clientSessionListener);
		sendLoginFailure(session, loginResponse);
	}

	/**
	 * Sends login failure to a {@link ClientSession}
	 * @param session the {@link ClientSession} to send login failure
	 * @param loginResponse 
	 */
	private void sendLoginFailure(ClientSession session, LoginResponse loginResponse) {
		ByteBuffer message = ByteBuffer.allocate(2);
		message.put(SessionProtocol.LOGIN_FAILURE.getId());
		message.put(loginResponse.getId());
		message.rewind();
		session.send(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onReconnectRequest(ClientSession session, ByteBuffer message) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void onSessionMessage(ClientSession session, ByteBuffer message) {
		ClientSessionListener clientSessionListener = getClientSessionListener(session);
		clientSessionListener.receivedMessage(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onLogoutRequest(ClientSession session, ByteBuffer message) {
		logoutSuccess(session, message);		
	}

	/**
	 * When getting logout success notify {@link AppListener} and sends logout
	 * @param session {@link ClientSession} that is login failure
	 * @param message any further message that came with logout success
	 */
	private void logoutSuccess(ClientSession session, ByteBuffer message) {
		LogoutReason reason = LogoutReason.USER_REQUEST;
		appListener.loggedOut(session, reason);
		sendLogout(session, reason);
	}
	
	/**
	 * Sends logout to {@link ClientSession}
	 * @param session {@link ClientSession} to send message
	 * @param reason reason of logout
	 */
	private void sendLogout(ClientSession session, LogoutReason reason) {
		ByteBuffer message = ByteBuffer.allocate(2);
		message.put(SessionProtocol.LOGOUT_SUCCESS.getId());
		message.put((byte)reason.ordinal());
		message.rewind();
		session.send(message);		
	}	

	/**
	 * {@inheritDoc}
	 */
	public void onChannelJoin(String channelName, ClientSession session) {
		AppContext.getChannelManager().channelJoin(channelName, session);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onChannelLeave(String channelName, ClientSession session) {
		AppContext.getChannelManager().channelLeave(channelName, session);
	}

	/**
	 * {@inheritDoc}
	 */
	public void onChannelMessage(String channelName, ByteBuffer message) {
		AppContext.getChannelManager().channelMessage(channelName, message);
	}
}
