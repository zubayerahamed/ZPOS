package com.zubayer.entity;

import com.zubayer.enums.BusinessType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "zbusiness")
@EqualsAndHashCode(callSuper = true)
public class Zbusiness extends AbstractModel<Integer> {

	private static final long serialVersionUID = 1466366937749857116L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer zid;

	@Column(name = "zorg", length = 50, nullable = false)
	private String zorg;

	@Column(name = "xemail", length = 50)
	private String xemail;

	@Column(name = "xmadd", length = 200)
	private String xmadd;

	@Column(name = "country", length = 100)
	private String country;

	@Column(name = "currency", length = 3)
	private String currency;

	@Lob
	@Column(name = "xlogo")
	private byte[] xlogo;

	@Column(name = "zactive")
	private Boolean zactive;

	@Enumerated(EnumType.STRING)
	@Column(name = "businessType", nullable = false)
	private BusinessType businessType;

	@Transient
	private String imageBase64;

	@Transient
	private String zemail;

	@Transient
	private String xpassword;

	@Transient
	private String zname;
}
