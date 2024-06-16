package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.ItemAddons;
import com.zubayer.entity.pk.ItemAddonsPK;

/**
 * @author Zubayer Ahamed
 * @since Jun 16, 2024
 */
@Repository
public interface ItemAddonsRepo extends JpaRepository<ItemAddons, ItemAddonsPK> {

	List<ItemAddons> findAllByZid(Integer zid);

	List<ItemAddons> findAllByZidAndXitem(Integer zid, Integer xitem);
}
