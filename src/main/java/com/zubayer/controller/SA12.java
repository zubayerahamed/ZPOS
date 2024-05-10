package com.zubayer.controller;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.model.DatatableRequestHelper;
import com.zubayer.model.DatatableResponseHelper;
import com.zubayer.model.ReloadSection;
import com.zubayer.service.XscreensService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Mar 25, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/SA12")
public class SA12 extends AbstractBaseController {

	@Autowired private XscreensService xscreensService;

	private String pageTitle = null;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "SA12"));
		if(!op.isPresent()) return null;
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(Model model) {
		model.addAttribute("xscreens", Xscreens.getDefaultInstance());

		List<Xscreens> menus = xscreensRepo.findAllByZidAndXtypeIn(sessionManager.getBusinessId(), Arrays.asList("Screen", "Module"));
		menus.sort(Comparator.comparing(Xscreens::getXsequence));
		model.addAttribute("pxscreens", menus);

		return "pages/SA12/SA12";
	}

	@GetMapping("/{xscreen}")
	public String loadFormFragment(@PathVariable String xscreen, Model model) {
		if("RESET".equalsIgnoreCase(xscreen)) {
			model.addAttribute("xscreens", Xscreens.getDefaultInstance());

			List<Xscreens> menus = xscreensRepo.findAllByZidAndXtypeIn(sessionManager.getBusinessId(), Arrays.asList("Screen", "Module"));
			menus.sort(Comparator.comparing(Xscreens::getXsequence));
			model.addAttribute("pxscreens", menus);

			return "pages/SA12/SA12-fragments::main-form";
		}

		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), xscreen));
		model.addAttribute("xscreens", op.isPresent() ? op.get() : Xscreens.getDefaultInstance());

		List<Xscreens> menus = xscreensRepo.findAllByZidAndXtypeIn(sessionManager.getBusinessId(), Arrays.asList("Screen", "Module"));
		// remove self
		menus = menus.stream().filter(f -> !f.getXscreen().equals(xscreen)).collect(Collectors.toList());
		// Get all child's xscreen list
		List<String> childs = new ArrayList<>();
		getAllChildrensMenu(xscreen, menus, childs);
		// remove all child's
		menus = menus.stream().filter(f -> !childs.contains(f.getXscreen())).collect(Collectors.toList());
		menus.sort(Comparator.comparing(Xscreens::getXsequence));
		model.addAttribute("pxscreens", menus);

		return "pages/SA12/SA12-fragments::main-form";
	}

	private void getAllChildrensMenu(String parentMenu, List<Xscreens> menus, List<String> childs) {
		for(Xscreens screen : menus) {
			if(screen.getPxscreen() != null && screen.getPxscreen().equals(parentMenu)) {
				childs.add(screen.getXscreen());
				getAllChildrensMenu(screen.getXscreen(), menus, childs);
			}
		}
	}

	@GetMapping("/header-table")
	public String loadHeaderTableFragment(Model model) {
		return "pages/SA12/SA12-fragments::header-table";
	}

	@GetMapping("/all")
	public @ResponseBody DatatableResponseHelper<Xscreens> getAll(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		DatatableRequestHelper helper = new DatatableRequestHelper(request);

		List<Xscreens> students = xscreensService.getAll(helper.getLength(), helper.getStart(), helper.getColumns().get(helper.getOrderColumnNo()).getName(), helper.getOrderType(), helper.getSearchValue());
		int totalRows = xscreensService.countAll(helper.getColumns().get(helper.getOrderColumnNo()).getName(), helper.getOrderType(), helper.getSearchValue());

		DatatableResponseHelper<Xscreens> response = new DatatableResponseHelper<Xscreens>();
		response.setDraw(helper.getDraw());
		response.setRecordsTotal(totalRows);
		response.setRecordsFiltered(totalRows);
		response.setData(students);
		return response;
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Xscreens xscreens, BindingResult bindingResult){

		// VALIDATE XSCREENS
		modelValidator.validateXscreens(xscreens, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		// Create new
		if(SubmitFor.INSERT.equals(xscreens.getSubmitFor())) {
			if("System".equals(xscreens.getXtype())) xscreens.setPxscreen(null);
			xscreens.setZid(sessionManager.getBusinessId());
			xscreens = xscreensRepo.save(xscreens);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/SA12/" + xscreens.getXscreen()));
			reloadSections.add(new ReloadSection("header-table-container", "/SA12/header-table"));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), xscreens.getXscreen()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Xscreens existObj = op.get();
		BeanUtils.copyProperties(xscreens, existObj, "zid", "zuserid", "ztime", "xscreen");
		if("System".equals(existObj.getXtype())) existObj.setPxscreen(null);
		existObj = xscreensRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/SA12/" + existObj.getXscreen()));
		reloadSections.add(new ReloadSection("header-table-container", "/SA12/header-table"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping("/{xscreen}")
	public @ResponseBody Map<String, Object> delete(@PathVariable String xscreen){
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), xscreen));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Xscreens obj = op.get();
		xscreensRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/SA12/RESET"));
		reloadSections.add(new ReloadSection("header-table-container", "/SA12/header-table"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}
	
}
