package com.zubayer.entity;

import com.zubayer.entity.pk.UOMPK;
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
 * CSE202401068
 */
@Data
@Entity
@Table(name = "uom")
@IdClass(UOMPK.class)
@EqualsAndHashCode(callSuper = true)
public class UOM extends AbstractModel<Integer> {

	private static final long serialVersionUID = 8152952486064776605L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xcode")
	private Integer xcode;

	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static UOM getDefaultInstance() {
		UOM obj = new UOM();
		obj.setSubmitFor(SubmitFor.INSERT);
		return obj;
	}
}
