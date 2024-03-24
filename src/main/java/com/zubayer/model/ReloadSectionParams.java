package com.zubayer.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
@Data
@AllArgsConstructor
public class ReloadSectionParams {
	private String key;
	private String value;
}
