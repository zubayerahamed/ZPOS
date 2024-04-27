package com.zubayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.pk.AddOnsPK;

/**
 * @author Zubayer Ahamed
 * @since Apr 26, 2024
 * CSE202101068
 */
@Repository
public interface AddOnsRepo extends JpaRepository<AddOns, AddOnsPK>{

}
