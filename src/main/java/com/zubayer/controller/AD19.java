package com.zubayer.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zubayer.entity.Outlet;
import com.zubayer.entity.Shop;
import com.zubayer.entity.Terminal;
import com.zubayer.entity.Xfloor;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.Xtable;
import com.zubayer.entity.Xusers;
import com.zubayer.entity.pk.OutletPK;
import com.zubayer.entity.pk.ShopPK;
import com.zubayer.entity.pk.TerminalPK;
import com.zubayer.entity.pk.XfloorPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.entity.pk.XtablePK;
import com.zubayer.entity.pk.XusersPK;
import com.zubayer.enums.POSRole;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.OutletRepo;
import com.zubayer.repository.ShopRepo;
import com.zubayer.repository.TerminalRepo;
import com.zubayer.repository.XfloorRepo;
import com.zubayer.repository.XtableRepo;
import com.zubayer.repository.XusersRepo;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD19")
public class AD19 extends AbstractBaseController {

	@Autowired private ShopRepo shopRepo;
	@Autowired private OutletRepo outletRepo;
	@Autowired private XfloorRepo xfloorRepo;
	@Autowired private TerminalRepo terminalRepo;
	@Autowired private XtableRepo xtableRepo;
	@Autowired private XusersRepo waiterRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD19"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String id, @RequestParam(required = false) String xoutlet, @RequestParam(required = false) String xshop, @RequestParam(required = false) String xfloor, @RequestParam(required = false) String xterminal, Model model, HttpServletRequest request) throws ResourceNotFoundException {

		List<Xtable> xtables = xtableRepo.findAllByZid(sessionManager.getBusinessId());
		xtables.forEach(t -> {
			Optional<Shop> shopOp = shopRepo.findById(new ShopPK(sessionManager.getBusinessId(), t.getXoutlet(), t.getXshop()));
			if(shopOp.isPresent()) t.setShopName(shopOp.get().getXname());

			Optional<Outlet> outletOp = outletRepo.findById(new OutletPK(sessionManager.getBusinessId(), t.getXoutlet()));
			if(outletOp.isPresent()) t.setOutletName(outletOp.get().getXname());

			Optional<Xfloor> floorOp = xfloorRepo.findById(new XfloorPK(sessionManager.getBusinessId(), t.getXoutlet(), t.getXshop(), t.getXfloor()));
			if(floorOp.isPresent()) t.setFloorName(floorOp.get().getXname());

			Optional<Terminal> terminalOp = terminalRepo.findById(new TerminalPK(sessionManager.getBusinessId(), t.getXoutlet(), t.getXshop(), t.getXterminal()));
			if(terminalOp.isPresent()) t.setTerminalName(terminalOp.get().getXname());

			Optional<Xusers> waiterOp = waiterRepo.findById(new XusersPK(sessionManager.getBusinessId(), t.getXwaiter()));
			if(waiterOp.isPresent()) t.setWaiterName(waiterOp.get().getXdisplayname());
		});

		Map<String, Map<String, List<Xtable>>> tablesMap = xtables.stream().collect(Collectors.groupingBy(Xtable::getOutletName, Collectors.groupingBy(Xtable::getShopName)));

		model.addAttribute("tablesMap", tablesMap);

		List<Outlet> outlets = outletRepo.findAllByZid(sessionManager.getBusinessId());
		outlets = outlets.stream().filter(o -> o.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		outlets.sort(Comparator.comparing(Outlet::getXname));
		model.addAttribute("outlets", outlets);

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(id)) {
				model.addAttribute("xtable", Xtable.getDefaultInstance());
				return "pages/AD19/AD19-fragments::main-form";
			}

			Optional<Xtable> xtableOp = xtableRepo.findById(new XtablePK(sessionManager.getBusinessId(), Integer.valueOf(xoutlet), Integer.valueOf(xshop), Integer.parseInt(xfloor), Integer.parseInt(xterminal), Integer.parseInt(id)));
			if(!xtableOp.isPresent()) {
				model.addAttribute("xtable", Xtable.getDefaultInstance());
			}

			model.addAttribute("xtable", xtableOp.get());

			List<Shop> shops = shopRepo.findAllByZidAndOutletId(sessionManager.getBusinessId(), Integer.parseInt(xoutlet));
			shops = shops.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
			shops.sort(Comparator.comparing(Shop::getXname));
			model.addAttribute("shops", shops);

			List<Xfloor> floors = xfloorRepo.findAllByZidAndXoutletAndXshop(sessionManager.getBusinessId(), Integer.parseInt(xoutlet), Integer.parseInt(xshop));
			floors = floors.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
			floors.sort(Comparator.comparing(Xfloor::getXname));
			model.addAttribute("floors", floors);

			List<Terminal> terminals = terminalRepo.findAllByZidAndOutletIdAndShopId(sessionManager.getBusinessId(), Integer.parseInt(xoutlet), Integer.parseInt(xshop));
			terminals = terminals.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
			terminals.sort(Comparator.comparing(Terminal::getXname));
			model.addAttribute("terminals", terminals);

			List<Xusers> waiters = waiterRepo.findAllByZidAndXoutletAndXshop(sessionManager.getBusinessId(), Integer.parseInt(xoutlet), Integer.parseInt(xshop));
			waiters = waiters.stream().filter(f -> f.getZactive().equals(Boolean.TRUE) && f.getXrole().equals(POSRole.WAITER)).collect(Collectors.toList());
			waiters.sort(Comparator.comparing(Xusers::getXdisplayname));
			model.addAttribute("waiters", waiters);

			return "pages/AD19/AD19-fragments::main-form";
		}

		model.addAttribute("xtable", Xtable.getDefaultInstance());
		return "pages/AD19/AD19";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Xtable xtable, BindingResult bindingResult){

		// VALIDATE Shop
		modelValidator.validateXtable(xtable, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		if(xtable.getXoutlet() == null) {
			responseHelper.setErrorStatusAndMessage("Outlet required");
			return responseHelper.getResponse();
		}

		if(xtable.getXshop() == null) {
			responseHelper.setErrorStatusAndMessage("Shop required");
			return responseHelper.getResponse();
		}

		if(xtable.getXterminal() == null) {
			responseHelper.setErrorStatusAndMessage("Terminal required");
			return responseHelper.getResponse();
		}

		if(xtable.getXfloor() == null) {
			responseHelper.setErrorStatusAndMessage("Floor required");
			return responseHelper.getResponse();
		}

		// Create new
		if(SubmitFor.INSERT.equals(xtable.getSubmitFor())) {
			xtable.setId(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "AD19"));
			xtable.setZid(sessionManager.getBusinessId());
			xtable = xtableRepo.save(xtable);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD19?id=" + xtable.getId() + "&xoutlet=" + xtable.getXoutlet() + "&xshop=" + xtable.getXshop() + "&xfloor=" + xtable.getXfloor() + "&xterminal=" + xtable.getXterminal()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Xtable> op = xtableRepo.findById(new XtablePK(sessionManager.getBusinessId(), xtable.getXoutlet(), xtable.getXshop(), xtable.getXfloor(), xtable.getXterminal(), xtable.getId()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Xtable existObj = op.get();
		BeanUtils.copyProperties(xtable, existObj, "zid", "createdBy", "createdOn", "xoutlet", "xshop", "xfloor", "xterminal");
		existObj = xtableRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD19?id=" + xtable.getId() + "&xoutlet=" + xtable.getXoutlet() + "&xshop=" + xtable.getXshop() + "&xfloor=" + xtable.getXfloor() + "&xterminal=" + xtable.getXterminal()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer id, @RequestParam Integer xoutlet, @RequestParam Integer xshop, @RequestParam Integer xfloor, @RequestParam Integer xterminal){
		Optional<Xtable> op = xtableRepo.findById(new XtablePK(sessionManager.getBusinessId(), xoutlet, xshop, xfloor, xterminal, id));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Xtable obj = op.get();
		xtableRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD19?id=RESET&xoutlet=RESET&xshop=RESET&xfloor=RESET&xterminal=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}

	@GetMapping("/xshop-field")
	public String loadXsaddFieldFragment(@RequestParam Integer xoutlet, @RequestParam(required = false) Integer xtableid,  Model model){
		List<Shop> shops = shopRepo.findAllByZidAndOutletId(sessionManager.getBusinessId(), xoutlet);
		shops = shops.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		shops.sort(Comparator.comparing(Shop::getXname));
		model.addAttribute("shops", shops);

		Xtable xtable = Xtable.getDefaultInstance();
		if(xtableid != 0) {
			xtable.setId(xtableid);
		}
		model.addAttribute("xtable", xtable);

		return "pages/AD19/AD19-fragments::xshop-field";
	}

	@GetMapping("/xfloor-field")
	public String loadXfloorFieldFragment(@RequestParam Integer xshop, @RequestParam Integer xoutlet, @RequestParam(required = false) Integer xtableid,  Model model){
		List<Xfloor> floors = xfloorRepo.findAllByZidAndXoutletAndXshop(sessionManager.getBusinessId(), xoutlet, xshop);
		floors = floors.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		floors.sort(Comparator.comparing(Xfloor::getXname));
		model.addAttribute("floors", floors);

		Xtable xtable = Xtable.getDefaultInstance();
		if(xtableid != 0) {
			xtable.setId(xtableid);
		}
		model.addAttribute("xtable", xtable);

		return "pages/AD19/AD19-fragments::xfloor-field";
	}

	@GetMapping("/xterminal-field")
	public String loadXterminalFieldFragment(@RequestParam Integer xshop, @RequestParam Integer xoutlet, @RequestParam(required = false) Integer xtableid,  Model model){
		List<Terminal> terminals = terminalRepo.findAllByZidAndOutletIdAndShopId(sessionManager.getBusinessId(), xoutlet, xshop);
		terminals = terminals.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		terminals.sort(Comparator.comparing(Terminal::getXname));
		model.addAttribute("terminals", terminals);

		Xtable xtable = Xtable.getDefaultInstance();
		if(xtableid != 0) {
			xtable.setId(xtableid);
		}
		model.addAttribute("xtable", xtable);

		return "pages/AD19/AD19-fragments::xterminal-field";
	}

	@GetMapping("/xwaiter-field")
	public String loadXwaiterFieldFragment(@RequestParam Integer xshop, @RequestParam Integer xoutlet, @RequestParam(required = false) Integer xtableid,  Model model){
		List<Xusers> waiters = waiterRepo.findAllByZidAndXoutletAndXshop(sessionManager.getBusinessId(), xoutlet, xshop);
		waiters = waiters.stream().filter(f -> f.getZactive().equals(Boolean.TRUE) && f.getXrole().equals(POSRole.WAITER)).collect(Collectors.toList());
		waiters.sort(Comparator.comparing(Xusers::getXdisplayname));
		model.addAttribute("waiters", waiters);

		Xtable xtable = Xtable.getDefaultInstance();
		if(xtableid != 0) {
			xtable.setId(xtableid);
		}
		model.addAttribute("xtable", xtable);

		return "pages/AD19/AD19-fragments::xwaiter-field";
	}
}
