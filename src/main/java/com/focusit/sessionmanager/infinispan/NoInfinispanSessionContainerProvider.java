package com.focusit.sessionmanager.infinispan;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Example session container provider
 * @author Denis V. Kirpichenkov
 *
 */
public class NoInfinispanSessionContainerProvider {
	
	@SuppressWarnings("rawtypes")
	public static Map get(){
		return new ConcurrentHashMap();
	}
}
