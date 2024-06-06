package com.zubayer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.AddOns;
import com.zubayer.entity.pk.AddOnsPK;

/**
 * @author Zubayer Ahamed
 * @since Apr 26, 2024
 * CSE202401068
 */
@Repository
public interface AddOnsRepo extends JpaRepository<AddOns, AddOnsPK>{

	List<AddOns> findAllByZid(Integer zid);
	
	Optional<AddOns> findByZidAndXname(Integer zid, String xname);

}
