package com.boris.dao;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootConfiguration
@EntityScan(basePackages = "com.boris.dao.entity")
@EnableJpaRepositories(basePackages = "com.boris.dao.repository")
public class DaoConfig {
}
