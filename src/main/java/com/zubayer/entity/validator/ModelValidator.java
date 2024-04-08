package com.zubayer.entity.validator;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import com.zubayer.entity.Outlet;
import com.zubayer.entity.Shop;
import com.zubayer.entity.Xscreens;
import com.zubayer.entity.Zbusiness;
import com.zubayer.entity.pk.XscreensPK;
import com.zubayer.enums.SubmitFor;
import com.zubayer.repository.XscreensRepo;
import com.zubayer.service.ZSessionManager;

import jakarta.validation.Validator;
/**
 * @author Zubayer Ahamed
 * @since Dec 04, 2020
 */
@Component
public class ModelValidator extends ConstraintValidator {

	@Autowired private XscreensRepo xscreensRepo;
	@Autowired private ZSessionManager sessionManager;

	public void validateZbusiness(Zbusiness zbusiness, Errors errors, Validator validator) {
		if(zbusiness == null) return;
		super.validate(zbusiness, errors, validator);
	}

	public void validateOutlet(Outlet outlet, Errors errors, Validator validator) {
		if(outlet == null) return;
		super.validate(outlet, errors, validator);
	}

	public void validateShop(Shop shop, Errors errors, Validator validator) {
		if(shop == null) return;
		super.validate(shop, errors, validator);
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
