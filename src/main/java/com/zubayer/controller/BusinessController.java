package com.zubayer.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
