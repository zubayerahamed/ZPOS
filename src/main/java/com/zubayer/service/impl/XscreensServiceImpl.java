package com.zubayer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zubayer.entity.Xscreens;
import com.zubayer.enums.DatatableSortOrderType;
import com.zubayer.service.XscreensService;

/**
 * @author Zubayer Ahamed
 * @since Jul 3, 2023
 */
@Service
public class XscreensServiceImpl extends AbstractService implements XscreensService {

	@Override
	public List<Xscreens> getAll(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText) {
		StringBuilder sql = new StringBuilder();
		sql.append(selectClause())
		.append(fromClause("xscreens"))
		.append(whereClause(searchText))
		.append(orderbyClause(orderBy, orderType.name()))
		.append(limitAndOffsetClause(limit, offset));

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
		List<Xscreens> list = new ArrayList<>();
		result.stream().forEach(row -> list.add(constractListOfXscreens(row)));

		return list;
	}

	@Override
	public int countAll(String orderBy, DatatableSortOrderType orderType, String searchText) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) ")
		.append(fromClause("xscreens"))
		.append(whereClause(searchText));
		return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
	}

	private Xscreens constractListOfXscreens(Map<String, Object> row) {
		Xscreens em = new Xscreens();
		em.setXscreen((String) row.get("xscreen"));
		em.setXtitle((String) row.get("xtitle"));
		em.setXnum((Integer) row.get("xnum"));
		em.setXtype((String) row.get("xtype"));
		em.setXicon((String) row.get("xicon"));
		em.setXsequence((Integer) row.get("xsequence"));
		em.setPxscreen((String) row.get("pxscreen"));
		return em;
	}

	private StringBuilder selectClause() {
		return new StringBuilder("SELECT * ");
	}

	private StringBuilder fromClause(String tableName) {
		return new StringBuilder(" FROM " + tableName + " ");
	}

	private StringBuilder whereClause(String searchText) {
		if (searchText == null || searchText.isEmpty())
			return new StringBuilder(" WHERE zid="+sessionManager.getBusinessId()+" ");
		return new StringBuilder(" WHERE zid="+sessionManager.getBusinessId()+" AND (xscreen LIKE '%" + searchText + "%' OR xtitle LIKE '%" + searchText
				+ "%' OR xnum LIKE '%" + searchText + "%' OR xtype LIKE '%" + searchText + "%' OR xicon LIKE '%"
				+ searchText + "%' OR xsequence LIKE '%" + searchText + "%') ");
	}

}
