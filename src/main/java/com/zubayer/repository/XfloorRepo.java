package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Xfloor;
import com.zubayer.entity.pk.XfloorPK;

/**
 * @author Zubayer Ahamed
 * @since Jun 11, 2024
 */
@Repository
public interface XfloorRepo extends JpaRepository<Xfloor, XfloorPK> {

	List<Xfloor> findAllByZid(Integer zid);

	List<Xfloor> findAllByZidAndXoutletAndXshop(Integer zid, Integer xoutlet, Integer xshop);
}
