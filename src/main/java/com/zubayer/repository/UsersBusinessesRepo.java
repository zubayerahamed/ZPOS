package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.UsersBusinesses;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Repository
public interface UsersBusinessesRepo extends JpaRepository<UsersBusinesses, Long> {

	public List<UsersBusinesses> findAllByUserId(Long userId);
}
