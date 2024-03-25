package com.zubayer.enums;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
public enum UserRole {

	SYSTEM_ADMIN("ROLE_SYSTEM_ADMIN", "System Admin", 0),
	ZADMIN("ROLE_ZADMIN", "Admin", 1),
	SUBSCRIBER("ROLE_SUBSCRIBER", "Subscriber", 100);

	private String code;
	private String roleName;
	private int priority;

	private UserRole(String code, String roleName, int priority) {
		this.code = code;
		this.roleName = roleName;
		this.priority = priority;
	}

	public String getCode() {
		return code;
	}

	public String getRoleName() {
		return roleName;
	}

	public int getPriority() {
		return priority;
	}
}
