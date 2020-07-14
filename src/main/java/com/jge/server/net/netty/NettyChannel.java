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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.DefaultChannelGroup;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Vector;

import com.jge.server.net.Channel;
import com.jge.server.net.Delivery;
import com.jge.server.net.session.ClientSession;
import com.jge.server.space.SpaceChannelReceiver;

/**
 * Netty implementation of {@link Channel}
 * @see Channel
 *
 */
public class NettyChannel extends Channel {
	/**
	 * The group used to join the sessions
	 */
	private DefaultChannelGroup channelGroup;

	/**
	 * Constructor passing parameters
	 * @param channelName the name of the channel
	 * @param channelReceiver the receiver of messages from this channel (a listener)
	 * @param delivery the {@link Delivery} of messages that are sent by this {@link Channel} 
	 * @param channelGroup the Netty {@link DefaultChannelGroup} of this {@link Channel}
	 */
	public NettyChannel(String channelName, SpaceChannelReceiver channelReceiver, Delivery delivery, DefaultChannelGroup channelGroup) {
		super(channelName, channelReceiver, delivery);
		this.channelGroup = channelGroup;
	}
	
	/**
	 * With a {@link ClientSession} it returns the associated Netty channel instance
	 * @param session the associated {@link ClientSession} linked to the Netty channel instance
	 * @return the Netty channel instance liked to the {@link ClientSession}
	 */
	private io.netty.channel.Channel getNettyChannelWithSession(ClientSession session) {
		NettyClientSession nettySession = (NettyClientSession)session;
		ChannelHandlerContext ctx = nettySession.getChannelHanlderContext();
		return ctx.channel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ClientSession channelJoinImpl(ClientSession session) {
		if (channelGroup.add(getNettyChannelWithSession(session))) {
			return session;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ClientSession> channelJoinImpl(List<ClientSession> sessions) {
		List<ClientSession> sessionListToReturn = new Vector<ClientSession>();
		for (ClientSession session : sessions) {
			ClientSession sessionAdded = channelJoinImpl(session);
			if (sessionAdded != null) {
				sessionListToReturn.add(sessionAdded);
			}
		}
		return sessionListToReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ClientSession channelLeaveImpl(ClientSession session) {
		if (channelGroup.remove(getNettyChannelWithSession(session))) {
			return session;
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ClientSession> channelLeaveImpl(List<ClientSession> sessions) {
		List<ClientSession> sessionListToReturn = new Vector<ClientSession>();
		for (ClientSession session : sessions) {
			ClientSession sessionRemoved = channelLeaveImpl(session);
			if (sessionRemoved != null) {
				sessionListToReturn.add(sessionRemoved);
			}
		}
		return sessionListToReturn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ClientSession> leaveAllImpl() {
		return channelLeaveImpl(getSessionsList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Channel send(ByteBuffer message) {
		ByteBuf unPooledCopiedBuffer = Unpooled.copiedBuffer(message);
		channelGroup.writeAndFlush(unPooledCopiedBuffer);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Channel send(ClientSession sender, ByteBuffer message) {
		int idSzBytes = 4; // sender.getId() -> long -> 4 bytes
		int initialCapacity = idSzBytes + message.capacity();
		ByteBuf byteBuf = Unpooled.directBuffer(initialCapacity);
		byteBuf.writeLong(sender.getId());
		byteBuf.writeBytes(message);
		channelGroup.writeAndFlush(byteBuf);
		return this;
	}
}
