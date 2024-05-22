package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since May 22, 2024 CSE202401068
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class XusersPK implements Serializable {

	private static final long serialVersionUID = 8614496468972203368L;
	private Integer zid;
	private Integer xusername;
}
