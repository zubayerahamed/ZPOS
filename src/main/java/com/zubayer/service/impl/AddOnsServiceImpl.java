package com.zubayer.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zubayer.entity.AddOns;
import com.zubayer.enums.DatatableSortOrderType;
import com.zubayer.service.AddOnsService;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Service
public class AddOnsServiceImpl extends AbstractService implements AddOnsService {

	@Override
	public List<AddOns> LMD14(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam) {
		searchText = sanitizeSearch(searchText);

		StringBuilder sql = new StringBuilder();
		sql.append(selectClause())
		.append(fromClause("addons im"))
		.append(whereClause(searchText, suffix, dependentParam))
		.append(orderbyClause(orderBy, orderType.name()))
		.append(limitAndOffsetClause(limit, offset));

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
		List<AddOns> list = new ArrayList<>();
		result.stream().forEach(row -> list.add(constractObj(row)));

		return list;
	}

	@Override
	public int LMD14(String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam) {
		searchText = sanitizeSearch(searchText);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) ")
		.append(fromClause("category im"))
		.append(whereClause(searchText, suffix, dependentParam));
		return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
	}

	private AddOns constractObj(Map<String, Object> row) {
		AddOns obj = new AddOns();
		obj.setZid((Integer) row.get("zid"));
		obj.setXcode((Integer) row.get("xcode"));
		obj.setXname((String) row.get("xname"));
		obj.setXprice((BigDecimal) row.get("xprice"));
		obj.setZactive((Boolean) row.get("zactive"));
		return obj;
	}

	private StringBuilder selectClause() {
		return new StringBuilder("SELECT im.* ");
	}

	private StringBuilder fromClause(String tableName) {
		return new StringBuilder(" FROM " + tableName + " ");
	}

	private StringBuilder whereClause(String searchText, int suffix, String dependentParam) {
		StringBuilder sql = new StringBuilder(" WHERE im.zid="+ sessionManager.getBusinessId()+ " ");

		if (searchText == null || searchText.isEmpty()) return sql;

		return sql.append(" AND (im.xcode LIKE '%" + searchText+ "%'" +  
				" OR im.xname LIKE '%" + searchText + "%') ");
	}


}
