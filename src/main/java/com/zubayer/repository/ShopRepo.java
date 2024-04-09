package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Shop;
import com.zubayer.entity.pk.ShopPK;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202101068
 */
@Repository
public interface ShopRepo extends JpaRepository<Shop, ShopPK>{

	List<Shop> findAllByZid(Integer zid);

	List<Shop> findAllByZidAndOutletId(Integer zid, Integer outletId);
}
