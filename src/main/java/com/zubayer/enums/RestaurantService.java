package com.zubayer.enums;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202101068
 */
public enum RestaurantService {

	DINE_IN("Dine In"), DELIVERY("Delivery"), PICKUP("Pickup");

	private final String desc;

	private RestaurantService(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return this.desc;
	}
}
