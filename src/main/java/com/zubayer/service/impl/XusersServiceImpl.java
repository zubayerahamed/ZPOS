package com.zubayer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.zubayer.entity.Xusers;
import com.zubayer.enums.DatatableSortOrderType;
import com.zubayer.enums.POSRole;
import com.zubayer.service.XusersService;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Service
public class XusersServiceImpl extends AbstractService implements XusersService {

	@Override
	public List<Xusers> LAD17(int limit, int offset, String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam) {
		searchText = sanitizeSearch(searchText);

		StringBuilder sql = new StringBuilder();
		sql.append(selectClause())
		.append(fromClause("xusers im"))
		.append(whereClause(searchText, suffix, dependentParam))
		.append(orderbyClause(orderBy, orderType.name()))
		.append(limitAndOffsetClause(limit, offset));

		List<Map<String, Object>> result = jdbcTemplate.queryForList(sql.toString());
		List<Xusers> list = new ArrayList<>();
		result.stream().forEach(row -> list.add(constractObj(row)));

		return list;
	}

	@Override
	public int LAD17(String orderBy, DatatableSortOrderType orderType, String searchText, int suffix, String dependentParam) {
		searchText = sanitizeSearch(searchText);

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) ")
		.append(fromClause("xusers im"))
		.append(whereClause(searchText, suffix, dependentParam));
		return jdbcTemplate.queryForObject(sql.toString(), Integer.class);
	}

	private Xusers constractObj(Map<String, Object> row) {
		Xusers obj = new Xusers();
		obj.setZid((Integer) row.get("zid"));
		obj.setXusername((Integer) row.get("xusername"));
		obj.setXdisplayname((String) row.get("xdisplayname"));
		obj.setXpassword((String) row.get("xpassword"));
		obj.setXrole(POSRole.valueOf((String) row.get("xrole")));
		obj.setXoutlet((Integer) row.get("xoutlet"));
		obj.setXshop((Integer) row.get("xshop"));
		obj.setZactive((Boolean) row.get("zactive"));
		obj.setOutletName((String) row.get("outletname"));
		obj.setShopName((String) row.get("shopname"));
		return obj;
	}

	private StringBuilder selectClause() {
		return new StringBuilder("SELECT im.*, outlet.xname as outletname, shop.xname as shopname ");
	}

	private StringBuilder fromClause(String tableName) {
		return new StringBuilder(" FROM " + tableName + " ")
				.append(" LEFT JOIN outlet ON outlet.id = im.xoutlet AND outlet.zid = im.zid ")
				.append(" LEFT JOIN shop ON shop.id = im.xshop AND shop.zid = im.zid ");
	}

	private StringBuilder whereClause(String searchText, int suffix, String dependentParam) {
		StringBuilder sql = new StringBuilder(" WHERE im.zid="+ sessionManager.getBusinessId()+ " ");

		if (searchText == null || searchText.isEmpty()) return sql;

		return sql.append(" AND (im.xusername LIKE '%" + searchText+ "%'" +  
				" OR im.xdisplayname LIKE '%" + searchText + "%') ");
	}


}
