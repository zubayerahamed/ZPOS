package com.zubayer.entity;

import java.util.List;

import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedStoredProcedureQueries;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024
 * CSE202401068
 */
@Data
@Entity
@Table(name = "xscreens")
@IdClass(XscreensPK.class)
@EqualsAndHashCode(callSuper = true)
@NamedStoredProcedureQueries({
	@NamedStoredProcedureQuery(name = "Fn_getTrn", procedureName = "Fn_getTrn", parameters = {
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "zid", type = Integer.class),
			@StoredProcedureParameter(mode = ParameterMode.IN, name = "screen", type = String.class),
			@StoredProcedureParameter(mode = ParameterMode.OUT, name = "trn_code", type = String.class) 
	}) 
})
public class Xscreens extends AbstractModel<Integer> {

	private static final long serialVersionUID = -6252002532263150998L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@NotBlank
	@Column(name = "xscreen", length = 10)
	private String xscreen;

	@NotBlank
	@Column(name = "xtitle", length = 50)
	private String xtitle;

	@Column(name = "xnum")
	private Integer xnum;

	@Column(name = "xinc")
	private Integer xinc;

	@NotBlank
	@Column(name = "xtype", length = 10)
	private String xtype;

	@NotNull
	@Column(name = "xsequence")
	private Integer xsequence;

	@Column(name = "xicon", length = 50)
	private String xicon;

	@Column(name = "pxscreen", length = 10)
	private String pxscreen;

	@Transient
	private List<Xscreens> subMenus;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Xscreens getDefaultInstance() {
		Xscreens obj = new Xscreens();
		obj.setSubmitFor(SubmitFor.INSERT);
		obj.setXnum(100);
		obj.setXsequence(0);
		obj.setXinc(1);
		return obj;
	}
}
