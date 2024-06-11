package com.zubayer.entity;

import com.zubayer.entity.pk.XfloorPK;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Jun 11, 2024
 */
@Data
@Entity
@Table(name = "xfloor")
@IdClass(XfloorPK.class)
@EqualsAndHashCode(callSuper = true)
public class Xfloor extends AbstractModel<Integer> {

	private static final long serialVersionUID = 1174708863479728249L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@Id
	@Basic(optional = false)
	@Column(name = "xoutlet")
	private Integer xoutlet;

	@Id
	@Basic(optional = false)
	@Column(name = "xshop")
	private Integer xshop;

	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;
}
