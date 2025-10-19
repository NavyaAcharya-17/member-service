package com.surest.member_service.mapper;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class MemberMapper {
    public MemberResponse toResponse(MemberEntity member) {
        return MemberResponse.builder()
                .memberId(member.getMemberId())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .dateOfBirth(member.getDateOfBirth())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public MemberEntity toEntity(MemberRequest request) {
        Timestamp now = Timestamp.from(Instant.now());
        return MemberEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .email(request.getEmail())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateEntity(MemberEntity member, MemberRequest request) {
        member.setFirstName(request.getFirstName());
        member.setLastName(request.getLastName());
        member.setDateOfBirth(request.getDateOfBirth());
        member.setEmail(request.getEmail());
        member.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }
}
