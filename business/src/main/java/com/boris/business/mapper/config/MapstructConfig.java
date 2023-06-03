package com.boris.business.mapper.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.ReportingPolicy;

@MapperConfig(componentModel = ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MapstructConfig {
}
