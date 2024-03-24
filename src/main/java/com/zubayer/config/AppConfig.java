package com.zubayer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Data;

/**
 * @author Zubayer Ahamed
 * @since Dec 27, 2020
 */
@Data
@Service
public class AppConfig {

	@Value("${app.version}")
	private String appVersion;
}
