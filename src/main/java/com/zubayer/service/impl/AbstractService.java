package com.zubayer.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zubayer.service.ZSessionManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * @author Zubayer Ahamed
 * @since Jul 3, 2023
 */
public abstract class AbstractService {

	@PersistenceContext protected EntityManager em;
	@Autowired protected JdbcTemplate jdbcTemplate;
	@Autowired protected ZSessionManager sessionManager;

	protected String sanitizeSearch(String searchText) {
		if(StringUtils.isBlank(searchText)) return searchText;
		searchText = searchText.replaceAll("'", "''");
		return searchText;
	}

	protected StringBuilder orderbyClause(String orderByField, String orderType) {
		return new StringBuilder(" ORDER BY " + orderByField + " " + orderType + " ");
	}

	protected StringBuilder limitAndOffsetClause(int limit, int offset) {
		return new StringBuilder(" OFFSET "+offset+" ROWS FETCH NEXT "+limit+" ROWS ONLY ");
	}

}
