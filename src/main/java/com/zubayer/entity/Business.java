package com.zubayer.entity;

import com.zubayer.enums.BusinessType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Data
@Entity
@Table(name = "business")
@EqualsAndHashCode(callSuper = true)
public class Business extends AbstractModel<Long> {

	private static final long serialVersionUID = 1466366937749857116L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", length = 10, unique = true)
	private String code;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "active", length = 1)
	private Boolean active = Boolean.TRUE;

	@Enumerated(EnumType.STRING)
	@Column(name = "businessType", nullable = false)
	private BusinessType businessType;

	@Transient
	private String zemail;

	@Transient
	private String zpasswd;
}
