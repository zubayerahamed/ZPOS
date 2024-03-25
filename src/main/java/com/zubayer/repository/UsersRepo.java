package com.zubayer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Users;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024 
 * CSE202101068
 */
@Repository
public interface UsersRepo extends JpaRepository<Users, Long> {

	Optional<Users> findByEmail(String email);

	Optional<Users> findByEmailAndXpasswordAndZactive(String email, String xpassword, Boolean zactive);
}
