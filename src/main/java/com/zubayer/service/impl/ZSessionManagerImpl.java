package com.zubayer.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import com.zubayer.entity.Business;
import com.zubayer.model.MyUserDetail;
import com.zubayer.service.ZSessionManager;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
@Service
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ZSessionManagerImpl implements ZSessionManager {

	private Map<String, Object> sessionMap;

	public ZSessionManagerImpl() {
		this.sessionMap = new HashMap<>();
	}

	@Override
	public void addToMap(String key, Object value) {
		sessionMap.put(key, value);
	}

	@Override
	public Object getFromMap(String key) {
		return sessionMap.get(key);
	}

	@Override
	public void removeFromMap(String key) {
		if (sessionMap.containsKey(key))
			sessionMap.remove(key);
	}


	@Override
	public Long getBusinessId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Business getBusiness() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MyUserDetail getLoggedInUserDetails() {
		// TODO Auto-generated method stub
		return null;
	}

}
