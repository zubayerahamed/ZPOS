package com.zubayer.enums;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
public enum BusinessType {

	RESTURANT_CENTRAL("Central"), RESTURANT_FOOD_COURT("Food Court"), RESTURANT("Restaurant");

	private final String desc;

	private BusinessType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}
}
