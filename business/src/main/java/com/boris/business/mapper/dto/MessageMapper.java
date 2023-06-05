package com.boris.business.mapper.dto;

import com.boris.business.mapper.config.DtoMapper;
import com.boris.business.mapper.config.MapstructConfig;
import com.boris.business.model.dto.MessageDto;
import com.boris.dao.entity.Message;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfig.class)
public interface MessageMapper extends DtoMapper<MessageDto, Message> {
}
