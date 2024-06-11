package com.zubayer.repository;

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

}
