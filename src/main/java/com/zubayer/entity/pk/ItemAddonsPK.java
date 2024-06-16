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
public class ItemAddonsPK implements Serializable {

	private static final long serialVersionUID = 3709363416176633780L;

	private Integer zid;
	private Integer xitem;
	private Integer xaddons;
}
