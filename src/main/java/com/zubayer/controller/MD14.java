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

import com.zubayer.entity.AddOns;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.AddOnsPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.AddOnsRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/MD14")
public class MD14 extends AbstractBaseController {

	@Autowired private AddOnsRepo addonsRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "MD14"));
		if(!op.isPresent()) return "Category";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String xcode, Model model, HttpServletRequest request) throws ResourceNotFoundException {

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(xcode)) {
				model.addAttribute("addons", AddOns.getDefaultInstance());
				return "pages/MD14/MD14-fragments::main-form";
			}

			Optional<AddOns> addonsOp = addonsRepo.findById(new AddOnsPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!addonsOp.isPresent()) {
				model.addAttribute("addons", AddOns.getDefaultInstance());
				return "pages/MD14/MD14-fragments::main-form";
			}

			model.addAttribute("addons", addonsOp.get());
			return "pages/MD14/MD14-fragments::main-form";
		}

		if(StringUtils.isNotBlank(xcode) && !"RESET".equalsIgnoreCase(xcode)) {
			Optional<AddOns> addonsOp = addonsRepo.findById(new AddOnsPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!addonsOp.isPresent()) {
				model.addAttribute("addons", AddOns.getDefaultInstance());
				return "pages/MD14/MD14";
			}

			model.addAttribute("addons", addonsOp.get());
			return "pages/MD14/MD14";
		}

		model.addAttribute("addons", AddOns.getDefaultInstance());
		return "pages/MD14/MD14";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(AddOns addons, BindingResult bindingResult){

		// VALIDATE outlet
		modelValidator.validateAddOns(addons, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(addons.getSubmitFor())) {
			addons.setXcode(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "MD14"));
			addons.setZid(sessionManager.getBusinessId());
			addons = addonsRepo.save(addons);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/MD14?xcode=" + addons.getXcode()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<AddOns> op = addonsRepo.findById(new AddOnsPK(sessionManager.getBusinessId(), addons.getXcode()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		AddOns existObj = op.get();
		BeanUtils.copyProperties(addons, existObj, "zid", "createdBy", "createdOn", "xcode");
		existObj = addonsRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD14?xcode=" + addons.getXcode()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated Successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer xcode){
		Optional<AddOns> op = addonsRepo.findById(new AddOnsPK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		AddOns obj = op.get();
		addonsRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD14?xcode=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted Successfully");
		return responseHelper.getResponse();
	}
}
