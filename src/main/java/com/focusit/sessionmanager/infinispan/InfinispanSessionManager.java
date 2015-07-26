package com.focusit.sessionmanager.infinispan;

import java.io.IOException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.session.StandardManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.infinispan.manager.DefaultCacheManager;

public class InfinispanSessionManager extends StandardManager {

	private final Log log = LogFactory.getLog(InfinispanSessionManager.class);
	
	private DefaultCacheManager cacheManager;
	
	public InfinispanSessionManager(){
		super();
		log.debug("Starting Innfinispan session manager");
		cacheManager = new DefaultCacheManager(true);
		sessions = cacheManager.getCache("TISM");
	}
	
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
	}

	@Override
	protected synchronized void stopInternal() throws LifecycleException {
		super.stopInternal();
	}

	
}
