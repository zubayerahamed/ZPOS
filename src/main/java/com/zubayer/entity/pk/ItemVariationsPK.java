package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since Jun 16, 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVariationsPK implements Serializable {

	private static final long serialVersionUID = 2269196715230354243L;

	private Integer zid;
	private Integer xitem;
	private Integer xvariation;
}
