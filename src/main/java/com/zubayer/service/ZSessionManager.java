package com.zubayer.service;

import com.zubayer.entity.Business;
import com.zubayer.model.MyUserDetail;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
public interface ZSessionManager {

	public void addToMap(String key, Object value);

	public Object getFromMap(String key);

	public void removeFromMap(String key);

	public Long getBusinessId();

	public Business getBusiness();

	public MyUserDetail getLoggedInUserDetails();
}
