package com.zubayer.entity;

import com.zubayer.entity.pk.XusersPK;
import com.zubayer.enums.POSRole;
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
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since May 22, 2024
 * CSE202401068
 */
@Data
@Entity
@Table(name = "xusers")
@IdClass(XusersPK.class)
@EqualsAndHashCode(callSuper = true)
public class Xusers extends AbstractModel<Integer> {

	private static final long serialVersionUID = -3503669090314188480L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid; 

	@Id
	@Basic(optional = false)
	@Column(name = "xusername")
	private Integer xusername;

	@NotBlank(message = "Display name required")
	@Column(name = "xdisplayname", length = 50)
	private String xdisplayname;

	@NotBlank(message = "Password required")
	@Column(name = "xpassword", length = 250)
	private String xpassword;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Enumerated(EnumType.STRING)
	@Column(name = "xrole", length = 20)
	private POSRole xrole;

	@Column(name = "xoutlet")
	private Integer xoutlet;

	@Column(name = "xshop")
	private Integer xshop;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Xusers getDefaultInstance() {
		Xusers obj = new Xusers();
		obj.setSubmitFor(SubmitFor.INSERT);
		obj.setZactive(true);
		return obj;
	}
}
