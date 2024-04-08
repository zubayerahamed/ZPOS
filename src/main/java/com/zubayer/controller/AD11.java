package com.zubayer.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.OutletPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.OutletRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202101068
 */
@Controller
@RequestMapping("/AD11")
public class AD11 extends AbstractBaseController {

	@Autowired private OutletRepo outletRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD11"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String id, Model model, HttpServletRequest request) throws ResourceNotFoundException {
		List<Outlet> outlets = outletRepo.findAllByZid(sessionManager.getBusinessId());
		model.addAttribute("outlets", outlets);

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(id)) {
				model.addAttribute("outlet", Outlet.getDefaultInstance());
				return "pages/AD11/AD11-fragments::main-form";
			}

			Optional<Outlet> outletOp = outletRepo.findById(new OutletPK(sessionManager.getBusinessId(), Integer.parseInt(id)));
			if(!outletOp.isPresent()) {
				model.addAttribute("outlet", Outlet.getDefaultInstance());
			}

			model.addAttribute("outlet", outletOp.get());
			return "pages/AD11/AD11-fragments::main-form";
		}

		model.addAttribute("outlet", Outlet.getDefaultInstance());
		return "pages/AD11/AD11";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Outlet outlet, BindingResult bindingResult){

		// VALIDATE outlet
		modelValidator.validateOutlet(outlet, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(outlet.getSubmitFor())) {
			outlet.setId(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "AD11"));
			outlet.setZid(sessionManager.getBusinessId());
			outlet = outletRepo.save(outlet);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD11?id=" + outlet.getId()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Outlet> op = outletRepo.findById(new OutletPK(sessionManager.getBusinessId(), outlet.getId()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Outlet existObj = op.get();
		BeanUtils.copyProperties(outlet, existObj, "zid", "createdBy", "createdOn");
		existObj = outletRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD11?id=" + outlet.getId()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer id){
		Optional<Outlet> op = outletRepo.findById(new OutletPK(sessionManager.getBusinessId(), id));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Outlet obj = op.get();
		outletRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD11?id=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}
}
