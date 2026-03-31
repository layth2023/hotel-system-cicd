package com.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class to enable JPA Auditing for automatic
 * timestamp management (createdAt, updatedAt fields).
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
