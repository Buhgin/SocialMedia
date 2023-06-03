package com.boris.business.mapper.dto;

import com.boris.business.mapper.config.DtoMapper;
import com.boris.business.mapper.config.MapstructConfig;
import com.boris.business.model.dto.ActivityDto;
import com.boris.dao.entity.Activity;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface ActivityMapper extends DtoMapper<ActivityDto, Activity> {

}
