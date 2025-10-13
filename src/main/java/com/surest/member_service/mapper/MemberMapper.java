package com.surest.member_service.mapper;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberResponse mapToResponse(MemberEntity entity);

    MemberResponse toResponse(MemberEntity entity);

    MemberEntity toEntity(MemberRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromResponse(MemberResponse dto, @MappingTarget MemberEntity entity);
}
