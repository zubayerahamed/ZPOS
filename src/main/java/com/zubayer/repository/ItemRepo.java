package com.zubayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Item;
import com.zubayer.entity.pk.ItemPK;

/**
 * @author Zubayer Ahamed
 * @since May 17, 2024
 * CSE202401068
 */
@Repository
public interface ItemRepo extends JpaRepository<Item, ItemPK> {

	
}
