package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Profiledt;
import com.zubayer.entity.pk.ProfiledtPK;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024
 * CSE202101068
 */
@Repository
public interface ProfiledtRepo extends JpaRepository<Profiledt, ProfiledtPK> {

	public List<Profiledt> findAllByXprofileAndZid(String xprofile, Integer zid);
}
