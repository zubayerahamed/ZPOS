package com.zubayer.entity.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Zubayer Ahamed
 * @since Jun 15, 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyPK implements Serializable {

	private static final long serialVersionUID = 5587896032765143663L;

	private Integer zid;
	private String xcode;
}
