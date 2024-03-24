package com.zubayer.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReloadSection {

	public ReloadSection(String id, String url) {
		this.id = id;
		this.url = url;
	}

	private String id;
	private String url;
	private List<ReloadSectionParams> postData = new ArrayList<>();
}
