package com.zubayer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Category;
import com.zubayer.entity.pk.CategoryPK;

import jakarta.transaction.Transactional;

/**
 * @author Zubayer Ahamed
 * @since Apr 26, 2024
 * CSE202401068
 */
@Transactional
@Repository
public interface CategoryRepo extends JpaRepository<Category, CategoryPK> {

	List<Category> findAllByZid(Integer zid);

	Optional<Category> findByZidAndXname(Integer zid, String xname);

	@Query(value = "SELECT * FROM category WHERE zid = ?1 AND xcode <> ?2 AND (xpcode IS NULL OR xpcode <> ?2)", nativeQuery = true)
	List<Category> findAllElegibleParentCategories(Integer zid, Integer xcode);

	@Query(value = "SELECT * FROM category WHERE zid = ?1 AND xpcode = ?2", nativeQuery = true)
	List<Category> findAllChildCategories(Integer zid, Integer xcode);

	@Modifying(flushAutomatically = true)
	@Query(value = "UPDATE category SET xpcode=null WHERE zid=?1 AND xpcode=?2", nativeQuery = true)
	Integer removeAllChilds(Integer zid, Integer xcode);
}
