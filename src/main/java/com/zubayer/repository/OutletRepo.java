package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Outlet;
import com.zubayer.entity.pk.OutletPK;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202101068
 */
@Repository
public interface OutletRepo extends JpaRepository<Outlet, OutletPK> {

	List<Outlet> findAllByZid(Integer zid);
}
