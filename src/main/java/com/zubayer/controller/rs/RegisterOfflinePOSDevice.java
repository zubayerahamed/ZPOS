package com.zubayer.controller.rs;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zubayer.annotation.RestApiController;
import com.zubayer.entity.Terminal;
import com.zubayer.entity.pk.TerminalPK;
import com.zubayer.repository.TerminalRepo;
import com.zubayer.util.POSUtil;

/**
 * @author Zubayer Ahamed
 * @since May 29, 2024
 */
@RestApiController
@RequestMapping("/api/v1/registerpos")
public class RegisterOfflinePOSDevice {

	@Autowired private TerminalRepo terminalRepo;
	@Autowired private POSUtil posUtil;

	@GetMapping("/device")
	public boolean registerPOSDevice(@RequestParam String key, @RequestParam String deviceid) {
		if(StringUtils.isBlank(key)) return false;
		if(StringUtils.isBlank(deviceid)) return false;

		Integer zid = posUtil.extractKey(key, "business");
		Integer outletid = posUtil.extractKey(key, "outlet");
		Integer shopid = posUtil.extractKey(key, "shop");
		Integer terminalid = posUtil.extractKey(key, "terminal");

		// search terminal with this device
		Optional<Terminal> existDeviceTerminalOp = terminalRepo.findByZidAndXdevice(zid, deviceid);
		if(existDeviceTerminalOp.isPresent() && existDeviceTerminalOp.get().getId() != terminalid) {
			return false;
		}

		Optional<Terminal> terminalOp = terminalRepo.findById(new TerminalPK(zid, outletid, shopid, terminalid));
		if(!terminalOp.isPresent()) return false;

		Terminal terminal = terminalOp.get();
		if(StringUtils.isNotBlank(terminal.getXdevice())) return false;
		terminal.setXdevice(deviceid);
		terminal = terminalRepo.save(terminal);

		return terminal == null ? false : true;
	}
}
