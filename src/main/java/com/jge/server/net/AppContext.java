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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import com.jge.server.net.netty.Netty;
import com.jge.server.net.netty.NettyChannelManagerImpl;
import com.jge.server.net.session.ClientSession;
import com.jge.server.utils.DGSLogger;

/**
 * This class provides access to global managers and global data 
 * information from the server   
 *
 */
public class AppContext {

	/**
	 * Singleton instance 
	 */
	private static AppContext instance;
	
	/**
	 * Gets the instance in a Singleton behavior
	 * @return the singleton instance of {@link AppContext}
	 */
	public static AppContext get() {
		if (instance == null) {
			instance = new AppContext();
		}
		return instance;
	}
	
	/**
	 * Gets the global Channel Manager of the Server Application
	 * @return the global {@link ChannelManager} of the application
	 */
	public static ChannelManager getChannelManager() {
		return get().channelManager;
	}
	
	/**
	 * Gets the global Task Manager of the Server Application
	 * @return the global {@link TaskManager} of Server Application
	 */
	public static TaskManager getTaskManager() {
		return get().taskManager;
	}

	/**
	 * Gets the global Data Manager of the Server Application
	 * @return the global {@link DataManager} of Server Application
	 */
	public static DataManager getDataManager() {
		return get().dataManager;
	}

	/**
	 * Gets the global Client id Manager of the Server Application
	 * @return the global {@link ClientIdManager} of Server Application
	 */
	public static ClientIdManager getClientIdManager() {
		return get().clientIdManager;
	}
	
	/**
	 * Gets a property
	 * The properties file is by default on config.properties file
	 * @param key the key to get the property
	 * @return a property
	 */
	public static String getProperty(String key) {
		return get().getPropertyString(key);
	}
	
	/**
	 * Gets a property
	 * The properties file is by default on config.properties file
	 * @param key the key to get the property
	 * @param defaultProperty if there isn't a property on confi.properties sets this property as default
	 * @return a property
	 */
	public static String getProperty(String key, String defaultProperty) {
		String property = getProperty(key);
		if (property == null) {
			property = defaultProperty;
		}
		return property;
	}
		
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor with no fields
	 * It loads the properties by {@link #loadProperties()}
	 */
	public AppContext() {
		loadProperties();
	}
	
	/**
	 * The properties file which is loaded by config.properties file
	 */
	private Properties properties;
	
	/**
	 * The incoming events to {@link AppContext} are replicated into {@link AppListener}
	 */
	private AppListener appListener;
	
	/**
	 * Entry point for messages coming from {@link ClientSession}
	 * Messages are processed and delivered by this {@link NetProcessor}
	 */
	private NetProcessor netProcessor;
	
	/**
	 * Root Net class to handle client x server messages
	 */
	private Net net;
	
	/**
	 * Manager instance to handle all {@link Channel}'s 
	 */
	private ChannelManager channelManager;
	
	/**
	 * Manager instance to handle all {@link Task}'s
	 */
	private TaskManager taskManager;
	
	/**
	 * Manager instance to handle all managed Objects
	 */
	private DataManager dataManager;
	
	/**
	 * Manager instance to handle all ids from clients
	 */
	private ClientIdManager clientIdManager;
	
	/**
	 * Loads the conf.properties file
	 */
	private void loadProperties() {
		try {
			String fileName = System.getProperty("config.properties");
			FileInputStream propFile = new FileInputStream(fileName);
			Properties properties = new Properties(System.getProperties());
			properties.load(propFile);
			this.properties = properties;
			
			DGSLogger.log(Level.INFO, "AppContext.loadProperties(), properties loaded");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			DGSLogger.log(Level.SEVERE, e);
			DGSLogger.log(Level.SEVERE, "You the file config.properties must exit for system initializations");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			DGSLogger.log(Level.SEVERE, e);
		}
	}
	
	/**
	 * Gets the net type used by the server
	 * @return the net type used by the server
	 */
	private NetType getNetType() {
		String netTypeStr = AppContext.getProperty("com.jge.server.net.nettype", NetType.NETTY.toString());
	    netTypeStr = netTypeStr.toUpperCase();
	    return NetType.valueOf(netTypeStr);
	}
	
	/**
	 * Loads the {@link NetProcessor} 
	 */
	private void loadNetProcessor() {
		this.netProcessor = new NetProcessor(appListener);
		DGSLogger.log(Level.INFO, "AppContext.loadNetProcessor(), NetProcessor loaded");
	}
	
	/**
	 * Gets the net processor instance
	 * @return the {@link NetProcessor} instance
	 */
	public NetProcessor getNetProcessor() {
		return netProcessor;
	}
	
	/**
	 * Loads the {@link Net} implementation instance
	 * The instance is choose by the config param: com.jge.server.net.nettype from
	 * file conf.properties
	 */
	private void loadNet() {
		switch (getNetType()) {
		case MINA:
			break;
			
		case NETTY:
			net = new Netty(getNetProcessor());
			break;
		}
		
		DGSLogger.log(Level.INFO, "AppContext.loadNet(), net loaded");
	}
	
	/**
	 * Gets the {@link Net} instance
	 * @return the {@link Net} instance
	 */
	public Net getNet() {
		return net;
	}
	
	/**
	 * Gets the properties file
	 * @return the {@link Properties} file
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * Gets a property String with a key
	 * @param key the key to retrieve the property String
	 * @return the property String
	 */
	public String getPropertyString(String key) {
		Properties properties = getProperties();
		Object propertyObj = properties.get(key);
		if (propertyObj == null) {
			return null;
		}
		return (String) propertyObj;
	}
	
	/**
	 * Loads the {@link ChannelManager} implementation instance
	 * The instance is choose by the config param: com.jge.server.net.nettype from
	 * file conf.properties
	 */
	protected void loadChannelManager() {
		switch (getNetType()) {
		case MINA:
			break;
			
		case NETTY:
			this.channelManager = new NettyChannelManagerImpl();
			break;
		}

		DGSLogger.log(Level.INFO, "AppContext.loadChannelManager(), ChannelManager loaded");
	}
	
	/**
	 * Loads the {@link TaskManager} of this application
	 */
	protected void loadTaskManager() {
		this.taskManager = new TaskManagerImpl();
		DGSLogger.log(Level.INFO, "AppContext.loadTaskManager(), TaskManager loaded");
	}
	
	/**
	 * Loads the {@link DataManager} of this application
	 */
	protected void loadDataManager() {
		this.dataManager = new DataManagerImpl();
		DGSLogger.log(Level.INFO, "AppContext.loadDataManager(), DataManager loaded");
	}
	
	/**
	 * Loads the {@link ClientIdManager} of this application
	 */
	protected void loadClientIdManger() {
		this.clientIdManager = new ClientNanoIdManagerImpl();
		
		DGSLogger.log(Level.INFO, "AppContext.loadClientIdManager(), ClientIdManager loaded");
	}
	
	/**
	 * Loads the {@link AppListener} of this application
	 * The {@link AppListener} is loaded via conf.properties file: com.jge.server.net.applistener
	 * which loads the object by reflection by class name instantiating it
	 * The {@link AppListener} can also be manually initialized by just creating a main class that 
	 * extends the {@link AppListener}. Check out the {@link InitializerTest} example
	 *    
	 * @param appListener the {@link AppListener} of this application
	 */
	private void loadAppListener(AppListener appListener) {
		if (appListener != null) {
			this.appListener = appListener;
		} else {
			String className = getProperty("com.jge.server.net.applistener");
			
			if (!className.equals("default")) {
				try {
					Class<?> forName = Class.forName(className);
					this.appListener = (AppListener)forName.newInstance();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
				DGSLogger.log(Level.INFO, "AppContext.loadProperties(), appListener loaded");
			} else {
				DGSLogger.log(Level.WARNING, "AppContext.loadAppListener, com.jge.server.net.applistener is set to default, so the user must implement it manually");
			}
		}
	}
	
	/**
	 * After creating the instance and loading all properties and objects the 
	 * {@link AppContext} must be initialized. So call this method manually when 
	 * creating a new instance of {@link AppContext} or dont't call it when
	 * loading the engine by command line. The engine automatically call this 
	 * method initializing the application
	 *  
	 * @param appListener
	 */
	public void init(AppListener appListener) {
		loadAppListener(appListener);
		
		loadNetProcessor();
		
		loadNet();
		
		loadChannelManager();
		
		loadTaskManager();
		
		loadDataManager();
		
		loadClientIdManger();
		
		appListener.initialize(getProperties());
	}
}