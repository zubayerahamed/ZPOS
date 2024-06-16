package com.zubayer.controller.rs;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zubayer.annotation.RestApiController;
import com.zubayer.entity.AddOns;
import com.zubayer.entity.Category;
import com.zubayer.entity.Charge;
import com.zubayer.entity.Currency;
import com.zubayer.entity.Item;
import com.zubayer.entity.Outlet;
import com.zubayer.entity.Shop;
import com.zubayer.entity.Terminal;
import com.zubayer.entity.UOM;
import com.zubayer.entity.Variation;
import com.zubayer.entity.VariationDetail;
import com.zubayer.entity.Xfloor;
import com.zubayer.entity.Xtable;
import com.zubayer.entity.Xusers;
import com.zubayer.entity.Zbusiness;
import com.zubayer.entity.pk.OutletPK;
import com.zubayer.entity.pk.ShopPK;
import com.zubayer.entity.pk.TerminalPK;
import com.zubayer.repository.AddOnsRepo;
import com.zubayer.repository.CategoryRepo;
import com.zubayer.repository.ChargeRepo;
import com.zubayer.repository.CurrencyRepo;
import com.zubayer.repository.ItemRepo;
import com.zubayer.repository.OutletRepo;
import com.zubayer.repository.ShopRepo;
import com.zubayer.repository.TerminalRepo;
import com.zubayer.repository.UOMRepo;
import com.zubayer.repository.VariationDetailRepo;
import com.zubayer.repository.VariationRepo;
import com.zubayer.repository.XfloorRepo;
import com.zubayer.repository.XtableRepo;
import com.zubayer.repository.XusersRepo;
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
	@Autowired private XusersRepo xusersRepo;
	@Autowired private UOMRepo uomRepo;
	@Autowired private ChargeRepo chargeRepo;
	@Autowired private CategoryRepo categoryRepo;
	@Autowired private AddOnsRepo addOnsRepo;
	@Autowired private VariationRepo variationRepo;
	@Autowired private VariationDetailRepo variationDetailRepo;
	@Autowired private ItemRepo itemRepo;
	@Autowired private CurrencyRepo currencyRepo;
	@Autowired private XfloorRepo floorRepo;
	@Autowired private XtableRepo tableRepo;

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

	@GetMapping("/posusers")
	public List<Xusers> syncPOSUsers(@RequestParam Integer businessid, @RequestParam Integer outletid, @RequestParam Integer shopid){
		return xusersRepo.findAllByZidAndXoutletAndXshop(businessid, outletid, shopid);
	}

	@GetMapping("/uom")
	public List<UOM> syncUOM(@RequestParam Integer businessid){
		return uomRepo.findAllByZid(businessid);
	}

	@GetMapping("/charges")
	public List<Charge> syncCharges(@RequestParam Integer businessid){
		return chargeRepo.findAllByZid(businessid);
	}

	@GetMapping("/currencies")
	public List<Currency> syncCurrencies(@RequestParam Integer businessid){
		return currencyRepo.findAllByZid(businessid);
	}

	@GetMapping("/floors")
	public List<Xfloor> syncFloors(@RequestParam Integer businessid, @RequestParam Integer outletid, @RequestParam Integer shopid){
		return floorRepo.findAllByZidAndXoutletAndXshop(businessid, outletid, shopid);
	}

	@GetMapping("/tables")
	public List<Xtable> syncTables(@RequestParam Integer businessid, @RequestParam Integer outletid, @RequestParam Integer shopid){
		return tableRepo.findAllByZidAndXoutletAndXshop(businessid, outletid, shopid);
	}

	@GetMapping("/category")
	public List<Category> syncCategory(@RequestParam Integer businessid){
		return categoryRepo.findAllByZid(businessid);
	}

	@GetMapping("/addons")
	public List<AddOns> syncAddOns(@RequestParam Integer businessid){
		return addOnsRepo.findAllByZid(businessid);
	}

	@GetMapping("/variation")
	public List<Variation> syncVariation(@RequestParam Integer businessid){
		return variationRepo.findAllByZid(businessid);
	}

	@GetMapping("/variation-detail")
	public List<VariationDetail> syncVariationDetail(@RequestParam Integer businessid){
		return variationDetailRepo.findAllByZid(businessid);
	}

	@GetMapping("/item")
	public List<Item> syncItem(@RequestParam Integer businessid){
		return itemRepo.findAllByZid(businessid);
	}

	
}
