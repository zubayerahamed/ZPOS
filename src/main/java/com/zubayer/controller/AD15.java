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

import com.zubayer.entity.Charge;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.ChargePK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.ChargeType;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.ChargeRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD15")
public class AD15 extends AbstractBaseController {

	@Autowired private ChargeRepo chargeRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD15"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String xcode, Model model, HttpServletRequest request) throws ResourceNotFoundException {
		model.addAttribute("charges", chargeRepo.findAllByXtypeAndZid(ChargeType.SD, sessionManager.getBusinessId()));

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(xcode)) {
				model.addAttribute("charge", Charge.getDefaultInstance());
				return "pages/AD15/AD15-fragments::main-form";
			}

			Optional<Charge> chargeOp = chargeRepo.findById(new ChargePK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!chargeOp.isPresent()) {
				model.addAttribute("charge", Charge.getDefaultInstance());
			}

			model.addAttribute("charge", chargeOp.get());
			return "pages/AD15/AD15-fragments::main-form";
		}

		if(StringUtils.isNotBlank(xcode) && !"RESET".equalsIgnoreCase(xcode)) {
			Optional<Charge> chargeOp = chargeRepo.findById(new ChargePK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!chargeOp.isPresent()) {
				model.addAttribute("charge", Charge.getDefaultInstance());
				return "pages/AD15/AD15";
			}

			model.addAttribute("charge", chargeOp.get());
			return "pages/AD15/AD15";
		}

		model.addAttribute("charge", Charge.getDefaultInstance());
		return "pages/AD15/AD15";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Charge charge, BindingResult bindingResult){
		charge.setXtype(ChargeType.SD);

		// VALIDATE outlet
		modelValidator.validateCharge(charge, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(charge.getSubmitFor())) {
			charge.setXcode(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "AD15"));
			charge.setZid(sessionManager.getBusinessId());
			charge = chargeRepo.save(charge);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD15?xcode=" + charge.getXcode()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Charge> op = chargeRepo.findById(new ChargePK(sessionManager.getBusinessId(), charge.getXcode()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Charge not found in this system to do update");
			return responseHelper.getResponse();
		}

		Charge existObj = op.get();
		BeanUtils.copyProperties(charge, existObj, "zid", "xcode", "xtype", "createdBy", "createdOn");
		existObj = chargeRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD15?xcode=" + charge.getXcode()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer xcode){
		Optional<Charge> op = chargeRepo.findById(new ChargePK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Charge obj = op.get();
		chargeRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD15?xcode=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted Successfully");
		return responseHelper.getResponse();
	}
}
