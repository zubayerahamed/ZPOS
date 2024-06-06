package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Xusers;
import com.zubayer.entity.pk.XusersPK;

/**
 * @author Zubayer Ahamed
 * @since May 22, 2024
 * CSE202401068
 */
@Repository
public interface XusersRepo extends JpaRepository<Xusers, XusersPK> {

	List<Xusers> findAllByZid(Integer zid);

	List<Xusers> findAllByZidAndXoutletAndXshop(Integer zid, Integer xoutlet, Integer xshop); 
}
