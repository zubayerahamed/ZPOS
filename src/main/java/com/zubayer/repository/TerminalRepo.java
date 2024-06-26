package com.zubayer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Terminal;
import com.zubayer.entity.pk.TerminalPK;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202101068
 */
@Repository
public interface TerminalRepo extends JpaRepository<Terminal, TerminalPK> {

	List<Terminal> findAllByZid(Integer zid);

	Optional<Terminal> findByZidAndXdevice(Integer zid, String xdevice);

	List<Terminal> findAllByZidAndOutletIdAndShopId(Integer zid, Integer outletId, Integer shopId);
}
