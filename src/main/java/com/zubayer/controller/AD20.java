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

import com.zubayer.entity.Currency;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.CurrencyPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.CurrencyRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD20")
public class AD20 extends AbstractBaseController {

	@Autowired private CurrencyRepo currencyRepo;
	

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD20"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String xcode, Model model, HttpServletRequest request) throws ResourceNotFoundException {
		model.addAttribute("currencies", currencyRepo.findAllByZid(sessionManager.getBusinessId()));

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(xcode)) {
				model.addAttribute("currency", Currency.getDefaultInstance());
				return "pages/AD20/AD20-fragments::main-form";
			}

			Optional<Currency> currencyOp = currencyRepo.findById(new CurrencyPK(sessionManager.getBusinessId(), xcode));
			if(!currencyOp.isPresent()) {
				model.addAttribute("currency", Currency.getDefaultInstance());
			}

			model.addAttribute("currency", currencyOp.get());
			return "pages/AD20/AD20-fragments::main-form";
		}

		if(StringUtils.isNotBlank(xcode) && !"RESET".equalsIgnoreCase(xcode)) {
			Optional<Currency> currencyOp = currencyRepo.findById(new CurrencyPK(sessionManager.getBusinessId(), xcode));
			if(!currencyOp.isPresent()) {
				model.addAttribute("currency", Currency.getDefaultInstance());
				return "pages/AD20/AD20";
			}

			model.addAttribute("currency", currencyOp.get());
			return "pages/AD20/AD20";
		}

		model.addAttribute("currency", Currency.getDefaultInstance());
		return "pages/AD20/AD20";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Currency currency, BindingResult bindingResult){

		// VALIDATE outlet
		modelValidator.validateCurrency(currency, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(currency.getSubmitFor())) {
			currency.setZid(sessionManager.getBusinessId());
			currency = currencyRepo.save(currency);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD20?xcode=" + currency.getXcode()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Currency> op = currencyRepo.findById(new CurrencyPK(sessionManager.getBusinessId(), currency.getXcode()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Currency existObj = op.get();
		BeanUtils.copyProperties(currency, existObj, "zid", "xcode", "createdBy", "createdOn");
		existObj = currencyRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD20?xcode=" + currency.getXcode()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam String xcode){
		Optional<Currency> op = currencyRepo.findById(new CurrencyPK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Currency obj = op.get();
		currencyRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD20?xcode=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted Successfully");
		return responseHelper.getResponse();
	}
}
