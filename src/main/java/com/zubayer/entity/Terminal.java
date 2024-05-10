package com.zubayer.entity;

import com.zubayer.entity.pk.TerminalPK;
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
 * @since Apr 8, 2024
 * CSE202401068
 */
@Data
@Entity
@Table(name = "terminal")
@IdClass(TerminalPK.class)
@EqualsAndHashCode(callSuper = true)
public class Terminal extends AbstractModel<Integer> {

	private static final long serialVersionUID = 1119941964108301782L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "outletId")
	private Integer outletId;

	@Id
	@Basic(optional = false)
	@Column(name = "shopId")
	private Integer shopId;

	@Id
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Transient
	private String outletName;

	@Transient
	private String shopName;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Terminal getDefaultInstance() {
		Terminal obj = new Terminal();
		obj.setSubmitFor(SubmitFor.INSERT);
		return obj;
	}
}
