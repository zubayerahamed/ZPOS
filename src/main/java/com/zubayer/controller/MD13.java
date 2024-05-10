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
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.CategoryPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.CategoryRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/MD13")
public class MD13 extends AbstractBaseController {

	@Autowired private CategoryRepo categoryRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "MD13"));
		if(!op.isPresent()) return "Category";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String xcode, Model model, HttpServletRequest request) throws ResourceNotFoundException {
		model.addAttribute("categories", categoryRepo.findAllByZid(sessionManager.getBusinessId()));

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(xcode)) {
				model.addAttribute("category", Category.getDefaultInstance());
				return "pages/MD13/MD13-fragments::main-form";
			}

			Optional<Category> categoryOp = categoryRepo.findById(new CategoryPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!categoryOp.isPresent()) {
				model.addAttribute("category", Category.getDefaultInstance());
				return "pages/MD13/MD13-fragments::main-form";
			}

			model.addAttribute("category", categoryOp.get());
			model.addAttribute("categories", categoryRepo.findAllElegibleParentCategories(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			model.addAttribute("childs", categoryRepo.findAllChildCategories(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			return "pages/MD13/MD13-fragments::main-form";
		}

		if(StringUtils.isNotBlank(xcode) && !"RESET".equalsIgnoreCase(xcode)) {
			Optional<Category> categoryOp = categoryRepo.findById(new CategoryPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!categoryOp.isPresent()) {
				model.addAttribute("category", Category.getDefaultInstance());
				return "pages/MD13/MD13";
			}

			model.addAttribute("category", categoryOp.get());
			model.addAttribute("categories", categoryRepo.findAllElegibleParentCategories(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			model.addAttribute("childs", categoryRepo.findAllChildCategories(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			return "pages/MD13/MD13";
		}

		model.addAttribute("category", Category.getDefaultInstance());
		return "pages/MD13/MD13";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Category category, BindingResult bindingResult){

		// VALIDATE outlet
		modelValidator.validateCategory(category, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(category.getSubmitFor())) {
			category.setXcode(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "MD13"));
			category.setZid(sessionManager.getBusinessId());
			if(category.getXseqn() == null) category.setXseqn(0);
			category = categoryRepo.save(category);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/MD13?xcode=" + category.getXcode()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Category> op = categoryRepo.findById(new CategoryPK(sessionManager.getBusinessId(), category.getXcode()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Category existObj = op.get();
		BeanUtils.copyProperties(category, existObj, "zid", "createdBy", "createdOn", "xcode");
		existObj = categoryRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD13?xcode=" + category.getXcode()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated Successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer xcode){
		Optional<Category> op = categoryRepo.findById(new CategoryPK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Category obj = op.get();
		categoryRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD13?xcode=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted Successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping("/removechilds")
	public @ResponseBody Map<String, Object> removeChilds(@RequestParam Integer xcode){
		Optional<Category> op = categoryRepo.findById(new CategoryPK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do remove childs");
			return responseHelper.getResponse();
		}

		Integer count = categoryRepo.removeAllChilds(sessionManager.getBusinessId(), xcode);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD13?xcode=" + xcode));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage(count + " Child Categories Removed Successfully");
		return responseHelper.getResponse();
	}
}
