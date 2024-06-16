package com.zubayer.controller;

import java.util.ArrayList;
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

import com.zubayer.entity.Category;
import com.zubayer.entity.Item;
import com.zubayer.entity.ItemVariations;
import com.zubayer.entity.Variation;
import com.zubayer.entity.VariationDetail;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.CategoryPK;
import com.zubayer.entity.pk.ItemPK;
import com.zubayer.entity.pk.ItemVariationsPK;
import com.zubayer.entity.pk.VariationDetailPK;
import com.zubayer.entity.pk.VariationPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.ChargeType;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.CategoryRepo;
import com.zubayer.repository.ChargeRepo;
import com.zubayer.repository.ItemRepo;
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
		BeanUtils.copyProperties(item, existObj, "zid", "createdBy", "createdOn", "xcode");
		existObj = itemRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + item.getXcode()));
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
			reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + itemVariations.getXitem()));
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
		reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + existObj.getXitem()));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD16/detail-table?xitem=" + existObj.getXitem() + "&xvariation=" + existObj.getXvariation()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated Successfully");
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

		Item obj = op.get();
		itemRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=RESET"));
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
		reloadSections.add(new ReloadSection("main-form-container", "/MD16?xcode=" + xitem));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD16/detail-table?xitem="+xitem+"&xvariation=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}
}
