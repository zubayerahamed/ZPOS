package com.zubayer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Controller
@RequestMapping({"/", "/home"})
public class HomeController extends AbstractBaseController {

	@Override
	protected String pageTitle() {
		return "Home";
	}

	@GetMapping
	public String loadHomePage() {
		return "index";
	}
}
