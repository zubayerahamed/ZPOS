package com.zubayer.controller;

import org.springframework.web.bind.annotation.ModelAttribute;

import com.zubayer.model.MyUserDetail;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024 
 * CSE202101068
 */
public abstract class AbstractBaseController extends BaseController {

	@ModelAttribute("pageTitle")
	protected abstract String pageTitle();

	@ModelAttribute("loggedInUser")
	protected MyUserDetail loggedInUser() {
		return sessionManager.getLoggedInUserDetails();
	}

	protected boolean isAjaxRequest(HttpServletRequest request) {
		String requestedWithHeader = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equals(requestedWithHeader);
	}

}
