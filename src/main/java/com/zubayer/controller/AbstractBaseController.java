package com.zubayer.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

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

	protected String pageTitle = null;

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
		List<Xscreens> list = xscreensRepo.findAllByXtypeAndZid("Module", sessionManager.getBusinessId());
		list.sort(Comparator.comparing(Xscreens::getXsequence));

//		list.forEach(f -> {
//			List<Xscreens> menuLists = xscreensRepo.findAllByXtypeAndPxscreenAndZid("Screen", f.getXscreen(), sessionManager.getBusinessId());
//			f.setSubMenus(menuLists);
//			f.getSubMenus().sort(Comparator.comparing(Xscreens::getXsequence));
//		});
		subMenuBuilder(list);

		return list;
	}

	private void subMenuBuilder(List<Xscreens> list) {
		for(Xscreens screen : list) {
			List<Xscreens> menuLists = xscreensRepo.findAllByXtypeAndPxscreenAndZid("Screen", screen.getXscreen(), sessionManager.getBusinessId());
			screen.setSubMenus(menuLists);
			screen.getSubMenus().sort(Comparator.comparing(Xscreens::getXsequence));

			subMenuBuilder(screen.getSubMenus());
		}
	}
}
