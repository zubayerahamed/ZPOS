package com.zubayer.entity;

import java.math.BigDecimal;

import com.zubayer.entity.pk.ItemSetsPK;
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
@Table(name = "item_sets")
@IdClass(ItemSetsPK.class)
@EqualsAndHashCode(callSuper = true)
public class ItemSets extends AbstractModel<Integer> {

	private static final long serialVersionUID = -1284100415450552808L;

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
	@Column(name = "xsetitem")
	private Integer xsetitem;

	@Column(name = "xqty")
	private BigDecimal xqty;

	@Transient
	private String setItemName;

	@Transient
	private String setItemUnit;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static ItemSets getDefaultInstance(Integer xitem) {
		ItemSets obj = new ItemSets();
		obj.setSubmitFor(SubmitFor.INSERT);
		obj.setXitem(xitem);
		obj.setXqty(BigDecimal.ZERO);
		return obj;
	}
}
