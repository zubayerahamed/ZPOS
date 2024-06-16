package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Currency;
import com.zubayer.entity.pk.CurrencyPK;

/**
 * @author Zubayer Ahamed
 * @since Jun 15, 2024
 */
@Repository
public interface CurrencyRepo extends JpaRepository<Currency, CurrencyPK> {

	List<Currency> findAllByZid(Integer zid);
}
