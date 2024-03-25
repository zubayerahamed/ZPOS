package com.zubayer.controller;

import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.zubayer.config.AppConfig;
import com.zubayer.model.ResponseHelper;
import com.zubayer.service.ZSessionManager;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
public class BaseController {

	protected static final String OUTSIDE_USERS_NAME = "anonymousUser";
	protected static final String ALL_BUSINESS = "ALL_ACCESSABLE_BUSINESS";
	protected static final String ERROR = "Error is : {}, {}"; 
	protected static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	protected static final SimpleDateFormat SDF2 = new SimpleDateFormat("E, dd-MMM-yyyy");
	protected static final SimpleDateFormat SDF3 = new SimpleDateFormat("E, dd-MMM-yyyy HH:mm:ss");
	protected static final String UTF_CODE = "UTF-8";

	@Autowired protected ApplicationContext appContext;
	@Autowired protected Environment env;
	@Autowired protected ZSessionManager sessionManager;
	@Autowired protected AppConfig appConfig;
	@Autowired protected ResponseHelper responseHelper;

	@ModelAttribute("appVersion")
	protected String appVersion() {
		return appConfig.getAppVersion();
	}
}
