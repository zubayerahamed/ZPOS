package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202101068
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopPK implements Serializable {

	private static final long serialVersionUID = 1772449066845298747L;

	private Integer zid;
	private Integer outletId;
	private Integer id;
}
