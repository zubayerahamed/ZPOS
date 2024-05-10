package com.zubayer.entity;

import org.apache.commons.lang3.StringUtils;

import com.zubayer.enums.SubmitFor;
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
 * CSE202401068
 */
@Data
@Entity
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
public class Users extends AbstractModel<Integer> {

	private static final long serialVersionUID = -7661050792948007201L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "email", length = 255, unique = true)
	private String email;

	@Column(name = "xpassword", length = 255)
	private String xpassword;

	@Column(name = "xpasswordold", length = 20)
	private String xpasswordold;

	@Column(name = "xwh")
	private Integer xwh;

	@Column(name = "xstaff")
	private Integer xstaff;

	@Column(name = "xprofile", length = 20)
	private String xprofile;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Column(name = "sysadmin", length = 1)
	private Boolean sysadmin;

	@Column(name = "zadmin", length = 1)
	private Boolean zadmin;

	@Column(name = "subscriber", length = 1)
	private Boolean subscriber;

	@Column(name = "xswbusiness", length = 1)
	private Boolean xswbusiness = Boolean.FALSE;

	@Transient
	private String roles;

	public String getRoles() {
		this.roles = "";
		if (Boolean.TRUE.equals(sysadmin))
			roles += UserRole.SYSTEM_ADMIN.getCode() + ',';

		if (Boolean.TRUE.equals(zadmin))
			roles += UserRole.ZADMIN.getCode() + ',';

		if (Boolean.TRUE.equals(subscriber))
			roles += UserRole.SUBSCRIBER.getCode() + ',';

		if (StringUtils.isBlank(roles))
			return roles;

		int lastComma = roles.lastIndexOf(',');
		String finalString = roles.substring(0, lastComma);
		roles = finalString;
		return roles;
	}

	@Transient
	private String store;

	@Transient
	private String employee;

	@Transient
	private String businessName;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Users getDefaultInstance() {
		Users obj = new Users();
		obj.setSubmitFor(SubmitFor.INSERT);
		obj.setZactive(Boolean.FALSE);
		obj.setZactive(Boolean.TRUE);
		return obj;
	}
}
