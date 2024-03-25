package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024 
 * CSE202101068
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XscreensPK implements Serializable {

	private static final long serialVersionUID = -6867251113042279103L;

	private Integer zid;
	private String xscreen;
}
