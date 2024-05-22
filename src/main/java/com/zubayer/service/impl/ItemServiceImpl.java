package com.zubayer.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zubayer.entity.Item;
import com.zubayer.enums.DatatableSortOrderType;
import com.zubayer.service.ItemService;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Service
public class ItemServiceImpl extends AbstractService implements ItemService {

	@Override
	public List<Item> LMD16(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam) {
		searchText = sanitizeSearch(searchText);

		StringBuilder sql = new StringBuilder();
		sql.append(selectClause())
		.append(fromClause("item im"))
		.append(whereClause(searchText, suffix, dependentParam))
		.append(orderbyClause(orderBy, orderType.name()))
		.append(limitAndOffsetClause(limit, offset));

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
		List<Item> list = new ArrayList<>();
		result.stream().forEach(row -> list.add(constractObj(row)));

		return list;
	}

	@Override
	public int LMD16(String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam) {
		searchText = sanitizeSearch(searchText);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) ")
		.append(fromClause("item im"))
		.append(whereClause(searchText, suffix, dependentParam));
		return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
	}

	private Item constractObj(Map<String, Object> row) {
		Item obj = new Item();
		obj.setZid((Integer) row.get("zid"));
		obj.setXcode((Integer) row.get("xcode"));
		obj.setXname((String) row.get("xname"));
		obj.setXcat((Integer) row.get("xcat"));
		obj.setXseqn((Integer) row.get("xseqn"));
		obj.setXsku((String) row.get("xsku"));
		obj.setXbarcode((String) row.get("xbarcode"));
		obj.setXsubtitle((String) row.get("xsubtitle"));
		obj.setXdesc((String) row.get("xdesc"));
		obj.setXprice((BigDecimal) row.get("xprice"));
		obj.setXsetmenu((Boolean) row.get("xsetmenu"));
		obj.setXsetdesc((String) row.get("xsetdesc"));
		obj.setXuom((Integer) row.get("xuom"));
		obj.setXvat((Integer) row.get("xvat"));
		obj.setXsd((Integer) row.get("xsd"));
		obj.setXsc((Integer) row.get("xsc"));

		obj.setUnit((String) row.get("unit"));
		obj.setCategoryname((String) row.get("categoryname"));
		obj.setVat((BigDecimal) row.get("vat"));
		obj.setSd((BigDecimal) row.get("sd"));

		obj.setZactive((Boolean) row.get("zactive"));
		return obj;
	}

	private StringBuilder selectClause() {
		return new StringBuilder("SELECT im.*, u.xname as unit, v.xrate as vat, s.xrate as sd, c.xname as categoryname ");
	}

	private StringBuilder fromClause(String tableName) {
		return new StringBuilder(" FROM " + tableName + " ")
				.append(" LEFT JOIN category as c ON c.xcode = im.xcat AND c.zid = im.zid ")
				.append(" LEFT JOIN uom as u ON u.xcode = im.xuom AND u.zid = im.zid ")
				.append(" LEFT JOIN charge as v ON v.xcode = im.xvat AND v.zid = im.zid ")
				.append(" LEFT JOIN charge as s ON s.xcode = im.xsd AND s.zid = im.zid ");
	}

	private StringBuilder whereClause(String searchText, int suffix, String dependentParam) {
		StringBuilder sql = new StringBuilder(" WHERE im.zid="+ sessionManager.getBusinessId()+ " ");

		if (searchText == null || searchText.isEmpty()) return sql;

		return sql.append(" AND (im.xcode LIKE '%" + searchText+ "%'" +  
				" OR im.xname LIKE '%" + searchText + "%') ");
	}


}
