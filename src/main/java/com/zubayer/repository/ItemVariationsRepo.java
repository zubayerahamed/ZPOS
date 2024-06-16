package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.ItemVariations;
import com.zubayer.entity.pk.ItemVariationsPK;

/**
 * @author Zubayer Ahamed
 * @since Jun 16, 2024
 */
@Repository
public interface ItemVariationsRepo extends JpaRepository<ItemVariations, ItemVariationsPK> {

	List<ItemVariations> findAllByZid(Integer zid);

	List<ItemVariations> findAllByZidAndXitem(Integer zid, Integer xitem);
}
