package com.zubayer.entity;

import com.zubayer.entity.pk.ShopPK;
import com.zubayer.enums.SubmitFor;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202101068
 */
@Data
@Entity
@Table(name = "shop")
@IdClass(ShopPK.class)
@EqualsAndHashCode(callSuper = true)
public class Shop extends AbstractModel<Integer> {

	private static final long serialVersionUID = 7818141720814743166L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "outletId")
	private Integer outletId;

	@Id
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Transient
	private String outletName;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Shop getDefaultInstance() {
		Shop obj = new Shop();
		obj.setSubmitFor(SubmitFor.INSERT);
		return obj;
	}
}
