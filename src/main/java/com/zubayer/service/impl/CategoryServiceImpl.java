package com.zubayer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zubayer.entity.Category;
import com.zubayer.enums.DatatableSortOrderType;
import com.zubayer.service.CategoryService;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Service
public class CategoryServiceImpl extends AbstractService implements CategoryService {

	@Override
	public List<Category> LMD13(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam) {
		searchText = sanitizeSearch(searchText);

		StringBuilder sql = new StringBuilder();
		sql.append(selectClause())
		.append(fromClause("category im"))
		.append(whereClause(searchText, suffix, dependentParam))
		.append(orderbyClause(orderBy, orderType.name()))
		.append(limitAndOffsetClause(limit, offset));

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
		List<Category> list = new ArrayList<>();
		result.stream().forEach(row -> list.add(constractObj(row)));

		return list;
	}

	@Override
	public int LMD13(String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam) {
		searchText = sanitizeSearch(searchText);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) ")
		.append(fromClause("category im"))
		.append(whereClause(searchText, suffix, dependentParam));
		return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
	}

	private Category constractObj(Map<String, Object> row) {
		Category obj = new Category();
		obj.setZid((Integer) row.get("zid"));
		obj.setXcode((Integer) row.get("xcode"));
		obj.setXname((String) row.get("xname"));
		obj.setXseqn((Integer) row.get("xseqn"));
		obj.setXicon((String) row.get("xicon"));
		obj.setXthumbnail((String) row.get("xthumbnail"));
		obj.setXpcode((Integer) row.get("xpcode"));
		obj.setZactive((Boolean) row.get("zactive"));
		obj.setParentCategory((String) row.get("parentname"));
		return obj;
	}

	private StringBuilder selectClause() {
		return new StringBuilder("SELECT im.*, c.xcode as parentcode, c.xname as parentname ");
	}

	private StringBuilder fromClause(String tableName) {
		return new StringBuilder(" FROM " + tableName + " ")
				.append(" LEFT JOIN category as c ON c.xcode = im.xpcode AND c.zid = im.zid ");
	}

	private StringBuilder whereClause(String searchText, int suffix, String dependentParam) {
		StringBuilder sql = new StringBuilder(" WHERE im.zid="+ sessionManager.getBusinessId()+ " ");

		if (searchText == null || searchText.isEmpty()) return sql;

		return sql.append(" AND (im.xcode LIKE '%" + searchText+ "%'" + 
				" OR c.xcode LIKE '%" + searchText + "%'" +
				" OR c.xname LIKE '%" + searchText + "%'" + 
				" OR im.xname LIKE '%" + searchText + "%') ");
	}


}
