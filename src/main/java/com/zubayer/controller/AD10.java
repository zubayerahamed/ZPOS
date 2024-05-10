package com.zubayer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.zubayer.entity.Xscreens;
import com.zubayer.entity.Zbusiness;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.MyUserDetail;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.ZbusinessRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD10")
public class AD10 extends AbstractBaseController {

	@Autowired private ZbusinessRepo businessRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD10"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(Model model, HttpServletRequest request) throws ResourceNotFoundException {
		Optional<Zbusiness> op = businessRepo.findById(sessionManager.getBusinessId());
		if(!op.isPresent()) throw new ResourceNotFoundException();

		Zbusiness zb = op.get();
		if(zb.getXlogo() != null && zb.getXlogo().length > 0) {
			zb.setImageBase64(Base64.getEncoder().encodeToString(zb.getXlogo()));
		}

		model.addAttribute("business", zb);

		if(isAjaxRequest(request)) {
			return "pages/AD10/AD10-fragments::main-form";
		}

		return "pages/AD10/AD10";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Zbusiness zbusiness, @RequestParam(value= "files[]", required = false) MultipartFile[] files, BindingResult bindingResult){

		// Process image first
		boolean imageChanged = false;
		if(files != null && files.length > 0) {
			try {
				zbusiness.setXlogo(files[0].getBytes());
				imageChanged = true;
			} catch (IOException e) {
				responseHelper.setErrorStatusAndMessage("Something wrong in image, please try again with new one");
				return responseHelper.getResponse();
			}
		}

		// VALIDATE XSCREENS
		modelValidator.validateZbusiness(zbusiness, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Update existing
		Optional<Zbusiness> op = businessRepo.findById(sessionManager.getBusinessId());
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Zbusiness existObj = op.get();
		if(imageChanged) {
			BeanUtils.copyProperties(zbusiness, existObj, "zid", "zactive", "createdBy", "createdOn", "businessType");
		} else {
			BeanUtils.copyProperties(zbusiness, existObj, "zid", "zactive", "xlogo", "createdBy", "createdOn", "businessType");
		}
		existObj = businessRepo.save(existObj);

		// Load newly updated zbusiness into session manager object
		MyUserDetail my = sessionManager.getLoggedInUserDetails();
		my.setZbusiness(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD10"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}
}
