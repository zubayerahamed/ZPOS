package com.zubayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Zbusiness;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Repository
public interface ZbusinessRepo extends JpaRepository<Zbusiness, Integer> {

}
