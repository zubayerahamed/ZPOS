package com.zubayer.entity;

import com.zubayer.entity.pk.ProfiledtPK;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024
 * CSE202101068
 */
@Data
@Entity
@Table(name = "profiledt")
@IdClass(ProfiledtPK.class)
@EqualsAndHashCode(callSuper = true)
public class Profiledt extends AbstractModel<Integer> {

	private static final long serialVersionUID = 5963946292035383502L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xprofile", length = 20)
	private String xprofile;

	@Id
	@Basic(optional = false)
	@Column(name = "xscreen", length = 10)
	private String xscreen;
}
