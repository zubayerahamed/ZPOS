package com.zubayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Category;
import com.zubayer.entity.pk.CategoryPK;

/**
 * @author Zubayer Ahamed
 * @since Apr 26, 2024
 * CSE202101068
 */
@Repository
public interface CategoryRepo extends JpaRepository<Category, CategoryPK> {

}
