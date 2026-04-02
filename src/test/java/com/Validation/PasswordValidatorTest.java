package com.Validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Password Validator Tests")
class PasswordValidatorTest {

    private PasswordValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
        context = mock(ConstraintValidatorContext.class);

        ConstraintValidatorContext.ConstraintViolationBuilder builder =
                mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    @DisplayName("Should accept valid password with all requirements")
    void shouldAcceptValidPassword() {
        assertTrue(validator.isValid("Password1!", context));
    }

    @Test
    @DisplayName("Should accept valid complex password")
    void shouldAcceptComplexPassword() {
        assertTrue(validator.isValid("MyStr0ng@Pass!", context));
    }

    @Test
    @DisplayName("Should reject null password")
    void shouldRejectNullPassword() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    @DisplayName("Should reject empty password")
    void shouldRejectEmptyPassword() {
        assertFalse(validator.isValid("", context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"short1!", "Pass1!", "Abc1@"})
    @DisplayName("Should reject password shorter than 8 characters")
    void shouldRejectShortPassword(String password) {
        assertFalse(validator.isValid(password, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"password1!", "lowercase1@", "nouppercase1#"})
    @DisplayName("Should reject password without uppercase")
    void shouldRejectWithoutUppercase(String password) {
        assertFalse(validator.isValid(password, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PASSWORD1!", "UPPERCASE1@", "NOLOWERCASE1#"})
    @DisplayName("Should reject password without lowercase")
    void shouldRejectWithoutLowercase(String password) {
        assertFalse(validator.isValid(password, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PasswordNoDigit!", "NoNumbers@Test", "AllLetters#Only"})
    @DisplayName("Should reject password without digit")
    void shouldRejectWithoutDigit(String password) {
        assertFalse(validator.isValid(password, context));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Password123", "NoSpecial1", "MissingChar99"})
    @DisplayName("Should reject password without special character")
    void shouldRejectWithoutSpecialChar(String password) {
        assertFalse(validator.isValid(password, context));
    }

    @Test
    @DisplayName("Should accept password with various special characters")
    void shouldAcceptVariousSpecialChars() {
        assertTrue(validator.isValid("Password1@", context));
        assertTrue(validator.isValid("Password1#", context));
        assertTrue(validator.isValid("Password1$", context));
        assertTrue(validator.isValid("Password1%", context));
        assertTrue(validator.isValid("Password1^", context));
        assertTrue(validator.isValid("Password1&", context));
        assertTrue(validator.isValid("Password1*", context));
    }

    @Test
    @DisplayName("Should accept minimum valid password")
    void shouldAcceptMinimumValidPassword() {
        assertTrue(validator.isValid("Abcdef1!", context));
    }

    @Test
    @DisplayName("Should accept long valid password")
    void shouldAcceptLongPassword() {
        String longPassword = "VeryL0ngP@ssw0rdThatIsValid123!";
        assertTrue(validator.isValid(longPassword, context));
    }
}
