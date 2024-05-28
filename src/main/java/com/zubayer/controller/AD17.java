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

import com.zubayer.entity.Xscreens;
import com.zubayer.entity.Xusers;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.entity.pk.XusersPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.XusersRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD17")
public class AD17 extends AbstractBaseController {

	@Autowired private XusersRepo xusersRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD17"));
		if(!op.isPresent()) return "Category";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String xusername, Model model, HttpServletRequest request) throws ResourceNotFoundException {

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(xusername)) {
				model.addAttribute("xusers", Xusers.getDefaultInstance());
				return "pages/AD17/AD17-fragments::main-form";
			}

			Optional<Xusers> xusersOp = xusersRepo.findById(new XusersPK(sessionManager.getBusinessId(), Integer.parseInt(xusername)));
			if(!xusersOp.isPresent()) {
				model.addAttribute("xusers", Xusers.getDefaultInstance());
				return "pages/AD17/AD17-fragments::main-form";
			}

			model.addAttribute("xusers", xusersOp.get());
			return "pages/AD17/AD17-fragments::main-form";
		}

		if(StringUtils.isNotBlank(xusername) && !"RESET".equalsIgnoreCase(xusername)) {
			Optional<Xusers> xusersOp = xusersRepo.findById(new XusersPK(sessionManager.getBusinessId(), Integer.parseInt(xusername)));
			if(!xusersOp.isPresent()) {
				model.addAttribute("xusers", Xusers.getDefaultInstance());
				return "pages/AD17/AD17";
			}

			model.addAttribute("xusers", xusersOp.get());
			return "pages/AD17/AD17";
		}

		model.addAttribute("xusers", Xusers.getDefaultInstance());
		return "pages/AD17/AD17";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Xusers xusers, BindingResult bindingResult){

		// VALIDATE outlet
		modelValidator.validateXusers(xusers, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(xusers.getSubmitFor())) {
			xusers.setXusername(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "AD17"));
			xusers.setZid(sessionManager.getBusinessId());
			xusers = xusersRepo.save(xusers);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD17?xusername=" + xusers.getXusername()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Xusers> op = xusersRepo.findById(new XusersPK(sessionManager.getBusinessId(), xusers.getXusername()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Xusers existObj = op.get();
		BeanUtils.copyProperties(xusers, existObj, "zid", "createdBy", "createdOn", "xusername");
		existObj = xusersRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD17?xusername=" + xusers.getXusername()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated Successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer xusername){
		Optional<Xusers> op = xusersRepo.findById(new XusersPK(sessionManager.getBusinessId(), xusername));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Xusers obj = op.get();
		xusersRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD17?xusername=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted Successfully");
		return responseHelper.getResponse();
	}
}
