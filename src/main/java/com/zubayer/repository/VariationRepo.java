package com.zubayer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Variation;
import com.zubayer.entity.pk.VariationPK;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Repository
public interface VariationRepo extends JpaRepository<Variation, VariationPK> {

	Optional<Variation> findByZidAndXname(Integer zid, String xname);

}
