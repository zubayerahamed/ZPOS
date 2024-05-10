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
@NoArgsConstructor
@AllArgsConstructor
public class TerminalPK implements Serializable{

	private static final long serialVersionUID = 8451816952834094176L;

	private Integer zid;
	private Integer outletId;
	private Integer shopId;
	private Integer id;
}
