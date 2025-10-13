package com.surest.member_service.service.impl;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import com.surest.member_service.exception.MemberException;
import com.surest.member_service.mapper.MemberMapper;
import com.surest.member_service.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    private MemberEntity memberEntity;
    private MemberRequest memberRequest;
    private MemberResponse memberResponse;
    private UUID memberId;
    private Page<MemberEntity> memberEntityPage;
    Page<MemberResponse> memberResponsePage;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        memberEntity = new MemberEntity();
        memberEntity.setMemberId(memberId);
        memberEntity.setEmail("test@example.com");

        memberRequest = new MemberRequest();
        memberRequest.setEmail("test@example.com");

        memberResponse = new MemberResponse();
        memberResponse.setMemberId(memberId);
        memberResponse.setEmail("test@example.com");

        memberEntityPage = new PageImpl<>(Collections.singletonList(memberEntity));
        memberResponsePage = new PageImpl<>(Collections.singletonList(memberResponse));
        pageable = mock(Pageable.class);
    }

    @Test
    void testGetMembers_Success() {
        when(memberRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(memberEntityPage);
        when(memberMapper.mapToResponse(memberEntity)).thenReturn(memberResponse);

        Page<MemberResponse> result = memberService.getMembers("John", "Doe", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(memberResponse.getEmail(), result.getContent().get(0).getEmail());
    }

    @Test
    void testGetMemberById_Success() throws MemberException {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(memberMapper.toResponse(memberEntity)).thenReturn(memberResponse);

        MemberResponse result = memberService.getMemberById(memberId);

        assertEquals(memberId, result.getMemberId());
        assertEquals(memberResponse.getEmail(), result.getEmail());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberException.class, () -> memberService.getMemberById(memberId));
    }

    @Test
    void testCreateMember_Success() throws MemberException {
        when(memberRepository.findByEmail(memberRequest.getEmail())).thenReturn(Optional.empty());
        when(memberMapper.toEntity(memberRequest)).thenReturn(memberEntity);
        when(memberRepository.save(memberEntity)).thenReturn(memberEntity);
        when(memberMapper.toResponse(memberEntity)).thenReturn(memberResponse);

        MemberResponse result = memberService.createMember(memberRequest);

        assertEquals(memberRequest.getEmail(), result.getEmail());
    }

    @Test
    void testCreateMember_EmailExists() {
        when(memberRepository.findByEmail(memberRequest.getEmail())).thenReturn(Optional.of(memberEntity));
        assertThrows(MemberException.class, () -> memberService.createMember(memberRequest));
    }

    @Test
    void testUpdateMember_Success() throws MemberException {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        doNothing().when(memberMapper).updateEntityFromResponse(memberResponse, memberEntity);
        when(memberRepository.save(memberEntity)).thenReturn(memberEntity);
        when(memberMapper.toResponse(memberEntity)).thenReturn(memberResponse);

        MemberResponse result = memberService.updateMember(memberId, memberResponse);

        assertEquals(memberId, result.getMemberId());
    }

    @Test
    void testUpdateMember_NotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        assertThrows(MemberException.class, () -> memberService.updateMember(memberId, memberResponse));
    }

    @Test
    void testDeleteMember_Success() {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(memberId);

        assertDoesNotThrow(() -> memberService.deleteMember(memberId));
        verify(memberRepository, times(1)).deleteById(memberId);
    }

    @Test
    void testDeleteMember_NotFound() {
        when(memberRepository.existsById(memberId)).thenReturn(false);
        assertThrows(MemberException.class, () -> memberService.deleteMember(memberId));
    }

}
