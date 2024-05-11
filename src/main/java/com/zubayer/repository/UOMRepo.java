package com.zubayer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.UOM;
import com.zubayer.entity.pk.UOMPK;

/**
 * @author Zubayer Ahamed
 * @since May 11, 2024
 * CSE202401068
 */
@Repository
public interface UOMRepo extends JpaRepository<UOM, UOMPK> {

	Optional<UOM> findByZidAndXname(Integer zid, String xname);

	List<UOM> findAllByZid(Integer zid);

}
