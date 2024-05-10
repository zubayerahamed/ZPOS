package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariationDetailPK implements Serializable {

	private static final long serialVersionUID = 540604996736283744L;

	private Integer zid;
	private Integer xrow;
	private String xcode;
}
