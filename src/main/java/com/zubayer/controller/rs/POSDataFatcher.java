package com.zubayer.controller.rs;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zubayer.annotation.RestApiController;
import com.zubayer.entity.Outlet;
import com.zubayer.entity.Shop;
import com.zubayer.entity.Terminal;
import com.zubayer.entity.Xusers;
import com.zubayer.entity.Zbusiness;
import com.zubayer.entity.pk.OutletPK;
import com.zubayer.entity.pk.ShopPK;
import com.zubayer.entity.pk.TerminalPK;
import com.zubayer.repository.OutletRepo;
import com.zubayer.repository.ShopRepo;
import com.zubayer.repository.TerminalRepo;
import com.zubayer.repository.XusersRepo;
import com.zubayer.repository.ZbusinessRepo;
import com.zubayer.util.POSUtil;

/**
 * @author Zubayer Ahamed
 * @since May 29, 2024
 */
@RestApiController
@RequestMapping("/api/v1/posdata")
public class POSDataFatcher {

	@Autowired private ZbusinessRepo zbusnessRepo;
	@Autowired private OutletRepo outletRepo;
	@Autowired private ShopRepo shopRepo;
	@Autowired private TerminalRepo terminalRepo;
	@Autowired private XusersRepo xusersRepo;
	@Autowired private POSUtil posUtil;

	@GetMapping("/business")
	public Zbusiness getZbusinessData(@RequestParam String key) {
		if(StringUtils.isBlank(key)) return null;

		Integer zid = posUtil.extractKey(key, "business");
		Optional<Zbusiness> businessOp = zbusnessRepo.findById(zid);
		return businessOp.isPresent() ? businessOp.get() : null;
	}

	@GetMapping("/outlet")
	public Outlet getOutletData(@RequestParam String key) {
		if(StringUtils.isBlank(key)) return null;

		Integer zid = posUtil.extractKey(key, "business");
		Integer outletid = posUtil.extractKey(key, "outlet");
		Optional<Outlet> outletOp = outletRepo.findById(new OutletPK(zid, outletid));
		return outletOp.isPresent() ? outletOp.get() : null;
	}

	@GetMapping("/shop")
	public Shop getShopData(@RequestParam String key) {
		if(StringUtils.isBlank(key)) return null;

		Integer zid = posUtil.extractKey(key, "business");
		Integer outletid = posUtil.extractKey(key, "outlet");
		Integer shopid = posUtil.extractKey(key, "shop");
		Optional<Shop> shopOp = shopRepo.findById(new ShopPK(zid, outletid, shopid));
		return shopOp.isPresent() ? shopOp.get() : null;
	}

	@GetMapping("/terminal")
	public Terminal getTerminalData(@RequestParam String key) {
		if(StringUtils.isBlank(key)) return null;

		Integer zid = posUtil.extractKey(key, "business");
		Integer outletid = posUtil.extractKey(key, "outlet");
		Integer shopid = posUtil.extractKey(key, "shop");
		Integer terminalid = posUtil.extractKey(key, "terminal");
		Optional<Terminal> terminalOp = terminalRepo.findById(new TerminalPK(zid, outletid, shopid, terminalid));
		return terminalOp.isPresent() ? terminalOp.get() : null;
	}

	@GetMapping("/posusers")
	public List<Xusers> getPOSUsersData(@RequestParam String key){
		if(StringUtils.isBlank(key)) return null;
		Integer zid = posUtil.extractKey(key, "business");
		return xusersRepo.findAllByZid(zid);
	}
}
