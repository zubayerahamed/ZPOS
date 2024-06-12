package com.zubayer.entity;

import com.zubayer.entity.pk.XtablePK;
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
 * @since Jun 11, 2024
 */
@Data
@Entity
@Table(name = "xtable")
@IdClass(XtablePK.class)
@EqualsAndHashCode(callSuper = true)
public class Xtable extends AbstractModel<Integer> {

	private static final long serialVersionUID = 1174708863479728249L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xoutlet")
	private Integer xoutlet;

	@Id
	@Basic(optional = false)
	@Column(name = "xshop")
	private Integer xshop;

	@Id
	@Basic(optional = false)
	@Column(name = "xfloor")
	private Integer xfloor;

	@Id
	@Basic(optional = false)
	@Column(name = "xterminal")
	private Integer xterminal;

	@Id
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@Column(name = "xname", length = 100)
	private String xname;

	@Column(name = "xcapacity")
	private Integer xcapacity;

	@Column(name = "xwaiter")
	private Integer xwaiter;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Transient
	private String outletName;

	@Transient
	private String shopName;

	@Transient
	private String floorName;

	@Transient
	private String terminalName;

	@Transient
	private String waiterName;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Xtable getDefaultInstance() {
		Xtable obj = new Xtable();
		obj.setSubmitFor(SubmitFor.INSERT);
		obj.setXcapacity(1);
		return obj;
	}
}
