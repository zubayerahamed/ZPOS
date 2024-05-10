package com.zubayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.VariationDetail;
import com.zubayer.entity.pk.VariationDetailPK;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Repository
public interface VariationDetailRepo extends JpaRepository<VariationDetail, VariationDetailPK> {

}
