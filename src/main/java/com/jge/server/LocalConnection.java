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
package com.jge.server;

import java.awt.Robot;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import com.jge.server.net.AppContext;
import com.jge.server.net.SessionProtocol;
import com.jge.server.net.Task;
import com.jge.server.utils.ByteUtils;
import com.jge.server.utils.DGSLogger;

/**
 * This class handles a local connection to engine.
 * It is used by robots: {@link Robot} to send play messages.
 * The messages must be enqueued by the method {@link LocalConnection}{@link #putOnQueueSender(LocalMessage)}
 * and messages are sent immediately respecting the queue of messages
 *  
 */
public class LocalConnection {
	/**
	 * The singleton instance of this class
	 */
	private static LocalConnection instance;
	
	/**
	 * Gets the singleton instance of {@link LocalConnection} 
	 * @return the singleton instance of {@link LocalConnection}
	 */
	public static LocalConnection get() {
		if (instance == null) {
			try {
				instance = new LocalConnection();
			} catch (Exception e) {
				e.printStackTrace();
				DGSLogger.log(Level.SEVERE, "LocalConnection.get(), could not get instance");
				System.exit(0);
			}
		}
		return instance;
	}
	
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * The intrinsic lock object to handle lock on {@link #queueSendBytesMap} insertion
	 */
	private final Object LOCK = new Object();
	
	/**
	 * The host of server connection 
	 */
	private final String host = "127.0.0.1";
	
	/**
	 * The port of server connection
	 */
	private int port = 8007;
	
	/**
	 * Socket used to handle the connection
	 */
	private Socket socket;
	
	/**
	 * <pre>
	 * The queue of sending messages
	 * All the messages that must be send is enqueued in this map that has:
	 * - Integer Key <- the index of message
	 * - SortedMap with (Key of Long, Value of LocalMessage)
	 * 		- Long Key <- the game id
	 * 		- {@link LocalMessage} <- the message itself
	 * </pre>
	 */
	private Map<Integer, SortedMap<Long, LocalMessage>> queueSendBytesMap = new HashMap<Integer, SortedMap<Long,LocalMessage>>();
	
	/**
	 * The already sent local messages
	 */
	private Map<Integer, LocalMessage> prevSentMap = new HashMap<Integer, LocalMessage>(); 
	
	/**
	 * Constructor which initializes the local socket connection
	 *  
	 * @throws Exception
	 */
	private LocalConnection() throws Exception {
		socket = new Socket(host, port);
		final OutputStream os = socket.getOutputStream();
		InputStream is = socket.getInputStream();
		String username = "ROBOT-PLAYER@";
		String email = "";
        String pass = "";
        String nameEmail = username + "&" + email;
        byte[] nameEmailAsBytes = nameEmail.getBytes("UTF-8");
		byte[] passAsBytes = pass.getBytes("UTF-8");
		
		// populate message_
		int capacity = 2 + 4 + nameEmailAsBytes.length + 4 + passAsBytes.length;
		byte[] message_ = new byte[capacity];
		int idx = 0;
		message_[idx++] = SessionProtocol.LOGIN_REQUEST.getId();
		message_[idx++] = SessionProtocol.VERSION.getId();
		byte[] nameEmailLenghtAsBytes = ByteUtils.getBytes((short)nameEmailAsBytes.length);
		message_[idx++] = nameEmailLenghtAsBytes[0];
		message_[idx++] = nameEmailLenghtAsBytes[1];
		for (int i = 0; i < nameEmailAsBytes.length; i++) {
			message_[idx++] = nameEmailAsBytes[i];
		}
		byte[] passLenghtAsBytes = ByteUtils.getBytes((short)passAsBytes.length);
		message_[idx++] = passLenghtAsBytes[0];
		message_[idx++] = passLenghtAsBytes[1];
        for (int i = 0; i < passAsBytes.length; i++) {
        	message_[idx++] = passAsBytes[i];
        }
        
        ByteBuffer buffer = ByteBuffer.allocate(message_.length + 2);
        buffer.putShort((short) message_.length);
        buffer.put(message_);
        buffer.flip();
        byte[] array = buffer.array();
        os.write(array);
        os.flush();
        
		DataInputStream in = new DataInputStream(is);
		byte[] packetData = new byte[in.readShort()];
		in.readFully(packetData);
		
		initQueueThreadSending(os);
	}

	/**
	 * Initializes the Thread sending messages
	 * 
	 * @param os the OutputStream used to send messages to local connection
	 */
	private void initQueueThreadSending(final OutputStream os) {
		new Thread() {
			public void run() {
				while (true) {
					synchronized (LOCK) {
						Iterator<Integer> gameKeys = queueSendBytesMap.keySet().iterator();
						while (gameKeys.hasNext()) {
							Integer gameKey = gameKeys.next();
							SortedMap<Long, LocalMessage> localMessageVecMap = queueSendBytesMap.get(gameKey);
							
							if (localMessageVecMap.size() > 0) {
								LocalMessage localMessage = localMessageVecMap.get(localMessageVecMap.firstKey());
								
								LocalMessage prevSentMessage = prevSentMap.get(gameKey);
								
								while (localMessageVecMap.size() > 0 &&
									      (localMessage.messageIdx < 0 || 
									      (prevSentMessage == null || localMessage.messageIdx - prevSentMessage.messageIdx == 1))) {
									try {										
										os.write(localMessage.message);
										os.flush();

										prevSentMap.put(localMessage.game, localMessage);
										
										LocalMessage remove = localMessageVecMap.remove(localMessage.messageIdx);
										DGSLogger.log("LocalConnection.ThreadSender(), message send a message, game: " + localMessage.game + ", messageIdx: " + localMessage.messageIdx + ", prevMessage: " + prevSentMessage + ", remove: " + remove + ", sz: " + localMessageVecMap.size());

										prevSentMessage = localMessage;
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
							}
						}
					}
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	/**
	 * Puts a new message on queue 
	 * @param message the message to be put on {@link #queueSendBytesMap}
	 */
	public void putOnQueueSender(LocalMessage message) {
		DGSLogger.log("LocalConnection.putOnQueueSender...");
		synchronized (LOCK) {
			SortedMap<Long, LocalMessage> localMessageVec = queueSendBytesMap.get(message.game);
			
			if (localMessageVec == null) {
				localMessageVec = new TreeMap<Long, LocalMessage>();
				queueSendBytesMap.put(message.game, localMessageVec);
			}
			
			localMessageVec.put(message.messageIdx, message);
		}
	}
	
	/**
	 * Initializes the connection
	 * 
	 * @throws Exception
	 */
	public static void initRobotsConnection() throws Exception {
		AppContext.getTaskManager().startTask(new Task() {
			public void run() {
				try {
					LocalConnection.get();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}	
}