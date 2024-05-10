package com.zubayer.service;

import java.util.List;

import com.zubayer.entity.AddOns;
import com.zubayer.enums.DatatableSortOrderType;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
public interface AddOnsService {

	public List<AddOns> LMD14(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam);
	public int LMD14(String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam);
}
