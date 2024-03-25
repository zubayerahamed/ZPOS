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
import com.zubayer.model.FakeLogin;
import com.zubayer.repository.UsersRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
@Slf4j
@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {

	private static final String FAKE_LOGAIN_PAGE_PATH = "login";

	@Autowired private UsersRepo usersRepo;

	@GetMapping
	public String loadLoginPage(Model model) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		if (OUTSIDE_USERS_NAME.equalsIgnoreCase(username)) {
			if (sessionManager.getFromMap("FAKE_LOGIN_USER") != null) {
				return "redirect:/business";
			}

			model.addAttribute("pageTitle", "Login");
			log.debug("Login page called at {}", new Date());
			return FAKE_LOGAIN_PAGE_PATH;
		}

		return "redirect:/";
	}

	@PostMapping("/fakelogin")
	public @ResponseBody Map<String, Object> doFakeLogin(FakeLogin fakeLoginUser) {
		if (fakeLoginUser == null || StringUtils.isBlank(fakeLoginUser.getUsername()) || StringUtils.isBlank(fakeLoginUser.getPassword())) {
			responseHelper.setErrorStatusAndMessage("Username or password is empty");
			return responseHelper.getResponse();
		}

		Optional<Users> users = usersRepo.findByEmailAndXpasswordAndZactive(fakeLoginUser.getUsername(), fakeLoginUser.getPassword(), Boolean.TRUE);
		if (!users.isPresent()) {
			responseHelper.setErrorStatusAndMessage("User not found in the system, please try again with appropriate username and password");
			return responseHelper.getResponse();
		}

		sessionManager.addToMap("FAKE_LOGIN_USER", users.get());

		responseHelper.setSuccessStatusAndMessage("Logged in successfully");
		responseHelper.setRedirectUrl("/business");
		return responseHelper.getResponse();
	}
}
