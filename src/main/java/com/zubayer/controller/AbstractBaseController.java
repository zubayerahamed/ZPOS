package com.zubayer.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.zubayer.entity.Profiledt;
import com.zubayer.entity.Users;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.Zbusiness;
import com.zubayer.entity.validator.ModelValidator;
import com.zubayer.model.MyUserDetail;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Validator;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024 
 * CSE202101068
 */
public abstract class AbstractBaseController extends BaseController {

	@Autowired protected ModelValidator modelValidator;
	@Autowired protected Validator validator;

	@ModelAttribute("pageTitle")
	protected abstract String pageTitle();

	@ModelAttribute("loggedInUser")
	protected MyUserDetail loggedInUser() {
		return sessionManager.getLoggedInUserDetails();
	}

	@ModelAttribute("loggedInZbusiness")
	protected Zbusiness loggedInZbusiness() {
		return sessionManager.getLoggedInUserDetails().getZbusiness();
	}

	protected boolean isAjaxRequest(HttpServletRequest request) {
		String requestedWithHeader = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equals(requestedWithHeader);
	}

	@ModelAttribute("otherBusinesses")
	protected List<Users> otherZbusinesses() {

		List<Users> selectedBusinessWiseUser = new ArrayList<>();
		// Business active users

		return selectedBusinessWiseUser;
	}

	@ModelAttribute("sidebarMenus")
	protected List<Xscreens> menusList(){
		List<Xscreens> list = xscreensRepo.findAllByXtypeAndZid("Screen", sessionManager.getBusinessId());
		list.sort(Comparator.comparing(Xscreens::getXsequence));

		if(sessionManager.getLoggedInUserDetails().isAdmin()) return list;

		// Filter menus, if uesr dont have access
		String xprofile = sessionManager.getLoggedInUserDetails().getXprofile();
		if(StringUtils.isNotBlank(xprofile)) {
			List<Profiledt> profildtList = profiledtRepo.findAllByXprofileAndZid(xprofile, sessionManager.getBusinessId());
			if(profildtList == null || profildtList.isEmpty()) return Collections.emptyList();

			// Create a map from full list first
			Map<String, Xscreens> map = new HashMap<>();
			for(Xscreens screen : list) {
				map.put(screen.getXscreen(), screen);
			}

			List<Xscreens> accessableList = new ArrayList<>();
			for(Profiledt dt : profildtList) {
				if(map.get(dt.getXscreen()) != null) {
					accessableList.add(map.get(dt.getXscreen()));
				}
			}

			accessableList.sort(Comparator.comparing(Xscreens::getXsequence));
			return accessableList;
		}

		return list;
	}
}
