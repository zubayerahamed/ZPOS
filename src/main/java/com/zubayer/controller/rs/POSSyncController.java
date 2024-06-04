package com.zubayer.controller.rs;

import java.text.SimpleDateFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zubayer.annotation.RestApiController;
import com.zubayer.entity.Outlet;
import com.zubayer.entity.Shop;
import com.zubayer.entity.Terminal;
import com.zubayer.entity.Zbusiness;
import com.zubayer.entity.pk.OutletPK;
import com.zubayer.entity.pk.ShopPK;
import com.zubayer.entity.pk.TerminalPK;
import com.zubayer.repository.OutletRepo;
import com.zubayer.repository.ShopRepo;
import com.zubayer.repository.TerminalRepo;
import com.zubayer.repository.ZbusinessRepo;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Zubayer Ahamed
 * @since Jun 4, 2024
 */
@Slf4j
@RestApiController
@RequestMapping("/api/v1/possync")
public class POSSyncController {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss"); 

	@Autowired private ZbusinessRepo zbusinessRepo;
	@Autowired private OutletRepo outletRepo;
	@Autowired private ShopRepo shopRepo;
	@Autowired private TerminalRepo terminalRepo;

	@GetMapping("/business")
	public Zbusiness syncBusiness(@RequestParam Integer businessid) {
		Optional<Zbusiness> op = zbusinessRepo.findById(businessid);
		return op.isPresent() ? op.get() : null;
	}

	@GetMapping("/outlet")
	public Outlet syncOutlet(@RequestParam Integer businessid, @RequestParam Integer outletid) {
		Optional<Outlet> op = outletRepo.findById(new OutletPK(businessid, outletid));
		return op.isPresent() ? op.get() : null;
	}

	@GetMapping("/shop")
	public Shop syncShop(@RequestParam Integer businessid, @RequestParam Integer outletid, @RequestParam Integer shopid) {
		Optional<Shop> op = shopRepo.findById(new ShopPK(businessid, outletid, shopid));
		return op.isPresent() ? op.get() : null;
	}

	@GetMapping("/terminal")
	public Terminal syncTerminal(@RequestParam Integer businessid, @RequestParam Integer outletid, @RequestParam Integer shopid, @RequestParam Integer terminalid) {
		Optional<Terminal> op = terminalRepo.findById(new TerminalPK(businessid, outletid, shopid, terminalid));
		return op.isPresent() ? op.get() : null;
	}
}
