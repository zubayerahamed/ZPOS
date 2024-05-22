package com.zubayer.service;

import java.util.List;

import com.zubayer.entity.Item;
import com.zubayer.enums.DatatableSortOrderType;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
public interface ItemService {

	public List<Item> LMD16(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam);
	public int LMD16(String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam);
}
