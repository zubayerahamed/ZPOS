package com.zubayer.entity;

import com.zubayer.entity.pk.VariationPK;
import com.zubayer.enums.SubmitFor;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Apr 26, 2024
 * CSE202401068
 */
@Data
@Entity
@Table(name = "variation")
@IdClass(VariationPK.class)
@EqualsAndHashCode(callSuper = true)
public class Variation extends AbstractModel<Integer> {

	private static final long serialVersionUID = -913461240215383071L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xcode")
	private Integer xcode;

	@NotBlank(message = "Variation Name Required")
	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Variation getDefaultInstance() {
		Variation obj = new Variation();
		obj.setSubmitFor(SubmitFor.INSERT);
		obj.setZactive(true);
		return obj;
	}
}
