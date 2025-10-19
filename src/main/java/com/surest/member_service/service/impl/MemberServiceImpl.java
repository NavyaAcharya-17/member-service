package com.surest.member_service.service.impl;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import com.surest.member_service.exception.MemberNotFoundException;
import com.surest.member_service.exception.ResourceAlreadyExistsException;
import com.surest.member_service.mapper.MemberMapper;
import com.surest.member_service.repository.MemberRepository;
import com.surest.member_service.service.MemberService;
import com.surest.member_service.specification.MemberSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembers(String firstName, String lastName, Pageable pageable) {
        return memberRepository.findAll(
                MemberSpecification.filterBy(firstName, lastName),
                pageable
        ).map(MemberEntity::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "members", key = "#memberId")
    public MemberResponse getMemberById(UUID memberId) throws MemberNotFoundException {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
        return memberMapper.toResponse(memberEntity);
    }

    @Override
    @Transactional
    public MemberResponse createMember(MemberRequest memberRequest) throws MemberNotFoundException {
        memberRepository.findByEmail(memberRequest.getEmail())
                .ifPresent(existingMember -> {
                    throw new ResourceAlreadyExistsException();
                });
        MemberEntity memberEntity = memberMapper.toEntity(memberRequest);
        MemberEntity savedEntity = memberRepository.save(memberEntity);
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    @CachePut(value = "members", key = "#memberId")
    public MemberResponse updateMember(UUID memberId, MemberRequest memberRequest) {
        MemberEntity existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
        memberMapper.updateEntity(existingMember, memberRequest);
        MemberEntity savedEntity = memberRepository.save(existingMember);
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "members", key = "#memberId")
    public void deleteMember(UUID memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException());
        memberRepository.delete(memberEntity);
    }
}
