package com.zubayer.entity;

import com.zubayer.entity.pk.OutletPK;
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
@Table(name = "outlet")
@IdClass(OutletPK.class)
@EqualsAndHashCode(callSuper = true)
public class Outlet extends AbstractModel<Integer> {

	private static final long serialVersionUID = 8152952486064776605L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Outlet getDefaultInstance() {
		Outlet obj = new Outlet();
		obj.setSubmitFor(SubmitFor.INSERT);
		return obj;
	}
}
