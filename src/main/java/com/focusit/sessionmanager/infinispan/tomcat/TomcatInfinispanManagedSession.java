package com.focusit.sessionmanager.infinispan.tomcat;

import java.beans.PropertyChangeSupport;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.catalina.Manager;
import org.apache.catalina.SessionListener;
import org.apache.catalina.session.StandardSession;

/**
 * Session managed by InfinispanSessionManager.
 * In general, any custom session manager can't work with tomcat's standard session. 
 * Because custom session manager needs to track some transient fields by itself.
 * In case of infinispan any transient field will be erased upon the cache replication.
 * So, to get rid on null pointers this class reinitialize transient fields to avoid NullPointerException
 * Moreover, when infinispan started to work in a cluster, it is very important to send changes back to cache to get correct objects in cache.
 * E.g. if you do cache.get(key).setId(uuid). This modification (changing id) will be invisible for all cluster nodes. 
 * The reason is that Infinispan can't detect object modifications to replicate them implicitly.
 * This class makes wrappers on all setters of StandardSession to avoid wrong replication. 
 * @author Denis V. Kirpichenkov
 *
 */
public class TomcatInfinispanManagedSession extends StandardSession {

	private static final String sessionName = "TomcatInfinispanManagedSession/1.0";

	public TomcatInfinispanManagedSession(Manager manager) {
		super(manager);
		container = manager.getContainer().getName();
	}
	
	private String container;
	
	private static final long serialVersionUID = -2661862476157128598L;

    @Override
    public String getInfo() {
        return sessionName;
    }

    private void initTransientFieldsAfterSerialization(){		
		if(notes==null){
			notes = new Hashtable<String, Object>();
		}
		
		if(listeners==null){
			listeners = new ArrayList<SessionListener>();
		}
		
		if(support==null){
			support = new PropertyChangeSupport(this);
		}
		
		if(manager==null){
			manager = TomcatInfinispanSessionManager.getManagerByContainer(container);
		}
	}

    private void updateSessionInInfinispan(){
    	if(manager instanceof TomcatInfinispanSessionManager){
    		TomcatInfinispanSessionManager ism = (TomcatInfinispanSessionManager) manager;
    		ism.updateSession(this);
    	}
    }
    
	@Override
	public Object getNote(String name) {
		initTransientFieldsAfterSerialization();
		return super.getNote(name);
	}
	
    @Override
    public Iterator<String> getNoteNames() {
		initTransientFieldsAfterSerialization();
		return super.getNoteNames();
    }
    
    @Override
    public void removeNote(String name) {
    	initTransientFieldsAfterSerialization();
    	super.removeNote(name);
    }
    
    @Override
    public void recycle() {
    	initTransientFieldsAfterSerialization();
    	super.recycle();
    }

	@Override
	public void setAuthType(String authType) {
		updateSessionInInfinispan();
		super.setAuthType(authType);
	}

	@Override
	public void setCreationTime(long time) {
		super.setCreationTime(time);
		updateSessionInInfinispan();
	}

	@Override
	public void setId(String id) {
		super.setId(id);
		updateSessionInInfinispan();
	}

	@Override
	public void setId(String id, boolean notify) {
		super.setId(id, notify);
		updateSessionInInfinispan();
	}

	@Override
	public void tellNew() {
		super.tellNew();
		updateSessionInInfinispan();
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		super.setMaxInactiveInterval(interval);
		updateSessionInInfinispan();
	}

	@Override
	public void setNew(boolean isNew) {
		super.setNew(isNew);
		updateSessionInInfinispan();
	}

	@Override
	public void setPrincipal(Principal principal) {
		super.setPrincipal(principal);
		updateSessionInInfinispan();
	}

	@Override
	public void setValid(boolean isValid) {
		super.setValid(isValid);
		updateSessionInInfinispan();
	}

	@Override
	public void access() {
		super.access();
		updateSessionInInfinispan();
	}

	@Override
	public void activate() {
		super.activate();
		updateSessionInInfinispan();
	}

	@Override
	public void setNote(String name, Object value) {
		super.setNote(name, value);
		updateSessionInInfinispan();
	}

	@Override
	public void removeAttribute(String name) {
		super.removeAttribute(name);
		updateSessionInInfinispan();
	}

	@Override
	public void removeAttribute(String name, boolean notify) {
		super.removeAttribute(name, notify);
		updateSessionInInfinispan();
	}

	@Override
	public void setAttribute(String name, Object value) {
		super.setAttribute(name, value);
		updateSessionInInfinispan();
	}

	@Override
	public void setAttribute(String name, Object value, boolean notify) {
		super.setAttribute(name, value, notify);
		updateSessionInInfinispan();
	}

    /**
     * Return a string representation of this object.
     */
    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("TomcatInfinispanManagedSession[");
        sb.append(id);
        sb.append("]");
        return (sb.toString());

    }
}
