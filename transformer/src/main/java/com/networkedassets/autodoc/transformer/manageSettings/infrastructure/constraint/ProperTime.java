package com.networkedassets.autodoc.transformer.manageSettings.infrastructure.constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import com.networkedassets.autodoc.transformer.settings.Branch;
import com.networkedassets.autodoc.transformer.settings.ScheduledEvent;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProperTime.Validator.class)
public @interface ProperTime {

	Class<?>[]groups() default {};

	Class<? extends Payload>[]payload() default {};

	String message() default "{com.networkedassets.autodoc.transformer.manageSettings.infrastructure.constraint.ProperDateTime.message}";

	public class Validator implements ConstraintValidator<ProperTime, Branch> {
		private static final String TIME_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

		@Override
		public void initialize(ProperTime time) {

		}

		@Override
		public boolean isValid(final Branch branch, final ConstraintValidatorContext constraintValidatorContext) {
			return branch.getScheduledEvents().isEmpty() ? true
					: branch.getScheduledEvents().stream().allMatch(s -> validateScheduledEvents(s));
		}

		private boolean validateScheduledEvents(ScheduledEvent scheduledEvent) {

			return validateTime(scheduledEvent.getTime());

		}

		private boolean validateTime(final String time) {

			Pattern pattern = Pattern.compile(TIME_PATTERN);
			Matcher matcher = pattern.matcher(time);
			return matcher.matches();

		}

	}
}
