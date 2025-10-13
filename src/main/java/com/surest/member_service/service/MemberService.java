package com.surest.member_service.service;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.exception.MemberException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MemberService {

    Page<MemberResponse> getMembers(String firstName, String lastName, Pageable pageable);

    MemberResponse getMemberById(UUID memberId) throws MemberException;

    MemberResponse createMember(MemberRequest memberRequest) throws MemberException;

    MemberResponse updateMember(UUID memberId, MemberResponse memberResponse) throws MemberException;

    void deleteMember(UUID memberId) throws MemberException;
}
