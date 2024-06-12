package com.zubayer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zubayer.entity.Xtable;
import com.zubayer.entity.pk.XtablePK;

/**
 * @author Zubayer Ahamed
 * @since Jun 11, 2024
 */
@Repository
public interface XtableRepo extends JpaRepository<Xtable, XtablePK> {

	List<Xtable> findAllByZid(Integer zid);
}
