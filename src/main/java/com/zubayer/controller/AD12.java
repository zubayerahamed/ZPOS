package com.zubayer.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

import com.zubayer.entity.Outlet;
import com.zubayer.entity.Shop;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.OutletPK;
import com.zubayer.entity.pk.ShopPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.OutletRepo;
import com.zubayer.repository.ShopRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD12")
public class AD12 extends AbstractBaseController {

	@Autowired private ShopRepo shopRepo;
	@Autowired private OutletRepo outletRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD12"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String id, @RequestParam(required = false) String outletid, Model model, HttpServletRequest request) throws ResourceNotFoundException {
		List<Shop> shops = shopRepo.findAllByZid(sessionManager.getBusinessId());
		shops.stream().forEach(f -> {
			Optional<Outlet> outletOp = outletRepo.findById(new OutletPK(sessionManager.getBusinessId(), f.getOutletId()));
			if(outletOp.isPresent()) {
				f.setOutletName(outletOp.get().getXname());
			}
		});

		Map<String, List<Shop>> shopMap = shops.stream().collect(Collectors.groupingBy(Shop::getOutletName));
		model.addAttribute("shopsMap", shopMap);

		List<Outlet> outlets = outletRepo.findAllByZid(sessionManager.getBusinessId());
		outlets = outlets.stream().filter(o -> o.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		outlets.sort(Comparator.comparing(Outlet::getXname));
		model.addAttribute("outlets", outlets);

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(id)) {
				model.addAttribute("shop", Shop.getDefaultInstance());
				return "pages/AD12/AD12-fragments::main-form";
			}

			Optional<Shop> shopOp = shopRepo.findById(new ShopPK(sessionManager.getBusinessId(), Integer.valueOf(outletid), Integer.parseInt(id)));
			if(!shopOp.isPresent()) {
				model.addAttribute("shop", Shop.getDefaultInstance());
			}

			model.addAttribute("shop", shopOp.get());
			return "pages/AD12/AD12-fragments::main-form";
		}

		model.addAttribute("shop", Shop.getDefaultInstance());
		return "pages/AD12/AD12";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Shop shop, BindingResult bindingResult){

		// VALIDATE Shop
		modelValidator.validateShop(shop, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		if(shop.getOutletId() == null) {
			responseHelper.setErrorStatusAndMessage("Outlet required");
			return responseHelper.getResponse();
		}

		// Create new
		if(SubmitFor.INSERT.equals(shop.getSubmitFor())) {
			shop.setId(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "AD12"));
			shop.setZid(sessionManager.getBusinessId());
			shop = shopRepo.save(shop);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD12?id=" + shop.getId() + "&outletid=" + shop.getOutletId()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Shop> op = shopRepo.findById(new ShopPK(sessionManager.getBusinessId(), shop.getOutletId(), shop.getId()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Shop existObj = op.get();
		BeanUtils.copyProperties(shop, existObj, "zid", "createdBy", "createdOn", "outletId");
		existObj = shopRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD12?id=" + shop.getId() + "&outletid=" + shop.getOutletId()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer id, @RequestParam Integer outletid){
		Optional<Shop> op = shopRepo.findById(new ShopPK(sessionManager.getBusinessId(), outletid, id));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Shop obj = op.get();
		shopRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD12?id=RESET&outletid=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}
}
