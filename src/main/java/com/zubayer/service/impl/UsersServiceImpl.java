package com.zubayer.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zubayer.entity.Users;
import com.zubayer.model.MyUserDetail;
import com.zubayer.repository.UsersRepo;
import com.zubayer.service.UsersService;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024 
 * CSE202101068
 */
@Service
public class UsersServiceImpl implements UserDetailsService, UsersService {

	@Autowired
	private UsersRepo usersRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Users> userOp = usersRepo.findByEmail(username);
		if (!userOp.isPresent())
			throw new UsernameNotFoundException("User not found");

		return new MyUserDetail(userOp.get());
	}

}
