
package com.teamactiontracker.backend.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;

class CreateActionCommentRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void validRequestPassesValidation() {
        CreateActionCommentRequest request = createRequest(10L, "Valid comment");

        Set<ConstraintViolation<CreateActionCommentRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void blankCommentFailsValidation() {
        CreateActionCommentRequest request = createRequest(10L, "   ");

        Set<ConstraintViolation<CreateActionCommentRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath().toString().equals("comment")
        ));
    }

    @Test
    void commentExceeding5000CharactersFailsValidation() {
        CreateActionCommentRequest request =
                createRequest(10L, "a".repeat(5001));

        Set<ConstraintViolation<CreateActionCommentRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath().toString().equals("comment")
        ));
    }

    @Test
    void missingActionItemIdFailsValidation() {
        CreateActionCommentRequest request = createRequest(null, "Valid comment");

        Set<ConstraintViolation<CreateActionCommentRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath().toString().equals("actionItemId")
        ));
    }

    @Test
    void zeroActionItemIdFailsValidation() {
        CreateActionCommentRequest request = createRequest(0L, "Valid comment");

        Set<ConstraintViolation<CreateActionCommentRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream().anyMatch(
                violation -> violation.getPropertyPath().toString().equals("actionItemId")
        ));
    }

    @Test
    void commentWithExactly5000CharactersPassesValidation() {
        CreateActionCommentRequest request =
                createRequest(10L, "a".repeat(5000));

        Set<ConstraintViolation<CreateActionCommentRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    private CreateActionCommentRequest createRequest(
            Long actionItemId,
            String comment) {

        CreateActionCommentRequest request =
                new CreateActionCommentRequest();

        request.setActionItemId(actionItemId);
        request.setComment(comment);

        return request;
    }
}
