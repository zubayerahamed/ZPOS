package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since Jun 11, 2024
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XfloorPK implements Serializable {

	private static final long serialVersionUID = 5649912067452124819L;

	private Integer zid;
	private Integer xoutlet;
	private Integer xshop;
	private Integer id;
}
