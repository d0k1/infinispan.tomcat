package com.focusit.sessionmanager.infinispan;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.Session;
import org.apache.catalina.session.StandardManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * Tomcat session manager backed by Infinispan. Without configuring it will work
 * as standard session manager
 * 
 * @author Denis V. Kirpichenkov
 *
 */
public class InfinispanSessionManager extends StandardManager {

	private final Log log = LogFactory.getLog(InfinispanSessionManager.class);

	/**
	 * In case of disable autostart webapp can start cache storage just in time.
	 * To do that webapp must provide it's context path to get appropriate session manager
	 */
	private static final ConcurrentHashMap<String, InfinispanSessionManager> managers = new ConcurrentHashMap<>();

	public InfinispanSessionManager() {
		super();
	}

	/**
	 * Default region name
	 */
	private String regionName = "TISM";

	/**
	 * autostart Infinispan cache manager and get region
	 */
	private boolean autostart = false;

	/**
	 * Custom configuration file path
	 */
	private String config = null;

	/**
	 * Sometime it is quite useful get region from provider method
	 */
	/**
	 * Provider class name
	 */
	private String containerProviderClass = null;
	/**
	 * Provider class method
	 */
	private String containerProviderMethod = "get";

	@Override
	public String getInfo() {
		return "InfinispanSessionManager/1.0";
	}

	@Override
	public String getName() {
		return "TISM";
	}

	@Override
	protected void doLoad() throws ClassNotFoundException, IOException {
	}

	@Override
	protected void doUnload() throws IOException {
	}

	@Override
	protected synchronized void startInternal() throws LifecycleException {
		super.startInternal();
		log.info("Starting session manager for container " + getContainer().getName());
		managers.put(getContainer().getName(), this);

		if (autostart) {
			startCache();
		}
	}

	@Override
	protected synchronized void stopInternal() throws LifecycleException {
		super.stopInternal();
	}

	public void restart() throws LifecycleException {
		stop();
		start();
	}

	public static InfinispanSessionManager getManagerByContainer(String name) {
		return managers.get(name);
	}

	/**
	 * Method provides an ability to change session storage at runtime. For
	 * example, when you need to get region from a deployed application instead
	 * of auto created one, this method will help
	 * 
	 * @param container
	 * @throws LifecycleException
	 */
	public void setSessionsContainer(Map<String, Session> container) throws LifecycleException {
		stop();
		log.debug("Using external session storage");
		sessions = container;
		start();
	}

	public String getRegionName() {
		return regionName;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void startCache() {
		if (getContainerProviderClass() != null && getContainerProviderMethod() != null) {
			try {
				Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(containerProviderClass);
				Method method = cls.getMethod(containerProviderMethod, null);
				sessions = (Map) method.invoke(null, null);
			} catch (ClassNotFoundException e) {
				log.error(e.toString(), e);
			} catch (IllegalAccessException e) {
				log.error(e.toString(), e);
			} catch (IllegalArgumentException e) {
				log.error(e.toString(), e);
			} catch (InvocationTargetException e) {
				log.error(e.toString(), e);
			} catch (NoSuchMethodException e) {
				log.error(e.toString(), e);
			} catch (SecurityException e) {
				log.error(e.toString(), e);
			}
		} else {
			if (config == null) {
				org.infinispan.configuration.global.GlobalConfigurationBuilder global = new org.infinispan.configuration.global.GlobalConfigurationBuilder();
				global.globalJmxStatistics().allowDuplicateDomains(true);
				org.infinispan.configuration.cache.ConfigurationBuilder builder = new org.infinispan.configuration.cache.ConfigurationBuilder();
				builder.jmxStatistics().disable();

				org.infinispan.manager.DefaultCacheManager manager = new org.infinispan.manager.DefaultCacheManager(
						builder.build(), true);
				sessions = manager.getCache(name);
			} else {
				try {
					org.infinispan.manager.DefaultCacheManager manager = new org.infinispan.manager.DefaultCacheManager(
							config, true);
					sessions = manager.getCache(name);
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}
		}
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public boolean isAutostart() {
		return autostart;
	}

	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	public String getContainerProviderClass() {
		return containerProviderClass;
	}

	public void setContainerProviderClass(String containerProviderClass) {
		this.containerProviderClass = containerProviderClass;
	}

	public String getContainerProviderMethod() {
		return containerProviderMethod;
	}

	public void setContainerProviderMethod(String containerProviderMethod) {
		this.containerProviderMethod = containerProviderMethod;
	}
}
