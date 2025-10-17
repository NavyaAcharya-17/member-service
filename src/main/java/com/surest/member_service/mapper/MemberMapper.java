package com.surest.member_service.mapper;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import org.mapstruct.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberResponse mapToResponse(MemberEntity entity);

    MemberResponse toResponse(MemberEntity entity);

    MemberEntity toEntity(MemberRequest request);

    @AfterMapping
    default void setAuditFields(@MappingTarget MemberEntity entity) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        }
        entity.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    void updateEntityFromResponse(MemberResponse dto, @MappingTarget MemberEntity entity);
}
