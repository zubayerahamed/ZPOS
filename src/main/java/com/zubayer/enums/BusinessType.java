package com.zubayer.enums;

/**
 * @author Zubayer Ahamed
 * @since Mar 24, 2024
 * CSE202101068
 */
public enum BusinessType {

	RESTURANT_CENTRAL("Central"), RESTURANT_FOOD_COURT("Food Court"), RESTURANT("Restaurant");

	private String des;

	private BusinessType(String des) {
		this.des = des;
	}

	public String getDes() {
		return this.des;
	}
}
