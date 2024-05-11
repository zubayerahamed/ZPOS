package com.zubayer.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Charge;
import com.zubayer.entity.pk.ChargePK;
import com.zubayer.enums.ChargeType;

/**
 * @author Zubayer Ahamed
 * @since May 11, 2024
 * CSE202401068
 */
@Repository
public interface ChargeRepo extends JpaRepository<Charge, ChargePK> {

	List<Charge> findAllByXtypeAndZid(ChargeType xtype, Integer zid);

	Optional<Charge> findByZidAndXtypeAndXrate(Integer zid, ChargeType xtype, BigDecimal xrate);

}
