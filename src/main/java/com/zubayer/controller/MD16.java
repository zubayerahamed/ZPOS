package com.zubayer.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zubayer.entity.AddOns;
import com.zubayer.entity.Category;
import com.zubayer.entity.Item;
import com.zubayer.entity.ItemAddons;
import com.zubayer.entity.ItemSets;
import com.zubayer.entity.ItemVariations;
import com.zubayer.entity.UOM;
import com.zubayer.entity.Variation;
import com.zubayer.entity.VariationDetail;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.AddOnsPK;
import com.zubayer.entity.pk.CategoryPK;
import com.zubayer.entity.pk.ItemAddonsPK;
import com.zubayer.entity.pk.ItemPK;
import com.zubayer.entity.pk.ItemSetsPK;
import com.zubayer.entity.pk.ItemVariationsPK;
import com.zubayer.entity.pk.UOMPK;
import com.zubayer.entity.pk.VariationPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.ChargeType;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.AddOnsRepo;
import com.zubayer.repository.CategoryRepo;
import com.zubayer.repository.ChargeRepo;
import com.zubayer.repository.ItemAddonsRepo;
import com.zubayer.repository.ItemRepo;
import com.zubayer.repository.ItemSetsRepo;
import com.zubayer.repository.ItemVariationsRepo;
import com.zubayer.repository.UOMRepo;
import com.zubayer.repository.VariationDetailRepo;
import com.zubayer.repository.VariationRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/MD16")
public class MD16 extends AbstractBaseController {

	@Autowired private ItemRepo itemRepo;
	@Autowired private UOMRepo uomRepo;
	@Autowired private ChargeRepo chargeRepo;
	@Autowired private CategoryRepo categoryRepo;
	@Autowired private ItemVariationsRepo itemVariationsRepo;
	@Autowired private VariationDetailRepo variationDetailRepo;
	@Autowired private VariationRepo variationRepo;
	@Autowired private AddOnsRepo addonsRepo;
	@Autowired private ItemAddonsRepo itemAddonsRepo;
	@Autowired private ItemSetsRepo itemSetsRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "MD16"));
		if(!op.isPresent()) return "Category";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String xcode, Model model, HttpServletRequest request) throws ResourceNotFoundException {
		model.addAttribute("uoms", uomRepo.findAllByZid(sessionManager.getBusinessId()));
		model.addAttribute("vats", chargeRepo.findAllByXtypeAndZid(ChargeType.VAT, sessionManager.getBusinessId()));
		model.addAttribute("sds", chargeRepo.findAllByXtypeAndZid(ChargeType.SD, sessionManager.getBusinessId()));

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(xcode)) {
				model.addAttribute("item", Item.getDefaultInstance());
				return "pages/MD16/MD16-fragments::main-form";
			}

			Optional<Item> itemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!itemOp.isPresent()) {
				model.addAttribute("item", Item.getDefaultInstance());
				return "pages/MD16/MD16-fragments::main-form";
			}

			Optional<Category> categoryOp = categoryRepo.findById(new CategoryPK(sessionManager.getBusinessId(), itemOp.get().getXcat()));
			if(categoryOp.isPresent()) itemOp.get().setCategoryname(categoryOp.get().getXname());

			model.addAttribute("item", itemOp.get());
			return "pages/MD16/MD16-fragments::main-form";
		}

		if(StringUtils.isNotBlank(xcode) && !"RESET".equalsIgnoreCase(xcode)) {
			Optional<Item> itemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!itemOp.isPresent()) {
				model.addAttribute("item", Item.getDefaultInstance());
				return "pages/MD16/MD16";
			}

			Optional<Category> categoryOp = categoryRepo.findById(new CategoryPK(sessionManager.getBusinessId(), itemOp.get().getXcat()));
			if(categoryOp.isPresent()) itemOp.get().setCategoryname(categoryOp.get().getXname());

			model.addAttribute("item", itemOp.get());
			return "pages/MD16/MD16";
		}

		model.addAttribute("item", Item.getDefaultInstance());
		return "pages/MD16/MD16";
	}

	@GetMapping("/detail-table")
	public String detailFormFragment(@RequestParam String xitem, @RequestParam String xvariation, Model model) {
		if("RESET".equalsIgnoreCase(xitem) && "RESET".equalsIgnoreCase(xvariation)) {
			return "pages/MD16/MD16-fragments::detail-table";
		}

		Optional<Item> itemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), Integer.valueOf(xitem)));
		if(!itemOp.isPresent()) return "pages/MD16/MD16-fragments::detail-table";
		model.addAttribute("item", itemOp.get());

		List<ItemVariations> itemVariations = itemVariationsRepo.findAllByZidAndXitem(sessionManager.getBusinessId(), Integer.valueOf(xitem));
		itemVariations.stream().forEach(iv -> {
			Optional<Variation> variationOp = variationRepo.findById(new VariationPK(sessionManager.getBusinessId(), iv.getXvariation()));
			if(variationOp.isPresent()) iv.setVariationName(variationOp.get().getXname());
	
			List<VariationDetail> options = variationDetailRepo.findAllByZidAndXcode(sessionManager.getBusinessId(), iv.getXvariation());
			if(options != null) iv.setOptions(options);
		});
		model.addAttribute("itemVariations", itemVariations);

		if("RESET".equalsIgnoreCase(xvariation)) {
			model.addAttribute("itemVariation", ItemVariations.getDefaultInstance(Integer.valueOf(xitem)));
			return "pages/MD16/MD16-fragments::detail-table";
		}

		Optional<ItemVariations> vtOp = itemVariationsRepo.findById(new ItemVariationsPK(sessionManager.getBusinessId(), Integer.valueOf(xitem), Integer.valueOf(xvariation)));
		if(vtOp.isPresent()) {
			Optional<Variation> variationOp = variationRepo.findById(new VariationPK(sessionManager.getBusinessId(), vtOp.get().getXvariation()));
			if(variationOp.isPresent()) vtOp.get().setVariationName(variationOp.get().getXname());
		}
		ItemVariations itemVariation = vtOp.isPresent() ? vtOp.get() : ItemVariations.getDefaultInstance(Integer.valueOf(xitem));
		model.addAttribute("itemVariation", itemVariation);
		return "pages/MD16/MD16-fragments::detail-table";
	}

	@GetMapping("/addons-table")
	public String addonsFormFragment(@RequestParam String xitem, @RequestParam String xaddons, Model model) {
		if("RESET".equalsIgnoreCase(xitem) && "RESET".equalsIgnoreCase(xaddons)) {
			return "pages/MD16/MD16-fragments::addons-table";
		}

		Optional<Item> itemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), Integer.valueOf(xitem)));
		if(!itemOp.isPresent()) return "pages/MD16/MD16-fragments::addons-table";
		model.addAttribute("item", itemOp.get());

		List<ItemAddons> itemAddonsList = itemAddonsRepo.findAllByZidAndXitem(sessionManager.getBusinessId(), Integer.valueOf(xitem));
		itemAddonsList.stream().forEach(iv -> {
			Optional<AddOns> addonsOp = addonsRepo.findById(new AddOnsPK(sessionManager.getBusinessId(), iv.getXaddons()));
			if(addonsOp.isPresent()) {
				iv.setAddonsName(addonsOp.get().getXname());
				iv.setAddonsPrice(addonsOp.get().getXprice());
			}
		});
		model.addAttribute("itemAddonsList", itemAddonsList);

		if("RESET".equalsIgnoreCase(xaddons)) {
			model.addAttribute("itemAddons", ItemAddons.getDefaultInstance(Integer.valueOf(xitem)));
			return "pages/MD16/MD16-fragments::addons-table";
		}

		ItemAddons itemAddons = ItemAddons.getDefaultInstance(Integer.valueOf(xitem));
		Optional<ItemAddons> vtOp = itemAddonsRepo.findById(new ItemAddonsPK(sessionManager.getBusinessId(), Integer.valueOf(xitem), Integer.valueOf(xaddons)));
		if(vtOp.isPresent()) {
			Optional<AddOns> addonsOp = addonsRepo.findById(new AddOnsPK(sessionManager.getBusinessId(), vtOp.get().getXaddons()));
			if(addonsOp.isPresent()) {
				vtOp.get().setAddonsName(addonsOp.get().getXname());
				vtOp.get().setAddonsPrice(addonsOp.get().getXprice());
			}
			itemAddons = vtOp.get();
		} else {
			Optional<AddOns> addonsOp = addonsRepo.findById(new AddOnsPK(sessionManager.getBusinessId(), Integer.valueOf(xaddons)));
			if(addonsOp.isPresent()) {
				itemAddons.setXaddons(addonsOp.get().getXcode());
				itemAddons.setAddonsName(addonsOp.get().getXname());
				itemAddons.setAddonsPrice(addonsOp.get().getXprice());
			}
		}

		model.addAttribute("itemAddons", itemAddons);
		return "pages/MD16/MD16-fragments::addons-table";
	}

	@GetMapping("/sets-table")
	public String setsFormFragment(@RequestParam String xitem, @RequestParam String xsetitem, Model model) {
		if("RESET".equalsIgnoreCase(xitem) && "RESET".equalsIgnoreCase(xsetitem)) {
			return "pages/MD16/MD16-fragments::sets-table";
		}

		Optional<Item> itemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), Integer.valueOf(xitem)));
		if(!itemOp.isPresent() || Boolean.FALSE.equals(itemOp.get().getXsetmenu())) return "pages/MD16/MD16-fragments::sets-table";
		model.addAttribute("item", itemOp.get());

		List<ItemSets> itemSetsList = itemSetsRepo.findAllByZidAndXitem(sessionManager.getBusinessId(), Integer.valueOf(xitem));
		itemSetsList.stream().forEach(iv -> {
			Optional<Item> setItemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), iv.getXsetitem()));
			if(setItemOp.isPresent()) {
				iv.setSetItemName(setItemOp.get().getXname());
				Optional<UOM> uomOp = uomRepo.findById(new UOMPK(sessionManager.getBusinessId(), setItemOp.get().getXuom()));
				if(uomOp.isPresent()) iv.setSetItemUnit(uomOp.get().getXname());
			}
		});
		model.addAttribute("itemSetsList", itemSetsList);

		if("RESET".equalsIgnoreCase(xsetitem)) {
			model.addAttribute("itemSets", ItemSets.getDefaultInstance(Integer.valueOf(xitem)));
			return "pages/MD16/MD16-fragments::sets-table";
		}

		ItemSets itemSets = ItemSets.getDefaultInstance(Integer.valueOf(xitem));
		Optional<ItemSets> iv = itemSetsRepo.findById(new ItemSetsPK(sessionManager.getBusinessId(), Integer.valueOf(xitem), Integer.valueOf(xsetitem)));
		if(iv.isPresent()) {
			Optional<Item> setItemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), iv.get().getXsetitem()));
			if(setItemOp.isPresent()) {
				iv.get().setSetItemName(setItemOp.get().getXname());
				Optional<UOM> uomOp = uomRepo.findById(new UOMPK(sessionManager.getBusinessId(), setItemOp.get().getXuom()));
				if(uomOp.isPresent()) iv.get().setSetItemUnit(uomOp.get().getXname());
			}
			itemSets = iv.get();
		} else {
			Optional<Item> setItemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), Integer.valueOf(xsetitem)));
			if(setItemOp.isPresent()) {
				itemSets.setXsetitem(setItemOp.get().getXcode());
				itemSets.setSetItemName(setItemOp.get().getXname());
				Optional<UOM> uomOp = uomRepo.findById(new UOMPK(sessionManager.getBusinessId(), setItemOp.get().getXuom()));
				if(uomOp.isPresent()) itemSets.setSetItemUnit(uomOp.get().getXname());
			}
		}

		model.addAttribute("itemSets", itemSets);
		return "pages/MD16/MD16-fragments::sets-table";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Item item, BindingResult bindingResult){

		// VALIDATE outlet
		modelValidator.validateItem(item, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(item.getSubmitFor())) {
			item.setXcode(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "MD16"));
			item.setZid(sessionManager.getBusinessId());
			item = itemRepo.save(item);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + item.getXcode()));
			reloadSections.add(new ReloadSection("detail-table-container", "/MD16/detail-table?xitem=" + item.getXcode() + "&xvariation=RESET"));
			reloadSections.add(new ReloadSection("addons-table-container", "/MD16/addons-table?xitem=" + item.getXcode() + "&xaddons=RESET"));
			reloadSections.add(new ReloadSection("sets-table-container", "/MD16/sets-table?xitem=" + item.getXcode() + "&xsetitem=RESET"));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Item> op = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), item.getXcode()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Item existObj = op.get();
		boolean ispreviousSetMenuItem = existObj.getXsetmenu();
		BeanUtils.copyProperties(item, existObj, "zid", "createdBy", "createdOn", "xcode");
		existObj = itemRepo.save(existObj);

		// check existing is set menu items but current is not set menu, then delete all previous set menu items 
		if(Boolean.TRUE.equals(ispreviousSetMenuItem) && Boolean.FALSE.equals(item.getXsetmenu())) {
			List<ItemSets> existingItemSets = itemSetsRepo.findAllByZidAndXitem(sessionManager.getBusinessId(), item.getXcode());
			if(existingItemSets != null && !existingItemSets.isEmpty()) {
				itemSetsRepo.deleteAll(existingItemSets);
			}
		}

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + item.getXcode()));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD16/detail-table?xitem=" + item.getXcode() + "&xvariation=RESET"));
		reloadSections.add(new ReloadSection("addons-table-container", "/MD16/addons-table?xitem=" + item.getXcode() + "&xaddons=RESET"));
		reloadSections.add(new ReloadSection("sets-table-container", "/MD16/sets-table?xitem=" + item.getXcode() + "&xsetitem=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated Successfully");
		return responseHelper.getResponse();
	}

	@PostMapping("/detail/store")
	public @ResponseBody Map<String, Object> storeDetail(ItemVariations itemVariations, BindingResult bindingResult){

		// VALIDATE 
		Optional<Variation> vOp = variationRepo.findById(new VariationPK(sessionManager.getBusinessId(), itemVariations.getXvariation()));
		if(!vOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Variation not found");
			return responseHelper.getResponse();
		}

		if(itemVariations.getXitem() == null) {
			responseHelper.setErrorStatusAndMessage("Invalid operation");
			return responseHelper.getResponse();
		}

		// Create new
		if(SubmitFor.INSERT.equals(itemVariations.getSubmitFor())) {
			// CHeck duplicate
			Optional<ItemVariations> ivOp = itemVariationsRepo.findById(new ItemVariationsPK(sessionManager.getBusinessId(), itemVariations.getXitem(), itemVariations.getXvariation()));
			if(ivOp.isPresent()) {
				responseHelper.setErrorStatusAndMessage("Variation already added");
				return responseHelper.getResponse();
			}

			itemVariations.setZid(sessionManager.getBusinessId());
			itemVariations = itemVariationsRepo.save(itemVariations);

			List<ReloadSection> reloadSections = new ArrayList<>();
			//reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + itemVariations.getXitem()));
			reloadSections.add(new ReloadSection("detail-table-container", "/MD16/detail-table?xitem=" + itemVariations.getXitem() + "&xvariation=RESET"));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<ItemVariations> ivOp = itemVariationsRepo.findById(new ItemVariationsPK(sessionManager.getBusinessId(), itemVariations.getXitem(), itemVariations.getXvariation()));
		if(!ivOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		String[] ignoreProperties = new String[] {
			"zid", 
			"createdBy", 
			"createdOn",
			"xitem", 
			"xvariation"
		};

		ItemVariations existObj = ivOp.get();
		BeanUtils.copyProperties(itemVariations, existObj, ignoreProperties);
		existObj = itemVariationsRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		//reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + existObj.getXitem()));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD16/detail-table?xitem=" + existObj.getXitem() + "&xvariation=" + existObj.getXvariation()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated Successfully");
		return responseHelper.getResponse();
	}

	@PostMapping("/addons/store")
	public @ResponseBody Map<String, Object> storeAddons(ItemAddons itemAddons, BindingResult bindingResult){

		// VALIDATE 
		Optional<AddOns> vOp = addonsRepo.findById(new AddOnsPK(sessionManager.getBusinessId(), itemAddons.getXaddons()));
		if(!vOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("AddOns not found");
			return responseHelper.getResponse();
		}

		if(itemAddons.getXitem() == null) {
			responseHelper.setErrorStatusAndMessage("Invalid operation");
			return responseHelper.getResponse();
		}

		// Create new
		if(SubmitFor.INSERT.equals(itemAddons.getSubmitFor())) {
			// CHeck duplicate
			Optional<ItemAddons> ivOp = itemAddonsRepo.findById(new ItemAddonsPK(sessionManager.getBusinessId(), itemAddons.getXitem(), itemAddons.getXaddons()));
			if(ivOp.isPresent()) {
				responseHelper.setErrorStatusAndMessage("AddOns already added");
				return responseHelper.getResponse();
			}

			itemAddons.setZid(sessionManager.getBusinessId());
			itemAddons = itemAddonsRepo.save(itemAddons);

			List<ReloadSection> reloadSections = new ArrayList<>();
			//reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + itemAddons.getXitem()));
			reloadSections.add(new ReloadSection("addons-table-container", "/MD16/addons-table?xitem=" + itemAddons.getXitem() + "&xaddons=RESET"));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		responseHelper.setErrorStatusAndMessage("Update not allowed");
		return responseHelper.getResponse();
	}

	@PostMapping("/sets/store")
	public @ResponseBody Map<String, Object> storeSetItems(ItemSets itemSets, BindingResult bindingResult){

		// VALIDATE 
		Optional<Item> itemsOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), itemSets.getXsetitem()));
		if(!itemsOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Item not found");
			return responseHelper.getResponse();
		}

		if(itemSets.getXitem() == null) {
			responseHelper.setErrorStatusAndMessage("Invalid operation");
			return responseHelper.getResponse();
		}

		if(itemSets.getXqty().compareTo(BigDecimal.ZERO) != 1) {
			responseHelper.setErrorStatusAndMessage("Invalid quantity");
			return responseHelper.getResponse();
		}

		// Create new
		if(SubmitFor.INSERT.equals(itemSets.getSubmitFor())) {
			// CHeck duplicate
			Optional<ItemSets> ivOp = itemSetsRepo.findById(new ItemSetsPK(sessionManager.getBusinessId(), itemSets.getXitem(), itemSets.getXsetitem()));
			if(ivOp.isPresent()) {
				responseHelper.setErrorStatusAndMessage("Set item already added");
				return responseHelper.getResponse();
			}

			itemSets.setZid(sessionManager.getBusinessId());
			itemSets = itemSetsRepo.save(itemSets);

			List<ReloadSection> reloadSections = new ArrayList<>();
			//reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + itemSets.getXitem()));
			reloadSections.add(new ReloadSection("sets-table-container", "/MD16/sets-table?xitem=" + itemSets.getXitem() + "&xsetitem=RESET"));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		responseHelper.setErrorStatusAndMessage("Update not allowed");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer xcode){
		Optional<Item> op = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		// Delete all details first
		// item variations
		List<ItemVariations> itemVariations = itemVariationsRepo.findAllByZidAndXitem(sessionManager.getBusinessId(), xcode);
		if(itemVariations != null && !itemVariations.isEmpty()) {
			itemVariationsRepo.deleteAll(itemVariations);
		}

		// addons
		List<ItemAddons> itemAddonsList = itemAddonsRepo.findAllByZidAndXitem(sessionManager.getBusinessId(), xcode);
		if(itemAddonsList != null && !itemAddonsList.isEmpty()) {
			itemAddonsRepo.deleteAll(itemAddonsList);
		}

		// set items
		List<ItemSets> itemSetsList = itemSetsRepo.findAllByZidAndXitem(sessionManager.getBusinessId(), xcode);
		if(itemSetsList != null && !itemSetsList.isEmpty()) {
			itemSetsRepo.deleteAll(itemSetsList);
		}

		Item obj = op.get();
		itemRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=RESET"));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD16/detail-table?xitem=RESET&xvariation=RESET"));
		reloadSections.add(new ReloadSection("addons-table-container", "/MD16/addons-table?xitem=RESET&xaddons=RESET"));
		reloadSections.add(new ReloadSection("sets-table-container", "/MD16/sets-table?xitem=RESET&xsetitem=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted Successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping("/detail-table")
	public @ResponseBody Map<String, Object> deleteDetail(@RequestParam Integer xitem, @RequestParam Integer xvariation){
		Optional<ItemVariations> ivOp = itemVariationsRepo.findById(new ItemVariationsPK(sessionManager.getBusinessId(), xitem, xvariation));
		if(!ivOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		ItemVariations obj = ivOp.get();
		itemVariationsRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		//reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + xitem));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD16/detail-table?xitem="+xitem+"&xvariation=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping("/addons-table")
	public @ResponseBody Map<String, Object> deleteAddons(@RequestParam Integer xitem, @RequestParam Integer xaddons){
		Optional<ItemAddons> ivOp = itemAddonsRepo.findById(new ItemAddonsPK(sessionManager.getBusinessId(), xitem, xaddons));
		if(!ivOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		ItemAddons obj = ivOp.get();
		itemAddonsRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		//reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + xitem));
		reloadSections.add(new ReloadSection("addons-table-container", "/MD16/addons-table?xitem="+xitem+"&xaddons=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping("/sets-table")
	public @ResponseBody Map<String, Object> deleteSets(@RequestParam Integer xitem, @RequestParam Integer xsetitem){
		Optional<ItemSets> ivOp = itemSetsRepo.findById(new ItemSetsPK(sessionManager.getBusinessId(), xitem, xsetitem));
		if(!ivOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		ItemSets obj = ivOp.get();
		itemSetsRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		//reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + xitem));
		reloadSections.add(new ReloadSection("sets-table-container", "/MD16/sets-table?xitem="+xitem+"&xsetitem=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}

	@PostMapping("/image")
	public @ResponseBody Map<String, Object> uploadImage(String image, String id){
		if(StringUtils.isBlank(image)) {
			responseHelper.setErrorStatusAndMessage("Image required");
			return responseHelper.getResponse();
		}

		if(StringUtils.isBlank(id)) {
			responseHelper.setErrorStatusAndMessage("Invalid Operation");
			return responseHelper.getResponse();
		}

		Optional<Item> itemOp = itemRepo.findById(new ItemPK(sessionManager.getBusinessId(), Integer.valueOf(id)));
		if(!itemOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Item not found in this system");
			return responseHelper.getResponse();
		}

		// If the string contains a data URL prefix, remove it
		if (image.startsWith("data:")) {
			image = image.substring(image.indexOf(",") + 1);
		}

		// Validate the Base64 string
		if (!BASE64_PATTERN.matcher(image).matches()) {
			throw new IllegalArgumentException("Invalid Base64 string");
		}

		byte[] imageByte = Base64.getDecoder().decode(image);
		if(imageByte == null) {
			responseHelper.setErrorStatusAndMessage("Invalid Image");
			return responseHelper.getResponse();
		}

		Item item = itemOp.get();
		item.setXimage(imageByte);
		item = itemRepo.save(item);

		if(item == null) {
			responseHelper.setErrorStatusAndMessage("Image not saved");
			return responseHelper.getResponse();
		}

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + item.getXcode()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Image Saved Successfully");
		return responseHelper.getResponse();
	}
}
