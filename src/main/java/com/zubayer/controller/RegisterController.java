package com.zubayer.controller;

import java.util.Date;
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

import com.zubayer.entity.Users;
import com.zubayer.model.RegisterAccount;
import com.zubayer.repository.UsersRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
@Slf4j
@Controller
@RequestMapping("/register")
public class RegisterController extends BaseController {

	private static final String REGISTER_PAGE_PATH = "register";

	@Autowired private UsersRepo usersRepo;

	@GetMapping
	public String loadRegisterPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		if (OUTSIDE_USERS_NAME.equalsIgnoreCase(username)) {
			if (sessionManager.getFromMap("FAKE_LOGIN_USER") != null) {
				return "redirect:/business";
			}

			model.addAttribute("pageTitle", "Register");
			log.debug("Register page called at {}", new Date());
			return REGISTER_PAGE_PATH;
		}

		return "redirect:/";
	}

	@PostMapping
	public @ResponseBody Map<String, Object> doFakeLogin(RegisterAccount req) {
		if(req == null) {
			responseHelper.setErrorStatusAndMessage("Email or Password is empty");
			return responseHelper.getResponse();
		}

		if(StringUtils.isBlank(req.getEmail())) {
			responseHelper.setErrorStatusAndMessage("Email address required");
			return responseHelper.getResponse();
		}

		if(StringUtils.isBlank(req.getPassword())) {
			responseHelper.setErrorStatusAndMessage("Password required");
			return responseHelper.getResponse();
		}

		if(StringUtils.isBlank(req.getCpassword())) {
			responseHelper.setErrorStatusAndMessage("Confirm Password required");
			return responseHelper.getResponse();
		}

		if(!req.getPassword().equals(req.getCpassword())) {
			responseHelper.setErrorStatusAndMessage("Confirm Password not matched");
			return responseHelper.getResponse();
		}

		Optional<Users> userOp = usersRepo.findByEmail(req.getEmail());
		if(userOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Account already exist with this email address");
			return responseHelper.getResponse();
		}

		Users user = new Users();
		user.setEmail(req.getEmail());
		user.setXpassword(req.getPassword());
		user.setZactive(true);
		user.setZadmin(true);
		user = usersRepo.save(user);
		if(user == null || user.getId() == null) {
			responseHelper.setErrorStatusAndMessage("Failed to crate account");
			return responseHelper.getResponse();
		}

		responseHelper.setSuccessStatusAndMessage("Account created successfully");
		responseHelper.setRedirectUrl("/login");
		return responseHelper.getResponse();
	}
	
}
