package com.zubayer.entity;

import com.zubayer.entity.pk.CategoryPK;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Apr 26, 2024 CSE202101068
 */
@Data
@Entity
@Table(name = "category")
@IdClass(CategoryPK.class)
@EqualsAndHashCode(callSuper = true)
public class Category extends AbstractModel<Integer> {

	private static final long serialVersionUID = -2212434269045686076L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "seqn")
	private Integer seqn;

	@Column(name = "icon", length = 50)
	private String icon;

	@Column(name = "thumbnail", length = 250)
	private String thumbnail;

	@Column(name = "parentId")
	private Integer parentId;
}
