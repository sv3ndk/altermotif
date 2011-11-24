package controllers.validators;

import com.google.common.base.Strings;

import controllers.BeanProvider;
import play.data.validation.Check;

public class ExistingUsernameValidator extends Check {

	@Override
	public boolean isSatisfied(Object validatedObject, Object value) {

		return !Strings.isNullOrEmpty((String) value) && BeanProvider.getUserProfileService().doesUsernameExists((String) value);
	}

}
