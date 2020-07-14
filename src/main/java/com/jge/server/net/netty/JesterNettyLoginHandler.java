package com.jge.server.net.netty;

import io.netty.channel.ChannelHandlerContext;

import com.jge.server.net.AppContext;
import com.jge.server.net.JesterSessionMap;
import com.jge.server.net.session.ClientSession;

/**
 * This class is used to map the {@link ClientSession} that enters and exit the Server
 * It manages the Netty's {@link ChannelHandlerContext} and maps the {@link ClientSession}'s by host
 * 
 */
public class JesterNettyLoginHandler {

	/**
	 * When a new read from a {@link ChannelHandlerContext} occurs it maps the context to client session
	 * @param ctx the Netty channel's context
	 * @throws Exception any occurring exception
	 */
	public static void handleContextOnRead(ChannelHandlerContext ctx) throws Exception {
		String host = JesterNettyUtils.getHostWithContext(ctx);
		if (host != null) {
			long id = AppContext.getClientIdManager().createNewId(host);
			ClientSession incomingSession = new NettyClientSession(id, host, ctx);
			JesterSessionMap.get().addClientSession(incomingSession);
		}
	}

	/**
	 * When the session finishes it handles the mapping removing the {@link ChannelHandlerContext} related to the context to client session
	 * @param ctx the Netty channel's context
	 * @throws Exception any occurring exception
	 */
	public static void handleContextOnSessionFinish(ChannelHandlerContext ctx) throws Exception {
		String host = JesterNettyUtils.getHostWithContext(ctx);
		if (host != null) {
			JesterSessionMap.get().removeClientSession(host);
		}
	}
}
