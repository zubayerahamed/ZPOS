package com.zubayer.entity.validator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zubayer.entity.AddOns;
import com.zubayer.entity.Category;
import com.zubayer.entity.Outlet;
import com.zubayer.entity.Shop;
import com.zubayer.entity.Terminal;
import com.zubayer.entity.Variation;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.Zbusiness;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.repository.AddOnsRepo;
import com.zubayer.repository.CategoryRepo;
import com.zubayer.repository.VariationRepo;
import com.zubayer.repository.XscreensRepo;
import com.zubayer.service.ZSessionManager;

import jakarta.validation.Validator;

/**
 * @author Zubayer Ahamed
 * @since May 10, 2024
 * CSE202401068
 */
@Component
public class ModelValidator extends ConstraintValidator {

	@Autowired private XscreensRepo xscreensRepo;
	@Autowired private ZSessionManager sessionManager;
	@Autowired private CategoryRepo categoryRepo;
	@Autowired private AddOnsRepo addOnsRepo;
	@Autowired private VariationRepo vRepo;

	public void validateZbusiness(Zbusiness zbusiness, Errors errors, Validator validator) {
		if(zbusiness == null) return;
		super.validate(zbusiness, errors, validator);
	}

	public void validateVariation(Variation variation, Errors errors, Validator validator) {
		if(variation == null) return;
		super.validate(variation, errors, validator);
		if (errors.hasErrors()) return;

		Optional<Variation> op = vRepo.findByZidAndXname(sessionManager.getBusinessId(), variation.getXname());
		if(!op.isPresent()) return;

		if(SubmitFor.INSERT.equals(variation.getSubmitFor()) && op.isPresent()) {
			errors.rejectValue("xname", "Variation Name Already Exist");
			return;
		}

		if(SubmitFor.UPDATE.equals(variation.getSubmitFor()) && op.isPresent() && !variation.getXcode().equals(op.get().getXcode())) {
			errors.rejectValue("xname", "Variation Name Already Exist");
			return;
		}
	}

	public void validateAddOns(AddOns addons, Errors errors, Validator validator) {
		if(addons == null) return;
		super.validate(addons, errors, validator);
		if (errors.hasErrors()) return;

		Optional<AddOns> op = addOnsRepo.findByZidAndXname(sessionManager.getBusinessId(), addons.getXname());
		if(!op.isPresent()) return;

		if(SubmitFor.INSERT.equals(addons.getSubmitFor()) && op.isPresent()) {
			errors.rejectValue("xname", "Addon Name Already Exist");
			return;
		}

		if(SubmitFor.UPDATE.equals(addons.getSubmitFor()) && op.isPresent() && !addons.getXcode().equals(op.get().getXcode())) {
			errors.rejectValue("xname", "Addons Name Already Exist");
			return;
		}
	}

	public void validateCategory(Category category, Errors errors, Validator validator) {
		if(category == null) return;
		super.validate(category, errors, validator);
		if (errors.hasErrors()) return;

		Optional<Category> op = categoryRepo.findByZidAndXname(sessionManager.getBusinessId(), category.getXname());
		if(!op.isPresent()) return;

		if(SubmitFor.INSERT.equals(category.getSubmitFor()) && op.isPresent()) {
			errors.rejectValue("xname", "Category Name Already Exist");
			return;
		}

		if(SubmitFor.UPDATE.equals(category.getSubmitFor()) && op.isPresent() && !category.getXcode().equals(op.get().getXcode())) {
			errors.rejectValue("xname", "Category Name Already Exist");
			return;
		}
	}

	public void validateOutlet(Outlet outlet, Errors errors, Validator validator) {
		if(outlet == null) return;
		super.validate(outlet, errors, validator);
	}

	public void validateShop(Shop shop, Errors errors, Validator validator) {
		if(shop == null) return;
		super.validate(shop, errors, validator);
	}

	public void validateTerminal(Terminal terminal, Errors errors, Validator validator) {
		if(terminal == null) return;
		super.validate(terminal, errors, validator);
	}

	public void validateXscreens(Xscreens xscreens, Errors errors, Validator validator) {
		if(xscreens == null) return;

		super.validate(xscreens, errors, validator);
		if (errors.hasErrors()) return;

		// check xscreen already exist
		Optional<Xscreens> op = xscreensRepo.findById(new XscreensPK(sessionManager.getBusinessId(), xscreens.getXscreen()));
		if(!op.isPresent()) return;

		if(SubmitFor.INSERT.equals(xscreens.getSubmitFor()) && op.isPresent()) {
			errors.rejectValue("xscreen", "Screen already exist in the system");
		}
	}
}
