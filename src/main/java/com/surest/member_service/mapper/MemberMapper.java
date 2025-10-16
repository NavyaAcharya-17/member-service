package com.surest.member_service.mapper;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberResponse mapToResponse(MemberEntity entity);

    MemberResponse toResponse(MemberEntity entity);

    @Mapping(target = "memberId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MemberEntity toEntity(MemberRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromResponse(MemberResponse dto, @MappingTarget MemberEntity entity);
}
