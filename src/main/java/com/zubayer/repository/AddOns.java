package com.zubayer.repository;

import java.math.BigDecimal;

import com.zubayer.entity.AbstractModel;
import com.zubayer.entity.pk.AddOnsPK;

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
 * @since Apr 26, 2024
 * CSE202101068
 */
@Data
@Entity
@Table(name = "addons")
@IdClass(AddOnsPK.class)
@EqualsAndHashCode(callSuper = true)
public class AddOns extends AbstractModel<Integer> {

	private static final long serialVersionUID = -913461240215383071L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "price")
	private BigDecimal price;
}
