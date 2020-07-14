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
package com.jge.server.net.netty;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

import com.jge.server.net.Channel;
import com.jge.server.net.ChannelManager;
import com.jge.server.net.Delivery;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.SpaceChannelReceiver;

/**
 * Netty implementation of {@link ChannelManager}
 * @see ChannelManager
 */
public class NettyChannelManagerImpl implements ChannelManager {
	/**
	 * <pre>
	 * The mapping of {@link Channel}'s where:
	 *   key <- String <- Channel Name
	 *   value <- {@link Channel} itself
	 * </pre>
	 */
	private ConcurrentHashMap<String, Channel> channelGroupMap = new ConcurrentHashMap<String, Channel>();
	
	/**
	 * {@inheritDoc}
	 */
	public Channel getChannel(String channelName) {
		return channelGroupMap.get(channelName);
	}

	/**
	 * {@inheritDoc}
	 */
	public Channel createChannel(String channelName, SpaceChannelReceiver channelReceiver, Delivery delivery) {
		DefaultChannelGroup defaultChannelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		NettyChannel nettyChannel = new NettyChannel(channelName, channelReceiver, delivery, defaultChannelGroup);
		channelGroupMap.put(channelName, nettyChannel);
		return nettyChannel;
	}

	/**
	 * {@inheritDoc}
	 */
	public void channelJoin(String channelName, ClientSession session) {
		Channel channel = channelGroupMap.get(channelName);
		channel.join(session);
	}

	/**
	 * {@inheritDoc}
	 */
	public void channelLeave(String channelName, ClientSession session) {
		Channel channel = channelGroupMap.get(channelName);
		channel.leave(session);
	}

	/**
	 * {@inheritDoc}
	 */
	public void channelMessage(String channelName, ByteBuffer message) {
		Channel channel = channelGroupMap.get(channelName);
		channel.send(message);
	}
}
