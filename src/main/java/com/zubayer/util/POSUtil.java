/**
 * 
 */
package com.zubayer.util;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zubayer.entity.Terminal;
import com.zubayer.entity.pk.TerminalPK;
import com.zubayer.repository.TerminalRepo;

/**
 * 
 */
@Component
public class POSUtil {

	@Autowired private TerminalRepo terminalRepo;

	public static String generatePOSKey(Integer zid, Integer outletId, Integer shopId, Integer terminalId) {
		if(zid == null) return null;
		if(outletId == null) return null;
		if(shopId == null) return null;
		if(terminalId == null) return null;

		StringBuilder key = new StringBuilder();
		key.append(StringUtils.leftPad(zid.toString(), 6, '0'));
		key.append("-");
		key.append(StringUtils.leftPad(outletId.toString(), 6, '0'));
		key.append("-");
		key.append(StringUtils.leftPad(shopId.toString(), 6, '0'));
		key.append("-");
		key.append(StringUtils.leftPad(terminalId.toString(), 6, '0'));

		return key.toString();
	}

	public boolean validateKey(String key) {
		if(StringUtils.isBlank(key)) return false;
		if(key.length() != 27) return false;
		String data[] = key.split("-");

		Integer zid = Integer.valueOf(removeLeadingZero(data[0]));
		Integer outlet = Integer.valueOf(removeLeadingZero(data[1]));
		Integer shop = Integer.valueOf(removeLeadingZero(data[2]));
		Integer terminal = Integer.valueOf(removeLeadingZero(data[3]));

		Optional<Terminal> terminalOp = terminalRepo.findById(new TerminalPK(zid, outlet, shop, terminal));
		return terminalOp.isPresent();
	}

	public Integer extractKey(String key, String type) {
		if(StringUtils.isBlank(key)) return null;
		if(key.length() != 27) return null;

		String data[] = key.split("-");

		Integer zid = Integer.valueOf(removeLeadingZero(data[0]));
		Integer outlet = Integer.valueOf(removeLeadingZero(data[1]));
		Integer shop = Integer.valueOf(removeLeadingZero(data[2]));
		Integer terminal = Integer.valueOf(removeLeadingZero(data[3]));

		if("business".equalsIgnoreCase(type)) return zid;
		if("outlet".equalsIgnoreCase(type)) return outlet;
		if("shop".equalsIgnoreCase(type)) return shop;
		if("terminal".equalsIgnoreCase(type)) return terminal;
		return null;
	}

	private String removeLeadingZero(String txt) {
		return txt.replaceFirst("^0+", "");
	}
}
