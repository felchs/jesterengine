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
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.ByteBuffer;

import com.jge.server.net.JesterSessionMap;
import com.jge.server.net.NetProcessor;
import com.jge.server.net.SessionProtocol;
import com.jge.server.net.session.ClientSession;

/**
 * Netty Incoming messages handler <- {@link ChannelInboundHandlerAdapter} implementation
 * 
 */
@Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
	/**
	 * The {@link NetProcessor} of incoming messages
	 */
	private NetProcessor netMessageReceiver;
	
	/**
	 * Constructor passing parameters
	 * @param netMessageReceiver 
	 */
	public NettyServerHandler(NetProcessor netMessageReceiver) {
		this.netMessageReceiver = netMessageReceiver;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try {
			JesterNettyLoginHandler.handleContextOnRead(ctx);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// client session
		String host = JesterNettyUtils.getHostWithContext(ctx);
		ClientSession clientSession = JesterSessionMap.get().getClientSession(host);
		
		ByteBuf byteBuff = (ByteBuf) msg;
  	  	ByteBuffer message = byteBuff.nioBuffer();
  	  	
  	  	@SuppressWarnings("unused")
		short len = message.getShort(); 
  	  	byte sessionProtocol = message.get();

  	  	if (SessionProtocol.RECONNECT_REQUEST.getId() == sessionProtocol) {
  	  		netMessageReceiver.onReconnectRequest(clientSession, message);
  	  	} else if (SessionProtocol.LOGIN_REQUEST.getId() == sessionProtocol) {
  	  		@SuppressWarnings("unused")
			byte version = message.get();
 	  		netMessageReceiver.onLoginRequest(clientSession, message);
  	  	} else if (SessionProtocol.SESSION_MESSAGE.getId() == sessionProtocol) {
  	  		netMessageReceiver.onSessionMessage(clientSession, message);
  	  	} else if (SessionProtocol.CHANNEL_JOIN.getId() == sessionProtocol) {
  			String channelName = JesterNettyUtils.readStringWithMessage(message);
  			netMessageReceiver.onChannelJoin(channelName, clientSession);
  		} else if (SessionProtocol.CHANNEL_LEAVE.getId() == sessionProtocol) {
  			String channelName = JesterNettyUtils.readStringWithMessage(message);
  			netMessageReceiver.onChannelLeave(channelName, clientSession);
 	  	} else if (SessionProtocol.CHANNEL_MESSAGE.getId() == sessionProtocol) {
 	  		String channelName = JesterNettyUtils.readStringWithMessage(message);
 	  		netMessageReceiver.onChannelMessage(channelName, message);
 	  	}
	}
  
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
  
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}