package com.zubayer.entity;

import java.math.BigDecimal;

import com.zubayer.entity.pk.ChargePK;
import com.zubayer.enums.ChargeType;
import com.zubayer.enums.SubmitFor;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "charge")
@IdClass(ChargePK.class)
@EqualsAndHashCode(callSuper = true)
public class Charge extends AbstractModel<Integer> {

	private static final long serialVersionUID = 8152952486064776605L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xcode")
	private Integer xcode;

	@Enumerated(EnumType.STRING)
	@Column(name = "xtype")
	private ChargeType xtype;

	@Column(name = "xrate")
	private BigDecimal xrate;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Charge getDefaultInstance() {
		Charge obj = new Charge();
		obj.setSubmitFor(SubmitFor.INSERT);
		return obj;
	}
}
