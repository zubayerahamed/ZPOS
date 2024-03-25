package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.UsersZbusinesses;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Repository
public interface UsersBusinessesRepo extends JpaRepository<UsersZbusinesses, Long> {

	public List<UsersZbusinesses> findAllByUid(Integer uid);
}
