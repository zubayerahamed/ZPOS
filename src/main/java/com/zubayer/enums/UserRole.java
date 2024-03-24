package com.zubayer.enums;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
public enum UserRole {

	ROLE_ADMIN("Admin"), 
	ROLE_CASHIER("Cashier"), 
	ROLE_WAITER("Waiter"), 
	ROLE_GENERAL("General");

	private String des;

	private UserRole(String des) {
		this.des = des;
	}

	public String getDes() {
		return this.des;
	}
}
