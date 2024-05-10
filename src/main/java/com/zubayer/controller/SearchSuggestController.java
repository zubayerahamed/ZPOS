package com.zubayer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zubayer.entity.AddOns;
import com.zubayer.entity.Category;
import com.zubayer.model.DatatableRequestHelper;
import com.zubayer.model.DatatableResponseHelper;
import com.zubayer.service.AddOnsService;
import com.zubayer.service.CategoryService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Controller
@RequestMapping("/search")
public class SearchSuggestController extends AbstractBaseController {

	@Autowired private CategoryService categoryService;
	@Autowired private AddOnsService addonsService;

	@PostMapping("/table/{fragmentcode}/{suffix}")
	public String loadHeaderTableFragment(
			@RequestParam(required = false) String hint, 
			@RequestParam(required = false) String dependentparam,
			@RequestParam(required = false) String resetparam, 
			@PathVariable String fragmentcode, 
			@PathVariable int suffix, 
			String fieldId, 
			boolean mainscreen,
			String mainreloadurl,
			String mainreloadid,
			String extrafieldcontroller,
			String detailreloadurl,
			String detailreloadid,
			String additionalreloadurl,
			String additionalreloadid,
			Model model){

		model.addAttribute("suffix", suffix);
		model.addAttribute("searchValue", hint);
		model.addAttribute("dependentParam", dependentparam);
		model.addAttribute("resetParam", resetparam);
		model.addAttribute("fieldId", fieldId);
		model.addAttribute("mainscreen", mainscreen);
		model.addAttribute("mainreloadurl", mainreloadurl);
		model.addAttribute("mainreloadid", mainreloadid);
		model.addAttribute("extrafieldcontroller", extrafieldcontroller);
		model.addAttribute("detailreloadurl", detailreloadurl);
		model.addAttribute("detailreloadid", detailreloadid);
		model.addAttribute("additionalreloadurl", additionalreloadurl);
		model.addAttribute("additionalreloadid", additionalreloadid);
		return "search-fragments::" + fragmentcode + "-table";
	}

	@GetMapping("/LMD13/{suffix}")
	public @ResponseBody DatatableResponseHelper<Category> LMD13(@PathVariable int suffix, @RequestParam(required = false) String dependentParam) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		DatatableRequestHelper helper = new DatatableRequestHelper(request);

		List<Category> list = categoryService.LMD13(helper.getLength(), helper.getStart(), helper.getColumns().get(helper.getOrderColumnNo()).getName(), helper.getOrderType(), helper.getSearchValue(), suffix, dependentParam);
		int totalRows = categoryService.LMD13(helper.getColumns().get(helper.getOrderColumnNo()).getName(), helper.getOrderType(), helper.getSearchValue(), suffix, dependentParam);

		DatatableResponseHelper<Category> response = new DatatableResponseHelper<>();
		response.setDraw(helper.getDraw());
		response.setRecordsTotal(totalRows);
		response.setRecordsFiltered(totalRows);
		response.setData(list);
		return response;
	}

	@GetMapping("/LMD14/{suffix}")
	public @ResponseBody DatatableResponseHelper<AddOns> LMD14(@PathVariable int suffix, @RequestParam(required = false) String dependentParam) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		DatatableRequestHelper helper = new DatatableRequestHelper(request);

		List<AddOns> list = addonsService.LMD14(helper.getLength(), helper.getStart(), helper.getColumns().get(helper.getOrderColumnNo()).getName(), helper.getOrderType(), helper.getSearchValue(), suffix, dependentParam);
		int totalRows = addonsService.LMD14(helper.getColumns().get(helper.getOrderColumnNo()).getName(), helper.getOrderType(), helper.getSearchValue(), suffix, dependentParam);

		DatatableResponseHelper<AddOns> response = new DatatableResponseHelper<>();
		response.setDraw(helper.getDraw());
		response.setRecordsTotal(totalRows);
		response.setRecordsFiltered(totalRows);
		response.setData(list);
		return response;
	}

	@Override
	protected String pageTitle() {
		return "";
	}

}
