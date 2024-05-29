package com.zubayer.controller.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zubayer.annotation.RestApiController;
import com.zubayer.util.POSUtil;

@RestApiController
@RequestMapping("/api/v1/validate")
public class ValidatePOSController {

	@Autowired private POSUtil posUtil;

	@GetMapping(value = "/key")
	public boolean validateKey(@RequestParam String key) {
		return posUtil.validateKey(key);
	}
}
