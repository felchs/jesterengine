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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import com.jge.server.net.AppContext;
import com.jge.server.net.Net;
import com.jge.server.net.NetProcessor;
import com.jge.server.net.SessionMessageReceiver;

/**
 * Netty implementation of {@link Net}
 * 
 *
 */
public class Netty extends Net {

	/**
	 * Whether uses SSL connection or not
	 */
    private static final boolean SSL = AppContext.getProperty("ssl") != null;
    
    /**
     * The por used to connect under Netty server
     */
    private static final int PORT = Integer.parseInt(AppContext.getProperty("com.jge.server.net.port", "8007"));
    
    ///////////////////////////////////////////////////////////////////////////
    
    /**
     * {@link SslContext} instance
     */
    private SslContext sslCtx = null;
    
    /**
     * The thread that starts the Netty
     */
    private NettyStarterThread nettyStarterThread;
    
    /**
     * Constructor passing parameters
     * @param netProcessor the net processor that is a {@link SessionMessageReceiver} 
     */
    public Netty(NetProcessor netProcessor) {
    	super(netProcessor);
    	
    	doInitOnNettyStarterThread();
	}

    /**
     * Initializes the {@link NettyStarterThread}
     * It blocks and waits until the Netty engine to be started 
     */
    private void doInitOnNettyStarterThread() {
    	this.nettyStarterThread = new NettyStarterThread(this);
    	nettyStarterThread.waitUntilStart();
	}

    /**
     * Initializes the server
     * @param nettyStarterThread the thread that starts the Netty engine
     */
	public void init(NettyStarterThread nettyStarterThread) {
        if (SSL) {
            SelfSignedCertificate ssc = null;
			try {
				ssc = new SelfSignedCertificate();
			} catch (CertificateException e) {
				e.printStackTrace();
			}
			
            try {
				sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
			} catch (SSLException e) {
				e.printStackTrace();
			}
        } else {
            sslCtx = null;
        }

        int nThreads = Runtime.getRuntime().availableProcessors();
        String nThreadsStr = AppContext.getProperty("com.jge.server.net.nthreads_net");
        if (nThreadsStr != null && !nThreadsStr.equals("default")) {
        	try {
        		nThreads = Integer.parseInt(nThreadsStr);
        	} catch(Exception e) {
        		e.printStackTrace();
        	}
        }
        
        // Configure the server.
        EventLoopGroup bossGroup = new NioEventLoopGroup(nThreads);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 100);
            b.handler(new LoggingHandler(LogLevel.INFO));
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline p = ch.pipeline();
                     if (sslCtx != null) {
                         p.addLast(sslCtx.newHandler(ch.alloc()));
                     }
                     //p.addLast(new LoggingHandler(LogLevel.INFO));
                     NettyServerHandler nettyServerHandler = new NettyServerHandler(getNetProcessor());
                     p.addLast(nettyServerHandler);
                 }
             });
            
            System.out.println("------------> PORT: " + PORT);
            // Start the server.
            ChannelFuture f = b.bind(PORT).sync();

            nettyStarterThread.notifyStart();
            
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
