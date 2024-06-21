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
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.pk.OutletPK;
import com.zubayer.entity.pk.ShopPK;
import com.zubayer.entity.pk.TerminalPK;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.exceptions.ResourceNotFoundException;
import com.zubayer.model.ReloadSection;
import com.zubayer.repository.OutletRepo;
import com.zubayer.repository.ShopRepo;
import com.zubayer.repository.TerminalRepo;
import com.zubayer.util.POSUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since Apr 8, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/AD13")
public class AD13 extends AbstractBaseController {

	@Autowired private ShopRepo shopRepo;
	@Autowired private OutletRepo outletRepo;
	@Autowired private TerminalRepo terminalRepo;

	@Override
	protected String pageTitle() {
		if(this.pageTitle != null) return this.pageTitle;
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), "AD13"));
		if(!op.isPresent()) return "";
		this.pageTitle = op.get().getXtitle();
		return this.pageTitle;
	}

	@GetMapping
	public String index(@RequestParam(required = false) String id, @RequestParam(required = false) String outletid, @RequestParam(required = false) String shopid, Model model, HttpServletRequest request) throws ResourceNotFoundException {

		List<Terminal> terminals = terminalRepo.findAllByZid(sessionManager.getBusinessId());
		terminals.forEach(t -> {
			Optional<Shop> shopOp = shopRepo.findById(new ShopPK(sessionManager.getBusinessId(), t.getOutletId(), t.getShopId()));
			if(shopOp.isPresent()) t.setShopName(shopOp.get().getXname());

			Optional<Outlet> outletOp = outletRepo.findById(new OutletPK(sessionManager.getBusinessId(), t.getOutletId()));
			if(outletOp.isPresent()) t.setOutletName(outletOp.get().getXname());
		});

		Map<String, Map<String, List<Terminal>>> terminalMap = terminals.stream()
																		.collect(Collectors.groupingBy(Terminal::getOutletName,
																		Collectors.groupingBy(Terminal::getShopName)));

		model.addAttribute("terminalsMap", terminalMap);

		List<Outlet> outlets = outletRepo.findAllByZid(sessionManager.getBusinessId());
		outlets = outlets.stream().filter(o -> o.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		outlets.sort(Comparator.comparing(Outlet::getXname));
		model.addAttribute("outlets", outlets);

		if(isAjaxRequest(request)) {
			if("RESET".equalsIgnoreCase(id)) {
				model.addAttribute("terminal", Terminal.getDefaultInstance());
				return "pages/AD13/AD13-fragments::main-form";
			}

			Optional<Terminal> terminalOp = terminalRepo.findById(new TerminalPK(sessionManager.getBusinessId(), Integer.valueOf(outletid), Integer.valueOf(shopid), Integer.parseInt(id)));
			if(!terminalOp.isPresent()) {
				model.addAttribute("terminal", Terminal.getDefaultInstance());
			}

			// Offline POS key
			String poskey = POSUtil.generatePOSKey(sessionManager.getBusinessId(), terminalOp.get().getOutletId(), terminalOp.get().getShopId(), terminalOp.get().getId());
			model.addAttribute("poskey", poskey);
			model.addAttribute("terminal", terminalOp.get());

			List<Shop> shops = shopRepo.findAllByZidAndOutletId(sessionManager.getBusinessId(), Integer.parseInt(outletid));
			shops = shops.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
			shops.sort(Comparator.comparing(Shop::getXname));
			model.addAttribute("shops", shops);

			return "pages/AD13/AD13-fragments::main-form";
		}

		model.addAttribute("terminal", Terminal.getDefaultInstance());
		return "pages/AD13/AD13";
	}

	@PostMapping("/store")
	public @ResponseBody Map<String, Object> store(Terminal terminal, BindingResult bindingResult){

		// VALIDATE Shop
		modelValidator.validateTerminal(terminal, bindingResult, validator);
		if(bindingResult.hasErrors()) return modelValidator.getValidationMessage(bindingResult);

		if(terminal.getOutletId() == null) {
			responseHelper.setErrorStatusAndMessage("Outlet required");
			return responseHelper.getResponse();
		}

		if(terminal.getShopId() == null) {
			responseHelper.setErrorStatusAndMessage("Shop required");
			return responseHelper.getResponse();
		}

		// Create new
		if(SubmitFor.INSERT.equals(terminal.getSubmitFor())) {
			terminal.setId(xscreensRepo.Fn_getTrn(sessionManager.getBusinessId(), "AD13"));
			terminal.setZid(sessionManager.getBusinessId());
			terminal = terminalRepo.save(terminal);

			List<ReloadSection> reloadSections = new ArrayList<>();
			reloadSections.add(new ReloadSection("main-form-container", "/AD13?id=" + terminal.getId() + "&outletid=" + terminal.getOutletId() + "&shopid=" + terminal.getShopId()));
			responseHelper.setReloadSections(reloadSections);
			responseHelper.setSuccessStatusAndMessage("Saved successfully");
			return responseHelper.getResponse();
		}

		// Update existing
		Optional<Terminal> op = terminalRepo.findById(new TerminalPK(sessionManager.getBusinessId(), terminal.getOutletId(), terminal.getShopId(), terminal.getId()));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do update");
			return responseHelper.getResponse();
		}

		Terminal existObj = op.get();
		BeanUtils.copyProperties(terminal, existObj, "zid", "createdBy", "createdOn", "outletId", "shopId", "xdevice");
		existObj = terminalRepo.save(existObj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD13?id=" + terminal.getId() + "&outletid=" + terminal.getOutletId() + "&shopid=" + terminal.getShopId()));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Updated successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping
	public @ResponseBody Map<String, Object> delete(@RequestParam Integer id, @RequestParam Integer outletid, @RequestParam Integer shopid){
		Optional<Terminal> op = terminalRepo.findById(new TerminalPK(sessionManager.getBusinessId(), outletid, shopid, id));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Terminal obj = op.get();
		terminalRepo.delete(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD13?id=RESET&outletid=RESET&shopid=RESET"));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Deleted successfully");
		return responseHelper.getResponse();
	}

	@DeleteMapping("/revoke-device")
	public @ResponseBody Map<String, Object> revokeDevice(@RequestParam Integer id, @RequestParam Integer outletid, @RequestParam Integer shopid){
		Optional<Terminal> op = terminalRepo.findById(new TerminalPK(sessionManager.getBusinessId(), outletid, shopid, id));
		if(!op.isPresent()) {
			responseHelper.setErrorStatusAndMessage("Data not found in this system to do delete");
			return responseHelper.getResponse();
		}

		Terminal obj = op.get();
		obj.setXdevice(null);
		terminalRepo.save(obj);

		List<ReloadSection> reloadSections = new ArrayList<>();
		reloadSections.add(new ReloadSection("main-form-container", "/AD13?id=" + id + "&outletid=" + outletid + "&shopid=" + shopid));
		responseHelper.setReloadSections(reloadSections);
		responseHelper.setSuccessStatusAndMessage("Device Revoked Successfully");
		return responseHelper.getResponse();
	}

	@GetMapping("/shopid-field")
	public String loadXsaddFieldFragment(@RequestParam Integer outletid, @RequestParam(required = false) Integer terminalid,  Model model){
		List<Shop> shops = shopRepo.findAllByZidAndOutletId(sessionManager.getBusinessId(), outletid);
		shops = shops.stream().filter(f -> f.getZactive().equals(Boolean.TRUE)).collect(Collectors.toList());
		shops.sort(Comparator.comparing(Shop::getXname));
		model.addAttribute("shops", shops);

		Terminal terminal = Terminal.getDefaultInstance();
		if(terminalid != 0) {
			terminal.setId(terminalid);
		}
		model.addAttribute("terminal", terminal);

		return "pages/AD13/AD13-fragments::shopid-field";
	}
}
