package com.zubayer.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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

import com.zubayer.entity.Variation;
import com.zubayer.entity.VariationDetail;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.VariationDetailPK;
import com.zubayer.entity.pk.VariationPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.VariationDetailRepo;
import com.zubayer.repository.VariationRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/MD15")
public class MD15 extends AbstractBaseController {

	@Autowired private VariationRepo vRepo;
	@Autowired private VariationDetailRepo vtRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "MD15"));
		if(!op.isPresent()) return "Category";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String xcode, Model model, HttpServletRequest request) throws ResourceNotFoundException {

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(xcode)) {
				model.addAttribute("variation", Variation.getDefaultInstance());
				return "pages/MD15/MD15-fragments::main-form";
			}

			Optional<Variation> vOp = vRepo.findById(new VariationPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!vOp.isPresent()) {
				model.addAttribute("variation", Variation.getDefaultInstance());
				return "pages/MD15/MD15-fragments::main-form";
			}

			model.addAttribute("variation", vOp.get());
			return "pages/MD15/MD15-fragments::main-form";
		}

		if(StringUtils.isNotBlank(xcode) && !"RESET".equalsIgnoreCase(xcode)) {
			Optional<Variation> vOp = vRepo.findById(new VariationPK(sessionManager.getBusinessId(), Integer.parseInt(xcode)));
			if(!vOp.isPresent()) {
				model.addAttribute("variation", Variation.getDefaultInstance());
				return "pages/MD15/MD15";
			}

			model.addAttribute("variation", vOp.get());
			return "pages/MD15/MD15";
		}

		model.addAttribute("variation", Variation.getDefaultInstance());
		return "pages/MD15/MD15";
	}

	@GetMapping("/detail-table")
	public String detailFormFragment(@RequestParam String xcode, @RequestParam String xrow, Model model) {
		if("RESET".equalsIgnoreCase(xcode) && "RESET".equalsIgnoreCase(xrow)) {
			return "pages/MD15/MD15-fragments::detail-table";
		}

		Optional<Variation> vOp = vRepo.findById(new VariationPK(sessionManager.getBusinessId(), Integer.valueOf(xcode)));
		if(!vOp.isPresent()) return "pages/MD15/MD15-fragments::detail-table";
		model.addAttribute("variation", vOp.get());

		List<VariationDetail> detailsList = vtRepo.findAllByZidAndXcode(sessionManager.getBusinessId(), Integer.valueOf(xcode));
		detailsList.sort(Comparator.comparing(VariationDetail::getXname));
		model.addAttribute("options", detailsList);

		if("RESET".equalsIgnoreCase(xrow)) {
			model.addAttribute("variationdt", VariationDetail.getDefaultInstance(Integer.valueOf(xcode)));
			return "pages/MD15/MD15-fragments::detail-table";
		}

		Optional<VariationDetail> vtOp = vtRepo.findById(new VariationDetailPK(sessionManager.getBusinessId(), Integer.valueOf(xcode), Integer.valueOf(xrow)));
		VariationDetail detail = vtOp.isPresent() ? vtOp.get() : VariationDetail.getDefaultInstance(Integer.valueOf(xcode));
		model.addAttribute("variationdt", detail);
		return "pages/MD15/MD15-fragments::detail-table";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Variation variation, BindingResult bindingResult){

		// VALIDATE outlet
		modelValidator.validateVariation(variation, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(variation.getSubmitFor())) {
			variation.setXcode(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "MD15"));
			variation.setZid(sessionManager.getBusinessId());
			variation = vRepo.save(variation);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/MD15?xcode=" + variation.getXcode()));
			reloadSections.add(new ReloadSection("detail-table-container", "/MD15/detail-table?xcode=" + variation.getXcode() + "&xrow=RESET"));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Variation> op = vRepo.findById(new VariationPK(sessionManager.getBusinessId(), variation.getXcode()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Variation existObj = op.get();
		BeanUtils.copyProperties(variation, existObj, "zid", "createdBy", "createdOn", "xcode");
		existObj = vRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD15?xcode=" + variation.getXcode()));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD15/detail-table?xcode=" + variation.getXcode() + "&xrow=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated Successfully");
		return responseHelper.getResponse();
	}

	@PostMapping("/detail/store")
	public @ResponseBody Map<String, Object> storeDetail(VariationDetail variationDetail, BindingResult bindingResult){

		// VALIDATE 
		Optional<Variation> vOp = vRepo.findById(new VariationPK(sessionManager.getBusinessId(), variationDetail.getXcode()));
		if(!vOp.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Variation not found");
			return responseHelper.getResponse();
		}

		if(variationDetail.getXprice() == null) {
			responseHelper.setErrorStatusAndMessage("Option price required");
			return responseHelper.getResponse();
		}

		if(variationDetail.getXprice().compareTo(BigDecimal.ZERO) == -1) {
			responseHelper.setErrorStatusAndMessage("Invalid price. Price should be greater than or equal to 0");
			return responseHelper.getResponse();
		}

		// Create new
		if(SubmitFor.INSERT.equals(variationDetail.getSubmitFor())) {
			variationDetail.setXrow(vtRepo.getNextAvailableRow(sessionManager.getBusinessId(), variationDetail.getXcode()));
			variationDetail.setZid(sessionManager.getBusinessId());
			variationDetail = vtRepo.save(variationDetail);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/MD15?xcode=" + variationDetail.getXcode()));
			reloadSections.add(new ReloadSection("detail-table-container", "/MD15/detail-table?xcode=" + variationDetail.getXcode() + "&xrow=RESET"));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved Successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<VariationDetail> op = vtRepo.findById(new VariationDetailPK(sessionManager.getBusinessId(), variationDetail.getXcode(), variationDetail.getXrow()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		String[] ignoreProperties = new String[] {
			"zid", 
			"createdBy", 
			"createdOn",
			"xcode", 
		};

		VariationDetail existObj = op.get();
		BeanUtils.copyProperties(variationDetail, existObj, ignoreProperties);
		existObj = vtRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD15?xcode=" + existObj.getXcode()));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD15/detail-table?xcode=" + existObj.getXcode() + "&xrow=" + existObj.getXrow()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated Successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer xcode){
		Optional<Variation> op = vRepo.findById(new VariationPK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		List<VariationDetail> options = vtRepo.findAllByZidAndXcode(sessionManager.getBusinessId(), Integer.valueOf(xcode));
		if(options != null && !options.isEmpty()) {
			vtRepo.deleteAll(options);
		}

		Variation obj = op.get();
		vRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD15?xcode=RESET"));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD15/detail-table?xcode=RESET&xrow=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted Successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping("/detail-table")
	public @ResponseBody Map<String, Object> deleteDetail(@RequestParam Integer xcode, @RequestParam Integer xrow){
		Optional<Variation> op = vRepo.findById(new VariationPK(sessionManager.getBusinessId(), xcode));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Optional<VariationDetail> opdt = vtRepo.findById(new VariationDetailPK(sessionManager.getBusinessId(), xcode, xrow));
		if(!opdt.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		VariationDetail obj = opdt.get();
		vtRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/MD15?xcode=" + xcode));
		reloadSections.add(new ReloadSection("detail-table-container", "/MD15/detail-table?xcode="+xcode+"&xrow=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}
}
