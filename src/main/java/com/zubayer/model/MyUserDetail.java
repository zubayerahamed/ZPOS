package com.zubayer.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.zubayer.entity.Users;
import com.zubayer.entity.Zbusiness;

import lombok.Data;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Data
public class MyUserDetail implements UserDetails {

	private static final long serialVersionUID = -8257425089705103719L;

	private Integer id;
	private String email;
	private String xpassword;
	private Integer zid;
	private boolean admin;
	private Integer xstaff;
	private Zbusiness zbusiness;
	private String xprofile;
	private Integer xwh;
	private String employeeName;
	private boolean switchBusiness;

	private boolean accountExpired;
	private boolean credentialExpired;
	private boolean accountLocked;
	private boolean enabled;
	private String roles;
	private List<GrantedAuthority> authorities;

	public MyUserDetail(Users user, Zbusiness business) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.xpassword = user.getXpassword();
		this.zid = business.getZid();
		this.zbusiness = business;
		this.admin = Boolean.TRUE.equals(user.getZadmin());
		this.xstaff = user.getXstaff();
		this.xprofile = user.getXprofile();
		this.xwh = user.getXwh();
		this.employeeName = user.getEmployee();
		if(user.getXswbusiness() == null) {
			this.switchBusiness = false;
		} else {
			this.switchBusiness = user.getXswbusiness();
		}

		this.accountExpired = false;
		this.credentialExpired = false;
		this.accountLocked = !Boolean.TRUE.equals(user.getZactive());
		this.enabled = Boolean.TRUE.equals(user.getZactive());
		this.roles = StringUtils.isBlank(user.getRoles()) ? com.zubayer.enums.UserRole.SUBSCRIBER.getCode() : user.getRoles();
		this.authorities = Arrays.stream(roles.split(",")).map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	public String getEmployeeName() {
		return this.employeeName;
	}

	public Integer getStore() {
		return this.xwh;
	}

	public void setZbusiness(Zbusiness zbusiness) {
		this.zbusiness = zbusiness;
	}

	public Zbusiness getZbusiness() {
		return zbusiness;
	}

	public String getXprofile() {
		return this.xprofile;
	}
	
	public Integer getXstaff() {
		return this.xstaff;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public String getRoles() {
		return roles;
	}

	@Override
	public String getPassword() {
		return this.xpassword;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !credentialExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !accountLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return !credentialExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public Integer getBusinessId() {
		return zid;
	}

	public boolean isAdmin() {
		return admin;
	}

	public boolean isSwitchBusiness() {
		return switchBusiness;
	}
}
