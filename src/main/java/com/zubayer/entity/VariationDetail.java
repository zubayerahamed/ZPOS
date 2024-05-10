package com.zubayer.entity;

import java.math.BigDecimal;

import com.zubayer.entity.pk.VariationDetailPK;
import com.zubayer.enums.SubmitFor;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Apr 26, 2024
 * CSE202401068
 */
@Data
@Entity
@Table(name = "variation_detail")
@IdClass(VariationDetailPK.class)
@EqualsAndHashCode(callSuper = true)
public class VariationDetail extends AbstractModel<Integer> {

	private static final long serialVersionUID = -913461240215383071L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xrow")
	private Integer xrow;

	@Id
	@Basic(optional = false)
	@Column(name = "xcode")
	private Integer xcode;

	@NotBlank(message = "Option Name Required")
	@Column(name = "xname", length = 100)
	private String xname;

	@NotNull(message = "Price must be greater than or equal to 0")
	@Min(value = 0, message = "Price should be minimum 0")
	@Column(name = "xprice")
	private BigDecimal xprice;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static VariationDetail getDefaultInstance() {
		VariationDetail obj = new VariationDetail();
		obj.setSubmitFor(SubmitFor.INSERT);
		return obj;
	}
}
