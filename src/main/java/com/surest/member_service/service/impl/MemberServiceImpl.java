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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    private final MemberMapper memberMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<MemberResponse> getMembers(String firstName, String lastName, Pageable pageable) {
        log.info("Fetching members with firstName: '{}' and lastName: '{}'", firstName, lastName);
        Page<MemberResponse> response = memberRepository.findAll(
                MemberSpecification.filterBy(firstName, lastName),
                pageable
        ).map(MemberEntity::toResponse);
        log.info("Fetched {} members", response.getNumberOfElements());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "members", key = "#memberId")
    public MemberResponse getMemberById(UUID memberId) throws MemberNotFoundException {
        log.info("Fetching member by ID: {}", memberId);
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("Member not found with ID: {}", memberId);
                    return new MemberNotFoundException();
                });
        log.info("Member found with ID: {}", memberId);
        return memberMapper.toResponse(memberEntity);
    }

    @Override
    @Transactional
    public MemberResponse createMember(MemberRequest memberRequest) throws MemberNotFoundException {
        log.info("Creating new member with email: {}", memberRequest.getEmail());
        memberRepository.findByEmail(memberRequest.getEmail())
                .ifPresent(existingMember -> {
                    log.warn("Member already exists with email: {}", memberRequest.getEmail());
                    throw new ResourceAlreadyExistsException();
                });
        MemberEntity memberEntity = memberMapper.toEntity(memberRequest);
        MemberEntity savedEntity = memberRepository.save(memberEntity);
        log.info("Member created successfully with ID: {}", savedEntity.getMemberId());
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    @CachePut(value = "members", key = "#memberId")
    public MemberResponse updateMember(UUID memberId, MemberRequest memberRequest) {
        log.info("Updating member with ID: {}", memberId);
        MemberEntity existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("Member not found with ID: {}", memberId);
                    return new MemberNotFoundException();
                });
        memberMapper.updateEntity(existingMember, memberRequest);
        MemberEntity savedEntity = memberRepository.save(existingMember);
        log.info("Member updated successfully with ID: {}", memberId);
        return memberMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional
    @CacheEvict(value = "members", key = "#memberId")
    public void deleteMember(UUID memberId) {
        log.info("Deleting member with ID: {}", memberId);
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("Member not found with ID: {}", memberId);
                    return new MemberNotFoundException();
                });
        log.info("Member deleted successfully with ID: {}", memberId);
        memberRepository.delete(memberEntity);
    }
}
