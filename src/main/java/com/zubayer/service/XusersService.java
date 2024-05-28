package com.zubayer.service;

import java.util.List;

import com.zubayer.entity.Xusers;
import com.zubayer.enums.DatatableSortOrderType;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
public interface XusersService {

	public List<Xusers> LAD17(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam);
	public int LAD17(String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam);
}
