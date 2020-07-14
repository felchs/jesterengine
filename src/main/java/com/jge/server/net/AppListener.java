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
import java.util.Properties;

import com.jge.server.net.session.ClientSession;

/**
 * The AppListener is an entry point class that handles events that came from {@link AppContext}
 * When creating a new application all events that came into {@link AppContext} which must be
 * replicated or handled externally, are handled in this {@link AppListener} class
 * E.g.: when a new ClientSession connects to the server, you must sign your game that a new Client entered the game
 * or at least that a new Client is active for entering the game (depending on your implementation)
 * 
 * The {@link AppListener} is loaded via conf.properties file: com.jge.server.net.applistener
 * which loads the object by reflection by class name instantiating it
 * The AppListener must not have a constructor with fields because it's just a listener one
 * Any need to initialize something must be within the method {@link AppListener#initialize(Properties)}
 * 
 * The {@link AppListener} can also be manually initialized by just creating a main class that 
 * extends the {@link AppListener}. Check out the {@link InitializerTest} example
 *  
 */
public interface AppListener {

	/**
	 * The initialization of objects and attributes can be made into this class
	 * It also passes the properties file to manage the initializations
	 * @param props the properties file to manage the initializations
	 */
	void initialize(Properties props);

	/**
	 * When a new {@link ClientSession} logs into the server it signs the log to {@link AppListener}
	 * by this method. For managing the future session behavior, a {@link ClientSessionListener} class
	 * must be returned 
	 * @param session the {@link ClientSession} that logged
	 * @return a {@link ClientSessionListener} to manage future session actions
	 */
    ClientSessionListener loggedIn(ClientSession session);

	/**
	 * When a new {@link ClientSession} logs out the server it signs the log to {@link AppListener}
	 * by this method. 
	 * @param session the {@link ClientSession} that logged
	 * @param reason the {@link LogoutReason} of logout
	 */
    void loggedOut(ClientSession session, LogoutReason reason);
    
	/**
	 * When a new {@link ClientSession} try to login the server but it fails it signs the log to {@link AppListener}
	 * @param session the {@link ClientSession} that logged
	 */
    void logginFailure(ClientSession session);

    /**
	 * When a new {@link ClientSession} logs into the server it signs the {@link AppListener}
	 * and retrieve an authentication  
	 * @param session the {@link ClientSession} that logged
     * @param message a message to handle authentication like password or key or anything else customized
     * @return the reason can login or not
     */
	LoginResponse authenticateOnLogin(ClientSession session, ByteBuffer message);
}
