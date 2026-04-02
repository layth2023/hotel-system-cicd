package com;

import org.springframework.boot.test.context.TestConfiguration;

/**
 * Test configuration for shared test setup.
 * DataInitializer is disabled via @Profile("!test") so no mock is needed.
 */
@TestConfiguration
public class TestConfig {
    // Additional test beans can be defined here if needed
}
