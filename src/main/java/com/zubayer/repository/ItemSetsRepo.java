package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.ItemSets;
import com.zubayer.entity.pk.ItemSetsPK;

/**
 * @author Zubayer Ahamed
 * @since Jun 16, 2024
 */
@Repository
public interface ItemSetsRepo extends JpaRepository<ItemSets, ItemSetsPK> {

	List<ItemSets> findAllByZid(Integer zid);

	List<ItemSets> findAllByZidAndXitem(Integer zid, Integer xitem);
}
