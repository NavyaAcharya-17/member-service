package com.surest.member_service.service.impl;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import com.surest.member_service.exception.MemberException;
import com.surest.member_service.mapper.MemberMapper;
import com.surest.member_service.repository.MemberRepository;
import com.surest.member_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembers(String firstName, String lastName, Pageable pageable) {
        Specification<MemberEntity> spec = Specification.allOf();
        if (firstName != null && !firstName.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%"));
        }
        if (lastName != null && !lastName.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%"));
        }
        return memberRepository.findAll(spec, pageable)
                .map(MemberEntity::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "members", key = "#memberId")
    public MemberResponse getMemberById(UUID memberId) throws MemberException {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(HttpStatus.NOT_FOUND, "Member not found" + memberId));
        return memberMapper.toResponse(memberEntity);
    }

    @Override
    @Transactional
    public MemberResponse createMember(MemberRequest memberRequest) throws MemberException {
        Optional<MemberEntity> existingMember = memberRepository.findByEmail(memberRequest.getEmail());
        if (existingMember.isPresent()) {
            throw new MemberException(HttpStatus.INTERNAL_SERVER_ERROR, "Email Already Exists" + memberRequest.getEmail());
        }
        MemberEntity memberEntity = memberMapper.toEntity(memberRequest);
        MemberEntity savedEntity = memberRepository.save(memberEntity);
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    @CachePut(value = "members", key = "#memberId")
    public MemberResponse updateMember(UUID memberId, MemberResponse memberResponse) throws MemberException {
        MemberEntity existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(HttpStatus.NOT_FOUND, "Member not found" + memberId));
        memberMapper.updateEntityFromResponse(memberResponse, existingMember);
        MemberEntity savedEntity = memberRepository.save(existingMember);
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "members", key = "#memberId")
    public void deleteMember(UUID memberId) throws MemberException {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberException(HttpStatus.NOT_FOUND, "Member not found" + memberId);
        }
        memberRepository.deleteById(memberId);
    }
}
