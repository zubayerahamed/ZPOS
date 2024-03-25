package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024
 * CSE202101068
 */
@Data
public class ProfiledtPK implements Serializable {

	private static final long serialVersionUID = 176500270942138654L;

	private Integer zid;
	private String xprofile;
	private String xscreen;
}
