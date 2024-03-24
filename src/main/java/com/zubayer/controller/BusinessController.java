package com.zubayer.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zubayer.entity.Business;
import com.zubayer.entity.Users;
import com.zubayer.entity.UsersBusinesses;
import com.zubayer.repository.BusinessRepo;
import com.zubayer.repository.UsersBusinessesRepo;


/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
@Controller
@RequestMapping("/business")
public class BusinessController extends BaseController {

	@Autowired private UsersBusinessesRepo ubRepo;
	@Autowired private BusinessRepo businessRepo;

	@GetMapping
	public String loadBusinessPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		if (!OUTSIDE_USERS_NAME.equalsIgnoreCase(username)) {
			return "redirect:/";
		}

		Users user = null;
		if(sessionManager.getFromMap("FAKE_LOGIN_USER") != null) {
			user = (Users) sessionManager.getFromMap("FAKE_LOGIN_USER");
		}
		if(user == null) {
			return "redirect:/";
		}

		List<Business> businesses = new ArrayList<>();
		List<UsersBusinesses> ubList = ubRepo.findAllByUserId(user.getId());
		if(ubList != null && !ubList.isEmpty()) {
			for(UsersBusinesses ub : ubList) {
				Optional<Business> businessOp = businessRepo.findById(ub.getBusinessId());
				if(businessOp.isPresent() && Boolean.TRUE.equals(businessOp.get().getActive())) {
					Business b = businessOp.get();
					b.setZemail(user.getEmail());
					b.setZpasswd(user.getXpassword());
					businesses.add(businessOp.get());
				}
			}
		}

		businesses.sort(Comparator.comparing(Business::getName));
		model.addAttribute("businesses", businesses);

		if(sessionManager.getFromMap(ALL_BUSINESS) != null) {
			sessionManager.removeFromMap(ALL_BUSINESS);
		}
		sessionManager.addToMap(ALL_BUSINESS, businesses);

		return "business-dashboard";
	}

	@GetMapping("/create")
	public String loadBusinessCreatePage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		if (!OUTSIDE_USERS_NAME.equalsIgnoreCase(username)) {
			return "redirect:/";
		}

		Users user = null;
		if(sessionManager.getFromMap("FAKE_LOGIN_USER") != null) {
			user = (Users) sessionManager.getFromMap("FAKE_LOGIN_USER");
		}
		if(user == null) {
			return "redirect:/";
		}

		return "business-create";
	}

	@PostMapping("/create")
	public @ResponseBody Map<String, Object> loadBusinessCreatePage(Business business) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		if (!OUTSIDE_USERS_NAME.equalsIgnoreCase(username)) {
			responseHelper.setErrorStatusAndMessage("Not allowed to create business");
			return responseHelper.getResponse();
		}

		Users user = null;
		if(sessionManager.getFromMap("FAKE_LOGIN_USER") != null) {
			user = (Users) sessionManager.getFromMap("FAKE_LOGIN_USER");
		}
		if(user == null) {
			responseHelper.setErrorStatusAndMessage("Not allowed to create business");
			return responseHelper.getResponse();
		}

		if(business == null) {
			responseHelper.setErrorStatusAndMessage("Business info not found");
			return responseHelper.getResponse();
		}

		if(StringUtils.isBlank(business.getName())) {
			responseHelper.setErrorStatusAndMessage("Business name required");
			return responseHelper.getResponse();
		}

		if(business.getBusinessType() == null) {
			responseHelper.setErrorStatusAndMessage("Business type required");
			return responseHelper.getResponse();
		}

		business = businessRepo.save(business);
		if(business == null || business.getId() == null) {
			responseHelper.setErrorStatusAndMessage("Business creation failed");
			return responseHelper.getResponse();
		}

		UsersBusinesses ub = new UsersBusinesses();
		ub.setUserId(user.getId());
		ub.setBusinessId(business.getId());
		ub = ubRepo.save(ub);
		if(ub == null || ub.getId() == null) {
			responseHelper.setErrorStatusAndMessage("Business creation failed");
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Business created successfully");
		responseHelper.setRedirectUrl("/business");
		return responseHelper.getResponse();
	}
}
