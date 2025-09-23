package com.ridematch.driver.Validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Component;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidId.IdExistsValidator.class)
@Documented
public @interface ValidId { //TODO: Move it to shared lib

    String message() default "GENERIC_INVALID_ID_INPUT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<?> entity();

    @Component
    class IdExistsValidator
            implements ConstraintValidator<ValidId, Long> {

        @PersistenceContext
        private EntityManager entityManager;

        private Class<?> entityClass;

        @Override
        public void initialize(ValidId annotation) {
            this.entityClass = annotation.entity();
        }

        @Override
        public boolean isValid(Long id, ConstraintValidatorContext context) {
            if (id == null) {
                return false;
            }
            return entityManager.find(entityClass, id) != null;
        }
    }
}
