package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.XscreensPK;

import jakarta.transaction.Transactional;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024
 * CSE202101068
 */
@Repository
public interface XscreensRepo extends JpaRepository<Xscreens, XscreensPK> {

	public List<Xscreens> findAllByXtypeAndZid(String xtype, Integer zid);

	@Transactional
	@Procedure(name = "Fn_getTrn")
	public Integer Fn_getTrn(@Param("zid") Integer zid, @Param("screen") String screen);
}