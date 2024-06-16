package com.zubayer.entity;

import java.util.ArrayList;
import java.util.List;

import com.zubayer.entity.pk.ItemVariationsPK;
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
 * @since Jun 16, 2024
 */
@Data
@Entity
@Table(name = "item_variations")
@IdClass(ItemVariationsPK.class)
@EqualsAndHashCode(callSuper = true)
public class ItemVariations extends AbstractModel<Integer>{

	private static final long serialVersionUID = -1261636036667922155L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xitem")
	private Integer xitem;

	@Id
	@Basic(optional = false)
	@Column(name = "xvariation")
	private Integer xvariation;

	@Column(name = "xrequired", length = 1)
	private Boolean xrequired = Boolean.TRUE;

	@Transient
	private String variationName;

	@Transient
	private List<VariationDetail> options = new ArrayList<>();

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static ItemVariations getDefaultInstance(Integer xitem) {
		ItemVariations obj = new ItemVariations();
		obj.setSubmitFor(SubmitFor.INSERT);
		obj.setXitem(xitem);
		return obj;
	}
}
