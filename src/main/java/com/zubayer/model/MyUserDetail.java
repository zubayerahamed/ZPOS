package com.zubayer.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.zubayer.entity.Users;

import lombok.Data;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Data
public class MyUserDetail implements UserDetails {

	private static final long serialVersionUID = -8257425089705103719L;

	private Long id;
	private String email;
	private String password;
	private String roles;
	private List<GrantedAuthority> authorities;

	public MyUserDetail(Users user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.password = user.getXpassword();
		this.roles = user.getRoles();
		this.authorities = Arrays.stream(roles.split(","))
				.map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
