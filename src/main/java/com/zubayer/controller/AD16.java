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

import com.zubayer.entity.UOM;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.UOMPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.UOMRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD16")
public class AD16 extends AbstractBaseController {

	@Autowired private UOMRepo uomRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD16"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String xcode, Model model, HttpServletRequest request) throws ResourceNotFoundException {
		model.addAttribute("uoms", uomRepo.findAllByZid(sessionManager.getBusinessId()));

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(xcode)) {
				model.addAttribute("uom", UOM.getDefaultInstance());
				return "pages/AD16/AD16-fragments::main-form";
			}

			Optional<UOM> uomOp = uomRepo.findById(new UOMPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!uomOp.isPresent()) {
				model.addAttribute("uom", UOM.getDefaultInstance());
			}

			model.addAttribute("uom", uomOp.get());
			return "pages/AD16/AD16-fragments::main-form";
		}

		if(StringUtils.isNotBlank(xcode) && !"RESET".equalsIgnoreCase(xcode)) {
			Optional<UOM> uomOp = uomRepo.findById(new UOMPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!uomOp.isPresent()) {
				model.addAttribute("uom", UOM.getDefaultInstance());
				return "pages/AD16/AD16";
			}

			model.addAttribute("uom", uomOp.get());
			return "pages/AD16/AD16";
		}

		model.addAttribute("uom", UOM.getDefaultInstance());
		return "pages/AD16/AD16";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(UOM uom, BindingResult bindingResult){

		// VALIDATE outlet
		modelValidator.validateUOM(uom, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(uom.getSubmitFor())) {
			uom.setXcode(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "AD16"));
			uom.setZid(sessionManager.getBusinessId());
			uom = uomRepo.save(uom);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD16?xcode=" + uom.getXcode()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<UOM> op = uomRepo.findById(new UOMPK(sessionManager.getBusinessId(), uom.getXcode()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		UOM existObj = op.get();
		BeanUtils.copyProperties(uom, existObj, "zid", "xcode", "createdBy", "createdOn");
		existObj = uomRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD16?xcode=" + uom.getXcode()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer xcode){
		Optional<UOM> op = uomRepo.findById(new UOMPK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		UOM obj = op.get();
		uomRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD16?xcode=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted Successfully");
		return responseHelper.getResponse();
	}
}
