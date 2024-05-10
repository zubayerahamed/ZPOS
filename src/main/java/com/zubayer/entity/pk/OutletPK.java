package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutletPK implements Serializable {

	private static final long serialVersionUID = 5803930898177526953L;

	private Integer zid;
	private Integer id;
}
