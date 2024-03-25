package com.zubayer.service.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zubayer.entity.Business;
import com.zubayer.entity.Users;
import com.zubayer.model.MyUserDetail;
import com.zubayer.repository.BusinessRepo;
import com.zubayer.repository.UsersRepo;
import com.zubayer.service.UsersService;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024 
 * CSE202101068
 */
@Service
public class UsersServiceImpl implements UserDetailsService, UsersService {

	@Autowired private UsersRepo usersRepo;
	@Autowired private BusinessRepo businessRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (StringUtils.isBlank(username)) {
			throw new UsernameNotFoundException("User not found in the system");
		}

		String[] token = username.split("\\|");
		if (token.length < 2)
			throw new UsernameNotFoundException("User not found in the system");

		String email = token[0];
		Long businessId = Long.valueOf(token[1]);

		Optional<Users> userOp = usersRepo.findByEmail(email);
		if (!userOp.isPresent())
			throw new UsernameNotFoundException("User not found");

		Optional<Business> businessOp = businessRepo.findById(businessId);
		if(!businessOp.isPresent() || Boolean.FALSE.equals(businessOp.get().getActive())) {
			throw new UsernameNotFoundException("Business not active");
		}

		return new MyUserDetail(userOp.get(), businessOp.get());
	}

}
