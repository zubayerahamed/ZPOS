package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

	List<VariationDetail> findAllByZidAndXcode(Integer zid, Integer xcode);

	@Query(value = "select isnull(max(COALESCE(xrow,0)) + 1, 1) from variation_detail where zid=?1 and xcode=?2", nativeQuery = true)
	public Integer getNextAvailableRow(Integer zid, Integer xcode);

}
