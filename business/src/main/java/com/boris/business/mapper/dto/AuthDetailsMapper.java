package com.boris.business.mapper.dto;

import com.boris.business.mapper.config.DtoMapper;
import com.boris.business.mapper.config.MapstructConfig;
import com.boris.business.model.dto.AuthDetailsDto;
import com.boris.dao.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface AuthDetailsMapper extends DtoMapper<AuthDetailsDto, User> {
}
