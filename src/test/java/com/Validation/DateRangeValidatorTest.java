package com.Validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Date Range Validator Tests")
class DateRangeValidatorTest {

    private DateRangeValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DateRangeValidator();
    }

    @Nested
    @DisplayName("LocalDate Validation Tests")
    class LocalDateTests {

        @BeforeEach
        void setUpForLocalDate() {
            ValidDateRange annotation = TestDateObject.class.getAnnotation(ValidDateRange.class);
            validator.initialize(annotation);
        }

        @Test
        @DisplayName("Should accept valid date range")
        void shouldAcceptValidDateRange() {
            TestDateObject obj = new TestDateObject();
            obj.setStartDate(LocalDate.of(2025, 6, 1));
            obj.setEndDate(LocalDate.of(2025, 6, 5));

            assertTrue(validator.isValid(obj, context));
        }

        @Test
        @DisplayName("Should reject end date before start date")
        void shouldRejectEndDateBeforeStartDate() {
            TestDateObject obj = new TestDateObject();
            obj.setStartDate(LocalDate.of(2025, 6, 5));
            obj.setEndDate(LocalDate.of(2025, 6, 1));

            assertFalse(validator.isValid(obj, context));
        }

        @Test
        @DisplayName("Should reject same start and end date")
        void shouldRejectSameDates() {
            TestDateObject obj = new TestDateObject();
            obj.setStartDate(LocalDate.of(2025, 6, 1));
            obj.setEndDate(LocalDate.of(2025, 6, 1));

            assertFalse(validator.isValid(obj, context));
        }

        @Test
        @DisplayName("Should accept null object")
        void shouldAcceptNullObject() {
            assertTrue(validator.isValid(null, context));
        }

        @Test
        @DisplayName("Should accept null start date")
        void shouldAcceptNullStartDate() {
            TestDateObject obj = new TestDateObject();
            obj.setStartDate(null);
            obj.setEndDate(LocalDate.of(2025, 6, 5));

            assertTrue(validator.isValid(obj, context));
        }

        @Test
        @DisplayName("Should accept null end date")
        void shouldAcceptNullEndDate() {
            TestDateObject obj = new TestDateObject();
            obj.setStartDate(LocalDate.of(2025, 6, 1));
            obj.setEndDate(null);

            assertTrue(validator.isValid(obj, context));
        }

        @Test
        @DisplayName("Should accept one day difference")
        void shouldAcceptOneDayDifference() {
            TestDateObject obj = new TestDateObject();
            obj.setStartDate(LocalDate.of(2025, 6, 1));
            obj.setEndDate(LocalDate.of(2025, 6, 2));

            assertTrue(validator.isValid(obj, context));
        }
    }

    @Nested
    @DisplayName("LocalDateTime Validation Tests")
    class LocalDateTimeTests {

        @BeforeEach
        void setUpForLocalDateTime() {
            ValidDateRange annotation = TestDateTimeObject.class.getAnnotation(ValidDateRange.class);
            validator.initialize(annotation);
        }

        @Test
        @DisplayName("Should accept valid datetime range")
        void shouldAcceptValidDateTimeRange() {
            TestDateTimeObject obj = new TestDateTimeObject();
            obj.setStartDateTime(LocalDateTime.of(2025, 6, 1, 10, 0));
            obj.setEndDateTime(LocalDateTime.of(2025, 6, 1, 14, 0));

            assertTrue(validator.isValid(obj, context));
        }

        @Test
        @DisplayName("Should reject end datetime before start datetime")
        void shouldRejectEndDateTimeBeforeStartDateTime() {
            TestDateTimeObject obj = new TestDateTimeObject();
            obj.setStartDateTime(LocalDateTime.of(2025, 6, 1, 14, 0));
            obj.setEndDateTime(LocalDateTime.of(2025, 6, 1, 10, 0));

            assertFalse(validator.isValid(obj, context));
        }

        @Test
        @DisplayName("Should reject same datetime")
        void shouldRejectSameDateTime() {
            TestDateTimeObject obj = new TestDateTimeObject();
            LocalDateTime sameTime = LocalDateTime.of(2025, 6, 1, 10, 0);
            obj.setStartDateTime(sameTime);
            obj.setEndDateTime(sameTime);

            assertFalse(validator.isValid(obj, context));
        }

        @Test
        @DisplayName("Should accept one minute difference")
        void shouldAcceptOneMinuteDifference() {
            TestDateTimeObject obj = new TestDateTimeObject();
            obj.setStartDateTime(LocalDateTime.of(2025, 6, 1, 10, 0));
            obj.setEndDateTime(LocalDateTime.of(2025, 6, 1, 10, 1));

            assertTrue(validator.isValid(obj, context));
        }
    }

    @ValidDateRange(startDateField = "startDate", endDateField = "endDate")
    private static class TestDateObject {
        private LocalDate startDate;
        private LocalDate endDate;

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }
    }

    @ValidDateRange(startDateField = "startDateTime", endDateField = "endDateTime")
    private static class TestDateTimeObject {
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;

        public LocalDateTime getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(LocalDateTime startDateTime) {
            this.startDateTime = startDateTime;
        }

        public LocalDateTime getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(LocalDateTime endDateTime) {
            this.endDateTime = endDateTime;
        }
    }
}
