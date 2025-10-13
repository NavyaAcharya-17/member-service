package com.surest.member_service.service.impl;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import com.surest.member_service.exception.MemberException;
import com.surest.member_service.mapper.MemberMapper;
import com.surest.member_service.repository.MemberRepository;
import com.surest.member_service.service.MemberService;
import com.surest.member_service.specification.MemberSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.surest.member_service.util.MemberUtil.EMAIL_ALREADY_EXISTS;
import static com.surest.member_service.util.MemberUtil.MEMBER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    @Override
    public Page<MemberResponse> getMembers(String firstName, String lastName, Pageable pageable) {
        Specification<MemberEntity> spec = MemberSpecification.trueSpec();

        if (firstName != null && !firstName.isBlank()) {
            spec = spec.and(MemberSpecification.firstNameContains(firstName));
        }

        if (lastName != null && !lastName.isBlank()) {
            spec = spec.and(MemberSpecification.lastNameContains(lastName));
        }

        return memberRepository.findAll(spec, pageable)
                .map(memberMapper::mapToResponse);
    }

    @Override
    public MemberResponse getMemberById(UUID memberId) throws MemberException {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND + memberId));
        return memberMapper.toResponse(memberEntity);
    }

    @Override
    public MemberResponse createMember(MemberRequest memberRequest) throws MemberException {
        // Check if email already exists
        Optional<MemberEntity> existingMember = memberRepository.findByEmail(memberRequest.getEmail());
        if (existingMember.isPresent()) {
            throw new MemberException(HttpStatus.INTERNAL_SERVER_ERROR, EMAIL_ALREADY_EXISTS + memberRequest.getEmail());
        }
        MemberEntity memberEntity = memberMapper.toEntity(memberRequest);
        MemberEntity savedEntity = memberRepository.save(memberEntity);
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    public MemberResponse updateMember(UUID memberId, MemberResponse memberResponse) throws MemberException {
        MemberEntity existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND + memberId));
        memberMapper.updateEntityFromResponse(memberResponse, existingMember);
        MemberEntity savedEntity = memberRepository.save(existingMember);
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    public void deleteMember(UUID memberId) throws MemberException {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND);
        }
        memberRepository.deleteById(memberId);
    }
}
