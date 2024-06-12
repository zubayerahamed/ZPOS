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
import com.zubayer.entity.Xfloor;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.OutletPK;
import com.zubayer.entity.pk.ShopPK;
import com.zubayer.entity.pk.XfloorPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.OutletRepo;
import com.zubayer.repository.ShopRepo;
import com.zubayer.repository.XfloorRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD18")
public class AD18 extends AbstractBaseController {

	@Autowired private ShopRepo shopRepo;
	@Autowired private OutletRepo outletRepo;
	@Autowired private XfloorRepo xfloorRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD18"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String id, @RequestParam(required = false) String xoutlet, @RequestParam(required = false) String xshop, Model model, HttpServletRequest request) throws ResourceNotFoundException {

		List<Xfloor> xfloors = xfloorRepo.findAllByZid(sessionManager.getBusinessId());
		xfloors.forEach(t -> {
			Optional<Shop> shopOp = shopRepo.findById(new ShopPK(sessionManager.getBusinessId(), t.getXoutlet(), t.getXshop()));
			if(shopOp.isPresent()) t.setShopName(shopOp.get().getXname());

			Optional<Outlet> outletOp = outletRepo.findById(new OutletPK(sessionManager.getBusinessId(), t.getXoutlet()));
			if(outletOp.isPresent()) t.setOutletName(outletOp.get().getXname());
		});

		Map<String, Map<String, List<Xfloor>>> floorsMap = xfloors.stream().collect(Collectors.groupingBy(Xfloor::getOutletName, Collectors.groupingBy(Xfloor::getShopName)));

		model.addAttribute("floorsMap", floorsMap);

		List<Outlet> outlets = outletRepo.findAllByZid(sessionManager.getBusinessId());
		outlets = outlets.stream().filter(o -> o.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		outlets.sort(Comparator.comparing(Outlet::getXname));
		model.addAttribute("outlets", outlets);

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(id)) {
				model.addAttribute("xfloor", Xfloor.getDefaultInstance());
				return "pages/AD18/AD18-fragments::main-form";
			}

			Optional<Xfloor> xfloorOp = xfloorRepo.findById(new XfloorPK(sessionManager.getBusinessId(), Integer.valueOf(xoutlet), Integer.valueOf(xshop), Integer.parseInt(id)));
			if(!xfloorOp.isPresent()) {
				model.addAttribute("xfloor", Xfloor.getDefaultInstance());
			}

			model.addAttribute("xfloor", xfloorOp.get());

			List<Shop> shops = shopRepo.findAllByZidAndOutletId(sessionManager.getBusinessId(), Integer.parseInt(xoutlet));
			shops = shops.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
			shops.sort(Comparator.comparing(Shop::getXname));
			model.addAttribute("shops", shops);

			return "pages/AD18/AD18-fragments::main-form";
		}

		model.addAttribute("xfloor", Xfloor.getDefaultInstance());
		return "pages/AD18/AD18";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Xfloor xfloor, BindingResult bindingResult){

		// VALIDATE Shop
		modelValidator.validateXfloor(xfloor, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		if(xfloor.getXoutlet() == null) {
			responseHelper.setErrorStatusAndMessage("Outlet required");
			return responseHelper.getResponse();
		}

		if(xfloor.getXshop() == null) {
			responseHelper.setErrorStatusAndMessage("Shop required");
			return responseHelper.getResponse();
		}

		// Create new
		if(SubmitFor.INSERT.equals(xfloor.getSubmitFor())) {
			xfloor.setId(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "AD18"));
			xfloor.setZid(sessionManager.getBusinessId());
			xfloor = xfloorRepo.save(xfloor);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD18?id=" + xfloor.getId() + "&xoutlet=" + xfloor.getXoutlet() + "&xshop=" + xfloor.getXshop()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Xfloor> op = xfloorRepo.findById(new XfloorPK(sessionManager.getBusinessId(), xfloor.getXoutlet(), xfloor.getXshop(), xfloor.getId()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Xfloor existObj = op.get();
		BeanUtils.copyProperties(xfloor, existObj, "zid", "createdBy", "createdOn", "xoutlet", "xshop");
		existObj = xfloorRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD18?id=" + xfloor.getId() + "&xoutlet=" + xfloor.getXoutlet() + "&xshop=" + xfloor.getXshop()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer id, @RequestParam Integer xoutlet, @RequestParam Integer xshop){
		Optional<Xfloor> op = xfloorRepo.findById(new XfloorPK(sessionManager.getBusinessId(), xoutlet, xshop, id));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Xfloor obj = op.get();
		xfloorRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD18?id=RESET&xoutlet=RESET&xshop=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}

	@GetMapping("/xshop-field")
	public String loadXsaddFieldFragment(@RequestParam Integer xoutlet, @RequestParam(required = false) Integer xfloorid,  Model model){
		List<Shop> shops = shopRepo.findAllByZidAndOutletId(sessionManager.getBusinessId(), xoutlet);
		shops = shops.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		shops.sort(Comparator.comparing(Shop::getXname));
		model.addAttribute("shops", shops);

		Xfloor xfloor = Xfloor.getDefaultInstance();
		if(xfloorid != 0) {
			xfloor.setId(xfloorid);
		}
		model.addAttribute("xfloor", xfloor);

		return "pages/AD18/AD18-fragments::xshop-field";
	}
}
