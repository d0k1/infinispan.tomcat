package com.focusit.sessionmanager.infinispan.tomcat.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.servlet.ServletContext;

public class TomcatSessionManagerOverrider {

	public static Object contextOverrride(ServletContext servletContext) throws Exception {
		Object ctx = servletContext;
		if(!ctx.getClass().getName().contains("org.apache.catalina.core.ApplicationContextFacade"))
			return null;
		
		try {
			Field f = ctx.getClass().getDeclaredField("context");
			f.setAccessible(true);
			Object appCtx = f.get(ctx);
			Field f1 = appCtx.getClass().getDeclaredField("context");
			f1.setAccessible(true);
			Object stdContext = f1.get(appCtx);
			Field f2 = stdContext.getClass().getDeclaredField("manager");
			f2.setAccessible(true);
			Object mgr = f2.get(stdContext);
			
			if(mgr.getClass().getName().contains("com.focusit.sessionmanager.infinispan.tomcat.TomcatInfinispanSessionManager")){
				return mgr;
			}
			
			Method m = mgr.getClass().getMethod("getContainer");
			Object container = m.invoke(mgr);
			
			Class<?> infManagerCls = Class.forName("com.focusit.sessionmanager.infinispan.tomcat.TomcatInfinispanSessionManager");
			Object infManager = infManagerCls.newInstance();

			Method m1 = infManager.getClass().getMethod("setGenericContainer", Object.class);
			m1.invoke(infManager, container);
			
			Method m2 = infManager.getClass().getMethod("start");
			m2.invoke(infManager);
			
			f2.set(stdContext, infManager);
			
			return infManager;
		} catch (Exception e) {
			throw e;
		}
	}
	
}
