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

import java.nio.ByteBuffer;

import com.jge.server.net.session.ClientSession;

/**
 * Netty implementation of {@link ClientSession}
 * @see ClientSession
 */
public class NettyClientSession extends ClientSession {
	/**
	 * Channel context associated with this {@link ClientSession}
	 */
	private ChannelHandlerContext ctx;

	/**
	 * Constructor passing parameters
	 * @param id the id of this {@link ClientSession}
	 * @param host the host associated with this {@link ClientSession}
	 * @param ctx the Netty {@link ChannelHandlerContext} associated with this {@link ClientSession}
	 */
	public NettyClientSession(long id, String host, ChannelHandlerContext ctx) {
		super(id, host);

		this.ctx = ctx;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isConnected() {
		return ctx.channel().isActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClientSession send(ByteBuffer message) {
		ByteBuf copiedBuffer = Unpooled.copiedBuffer(message);
		short payLoadLength = (short) copiedBuffer.capacity();
		ByteBuf bufToWrite = Unpooled.buffer(2 + payLoadLength);
		bufToWrite.writeShort(payLoadLength);
		bufToWrite.writeBytes(copiedBuffer);
		ctx.writeAndFlush(bufToWrite);
		return this;
	}
	
	/**
	 * Gets the Netty {@link ChannelHandlerContext} associated with this {@link ClientSession}
	 * @return the Netty {@link ChannelHandlerContext} associated with this {@link ClientSession}
	 */
	public ChannelHandlerContext getChannelHanlderContext() {
		return ctx;
	}
}