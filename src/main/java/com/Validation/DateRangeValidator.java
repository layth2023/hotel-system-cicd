package com.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

/**
 * Validator for date range validation.
 * Ensures that the start date is before the end date.
 */
public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDateField();
        this.endDateField = constraintAnnotation.endDateField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        if (object == null) {
            return true;
        }

        try {
            Field startField = getField(object.getClass(), startDateField);
            Field endField = getField(object.getClass(), endDateField);

            startField.setAccessible(true);
            endField.setAccessible(true);

            Object startValue = startField.get(object);
            Object endValue = endField.get(object);

            if (startValue == null || endValue == null) {
                return true; // Let @NotNull handle null validation
            }

            if (startValue instanceof LocalDate startDate && endValue instanceof LocalDate endDate) {
                return endDate.isAfter(startDate);
            }

            if (startValue instanceof LocalDateTime startDateTime && endValue instanceof LocalDateTime endDateTime) {
                return endDateTime.isAfter(startDateTime);
            }

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                return getField(clazz.getSuperclass(), fieldName);
            }
            throw e;
        }
    }
}
