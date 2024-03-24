package com.zubayer.entity;

import org.apache.commons.lang3.StringUtils;

import com.zubayer.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024 
 * CSE202101068
 */
@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class Users extends AbstractModel<Long> {

	private static final long serialVersionUID = -7661050792948007201L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email", length = 255, unique = true)
	private String email;

	@Column(name = "xpassword", length = 255)
	private String xpassword;

	@Column(name = "active", length = 1)
	private Boolean active = Boolean.TRUE;

	@Column(name = "isAdmin", length = 1)
	private Boolean isAdmin;

	@Transient
	private String roles;

	public String getRoles() {
		this.roles = "";
		if (StringUtils.isBlank(roles)) return UserRole.ROLE_ADMIN.name();
		if (Boolean.TRUE.equals(this.isAdmin)) roles += UserRole.ROLE_ADMIN.name() + ',';

		int lastComma = roles.lastIndexOf(',');
		String finalString = roles.substring(0, lastComma);
		roles = finalString;
		return roles;
	}
}
