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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.jge.server.client.ClientListener;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.SpaceChannelReceiver;

/**
 * A channel represents a way which many clients can communicate with
 * Each channel has a list of {@link ClientSession} which the messages are delivered
 * The message can have a delivery reliability check out the {@link Delivery}
 *  
 */
public abstract class Channel {
	/**
	 * A custom name for this {@link Channel}
	 */
	private String name;
	
	/**
	 * Handling events received by this {@link Channel} is made by {@link SpaceChannelReceiver}
	 */
	private SpaceChannelReceiver channelReceiver;
	
	/**
	 * The reliability of messages
	 */
	private Delivery delivery;
	
	/**
	 * The list of {@link ClientSession}'s connected to this {@link Channel}
	 */
	private Vector<ClientSession> sessionList = new Vector<ClientSession>();

	/**
	 * Constructor passing fields
	 * @param name the custom name of this {@link Channel} 
	 * @param channelReceiver the {@link SpaceChannelReceiver} of this {@link Channel}
	 * @param delivery the {@link Delivery} of this channel
	 */
	public Channel(String name, SpaceChannelReceiver channelReceiver, Delivery delivery) {
		this.name = name;
		this.channelReceiver = channelReceiver;
		this.delivery = delivery;
	}
	
	/**
	 * Gets the custom name of this channel
	 * @return the custom name of this {@link Channel}
	 */
    public String getName() {
		return name;
	}
    
    /**
     * Gets the channel receiver of this channel
     * @return the {@link SpaceChannelReceiver} of this {@link Channel}
     */
    public SpaceChannelReceiver getChannelReceiver() {
		return channelReceiver;
	}

    /**
     * Gets the delivery of this channel
     * @return the {@link Delivery} of this {@link Channel}
     */
    public Delivery getDelivery() {
		return delivery;
	}

    /**
     * Gets whether there are client sessions connected to this channel or not
     * @return whether there are {@link ClientSession} connected to this {@link Channel} or not
     */
    public boolean hasSessions() {
    	return !sessionList.isEmpty();
    }

    /**
     * Gets the client sessions iterator of this channel
     * @return the {@link Iterator} of {@link ClientSession} of this {@link Channel}
     */
    public Iterator<ClientSession> getSessions() {
    	return sessionList.iterator();
    }
    
    /**
     * Get client sessions list
     * @return the {@link ClientSession} list
     */
    public List<ClientSession> getSessionsList() {
    	return sessionList;
    }

    /**
     * A join to a {@link Channel} means the client integrate the 
     * {@link ClientListener}'s list and begins to receive messages from this {@link Channel}
     * @param session the {@link ClientSession} to join the {@link Channel}
     * @return the {@link Channel} with {@link ClientSession} joined
     */
    public Channel join(ClientSession session) {
    	ClientSession joinedSession = channelJoinImpl(session);
    	if (joinedSession != null) {
    		sessionList.add(session);
    	}
    	return this;
    }

    /**
     * A join to a {@link Channel} means the client integrate the 
     * {@link ClientListener}'s list and begins to receive messages from this {@link Channel}
     * @param sessions the {@link ClientSession}'s to join the {@link Channel}
     * @return the {@link Channel} with {@link ClientSession}'s joined
     */
	public Channel join(List<ClientSession> sessions) {
		List<ClientSession> joinedClientSessions = channelJoinImpl(sessions);
		sessionList.addAll(joinedClientSessions);
		return this;
	}

	/**
	 * To leave a {@link Channel} means the client will not more integrate the
	 * {@link ClientListener}'s list and will not more receive messages from this {@link Channel} 
	 * @param session the {@link ClientSession} to leave the {@link Channel}
	 * @return the {@link Channel} with {@link ClientSession} out
	 */
	public Channel leave(ClientSession session) {
		ClientSession removedSession = channelLeaveImpl(session);
		sessionList.remove(removedSession);
		return this;
	}

	/**
	 * To leave a {@link Channel} means the client will not more integrate the
	 * {@link ClientListener}'s list and will not more receive messages from this {@link Channel} 
	 * @param sessions the {@link ClientSession}'s to leave the {@link Channel}
	 * @return the {@link Channel} with {@link ClientSession}'s out
	 */
	public Channel leave(List<ClientSession> sessions) {
		List<ClientSession> removedSessions = channelLeaveImpl(sessions);
		sessionList.removeAll(removedSessions);
		return this;
	}

	/**
	 * To leave a {@link Channel} means the client will not more integrate the
	 * {@link ClientListener}'s list and will not more receive messages from this {@link Channel}
	 * With this method all {@link ClientSession}'s leave the {@link Channel} 
	 * @return the {@link Channel} with {@link ClientSession}'s out
	 */
	public Channel leaveAll() {
		List<ClientSession> removedSessions = leaveAllImpl();
		sessionList.removeAll(removedSessions);
		return this;
	}

	/**
	 * Abstract method for channel join
	 * @param session {@link ClientSession} to join the {@link Channel}
	 * @return the {@link ClientSession} that joined the {@link Channel}
	 */
    protected abstract ClientSession channelJoinImpl(ClientSession session);

	/**
	 * Abstract method for channel join
	 * @param sessions {@link ClientSession}'s to join the {@link Channel}
	 * @return the {@link ClientSession}'s that joined the {@link Channel}
	 */
    protected abstract List<ClientSession> channelJoinImpl(List<ClientSession> sessions);

	/**
	 * Abstract method for channel leave
	 * @param session {@link ClientSession} to leave the {@link Channel}
	 * @return the {@link ClientSession} that left the {@link Channel}
	 */
    protected abstract ClientSession channelLeaveImpl(ClientSession session);

	/**
	 * Abstract method for channel leave
	 * @param session {@link ClientSession}'s to leave the {@link Channel}
	 * @return the {@link ClientSession}'s that left the {@link Channel}
	 */
    protected abstract List<ClientSession> channelLeaveImpl(List<ClientSession> sessions);

	/**
	 * Abstract method for all {@link ClientSession}'s channel leave
	 * @return the {@link ClientSession} that left the {@link Channel}
	 */
    protected abstract List<ClientSession> leaveAllImpl();

    /**
     * Abstract method for channel sending message
     * This method follows a message sending for all {@link ClientSession}'s that are listed on {@link Channel#sessionList} 
     * @param message the message to deliver
     * @return this {@link Channel}
     */
	public abstract Channel send(ByteBuffer message);

	/**
     * Abstract method for channel sending message
     * It also send the {@link ClientSession} sender of the message
     * This method follows a message sending for all {@link ClientSession}'s that are listed on {@link Channel#sessionList} 
     * @param sender the {@link ClientSession} that is sending the message
     * @param message the message to deliver
     * @return this {@link Channel}
	 */
    public abstract Channel send(ClientSession sender, ByteBuffer message);
}
