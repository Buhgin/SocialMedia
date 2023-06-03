package com.boris.business;

import com.boris.dao.DaoConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan(basePackageClasses = DaoConfig.class)
public class BusinessConfig {
}