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

import com.zubayer.entity.Zbusiness;
import com.zubayer.entity.Users;
import com.zubayer.entity.UsersZbusinesses;
import com.zubayer.repository.ZbusinessRepo;
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
	@Autowired private ZbusinessRepo businessRepo;

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

		List<Zbusiness> businesses = new ArrayList<>();
		List<UsersZbusinesses> ubList = ubRepo.findAllByUid(user.getId());
		if(ubList != null && !ubList.isEmpty()) {
			for(UsersZbusinesses ub : ubList) {
				Optional<Zbusiness> businessOp = businessRepo.findById(ub.getZid());
				if(businessOp.isPresent()) {
					Zbusiness b = businessOp.get();
					b.setZemail(user.getEmail());
					b.setXpassword(user.getXpassword());
					businesses.add(businessOp.get());
				}
			}
		}

		businesses.sort(Comparator.comparing(Zbusiness::getZorg));
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
	public @ResponseBody Map<String, Object> loadBusinessCreatePage(Zbusiness business) {
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

		if(StringUtils.isBlank(business.getZorg())) {
			responseHelper.setErrorStatusAndMessage("Business name required");
			return responseHelper.getResponse();
		}

		if(business.getBusinessType() == null) {
			responseHelper.setErrorStatusAndMessage("Business type required");
			return responseHelper.getResponse();
		}

		business.setZactive(true);
		business = businessRepo.save(business);
		if(business == null || business.getZid() == null) {
			responseHelper.setErrorStatusAndMessage("Business creation failed");
			return responseHelper.getResponse();
		}

		UsersZbusinesses ub = new UsersZbusinesses();
		ub.setUid(user.getId());
		ub.setZid(business.getZid());
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
