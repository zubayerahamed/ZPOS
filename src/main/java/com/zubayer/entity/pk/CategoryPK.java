package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since Apr 26, 2024
 * CSE202101068
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPK implements Serializable {
	private static final long serialVersionUID = 3054549400025858258L;

	private Integer zid;
	private Integer xcode;
}
