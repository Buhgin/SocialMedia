package com.boris.business.mapper.request;

import com.boris.business.mapper.config.EntityMapper;
import com.boris.business.mapper.config.MapstructConfig;
import com.boris.business.model.request.MessageCreateRequest;
import com.boris.business.model.request.RegistrationRequest;
import com.boris.dao.entity.Message;
import com.boris.dao.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapstructConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageCreateMapper extends EntityMapper<Message, MessageCreateRequest> {
    @Override
    Message toEntity(MessageCreateRequest dto);
}
