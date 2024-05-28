package com.zubayer.controller.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zubayer.util.POSUtil;

@RestController
@RequestMapping("/api/v1")
public class ValidatePOSController {

	@Autowired private POSUtil posUtil;

	@GetMapping("/validate-key")
	public boolean validateKey(@RequestParam String key) {
		return posUtil.validateKey(key);
	}
}
