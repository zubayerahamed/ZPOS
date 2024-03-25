package com.zubayer.service;

import java.util.List;

import com.zubayer.entity.Xscreens;
import com.zubayer.enums.DatatableSortOrderType;

/**
 * @author Zubayer Ahamed
 * @since Jul 3, 2023
 */
public interface XscreensService {

	public List<Xscreens> getAll(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText);

	public int countAll(String orderBy, DatatableSortOrderType orderType, String searchText);
}
