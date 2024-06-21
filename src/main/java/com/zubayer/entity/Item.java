package com.zubayer.entity;

import java.math.BigDecimal;
import java.util.Base64;

import com.zubayer.entity.pk.ItemPK;
import com.zubayer.enums.SubmitFor;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since May 11, 2024 CSE202401068
 */
@Data
@Entity
@Table(name = "item")
@IdClass(ItemPK.class)
@EqualsAndHashCode(callSuper = true)
public class Item extends AbstractModel<Integer> {

	private static final long serialVersionUID = -807592763364044924L;

	@Id
	@Basic(optional = false)
	@Column(name = "zid")
	private Integer zid;

	@Id
	@Basic(optional = false)
	@Column(name = "xcode")
	private Integer xcode;

	@NotBlank(message = "Item Name Required")
	@Column(name = "xname", length = 100)
	private String xname;

	@NotNull(message = "Category Required")
	@Column(name = "xcat")
	private Integer xcat;

	@Column(name = "xseqn")
	private Integer xseqn = Integer.valueOf(0);

	@Column(name = "xsku", length = 100)
	private String xsku;

	@Column(name = "xbarcode", length = 200)
	private String xbarcode;

	@Column(name = "xsubtitle", length = 100)
	private String xsubtitle;

	@Column(name = "xdesc", length = 200)
	private String xdesc;

	@Lob
	@Column(name = "ximage")
	private byte[] ximage;

	@Column(name = "xprice")
	private BigDecimal xprice;

	@Column(name = "xsetmenu", length = 1)
	private Boolean xsetmenu = Boolean.FALSE;

	@Column(name = "xsetdesc", length = 200)
	private String xsetdesc;

	@Column(name = "xuom")
	private Integer xuom;

	@Column(name = "xvat")
	private Integer xvat;

	@Column(name = "xsd")
	private Integer xsd;

	@Column(name = "xsc")
	private Integer xsc;

	@Column(name = "zactive", length = 1)
	private Boolean zactive = Boolean.TRUE;

	@Transient
	private String imageBase64;

	public String getImageBase64() {
		if(this.getXimage() == null) return null;
		String base64String = Base64.getEncoder().encodeToString(this.getXimage());
		return base64String;
	}

	@Transient
	private String unit;

	@Transient
	private String categoryname;

	@Transient
	private BigDecimal vat;

	@Transient
	private BigDecimal sd;

	@Transient
	private SubmitFor submitFor = SubmitFor.UPDATE;

	public static Item getDefaultInstance() {
		Item obj = new Item();
		obj.setSubmitFor(SubmitFor.INSERT);
		obj.setXseqn(0);
		obj.setZactive(true);
		obj.setXprice(BigDecimal.ZERO);
		return obj;
	}
}
