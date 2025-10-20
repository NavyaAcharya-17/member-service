package com.surest.member_service.service.impl;


import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import com.surest.member_service.exception.MemberNotFoundException;
import com.surest.member_service.exception.ResourceAlreadyExistsException;
import com.surest.member_service.mapper.MemberMapper;
import com.surest.member_service.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    private MemberRequest request;
    private MemberEntity member;
    private MemberResponse response;
    private UUID memberId;
    Timestamp now;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();
        request = new MemberRequest();
        request.setEmail("test@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");

        member = new MemberEntity();
        member.setMemberId(memberId);
        member.setEmail("test@example.com");
        member.setFirstName("John");
        member.setLastName("Doe");

        response = new MemberResponse();
        response.setMemberId(memberId);
        response.setEmail("test@example.com");
        response.setFirstName("John");
        response.setLastName("Doe");
    }

    @Test
    void testGetMembersSuccess() {
        //Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberEntity> page = new PageImpl<>(List.of(member));
        when(memberRepository.findAll(Mockito.any(Specification.class), eq(pageable)))
                .thenReturn(page);
        //When
        Page<MemberResponse> result = memberService.getMembers("John", "Doe", pageable);

        //Then
        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getFirstName());
        verify(memberRepository).findAll(Mockito.any(Specification.class), eq(pageable));
    }

    @Test
    void testGetMemberByIdSuccess() throws MemberNotFoundException {
        // Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberMapper.toResponse(member)).thenReturn(response);

        //When
        MemberResponse result = memberService.getMemberById(memberId);

        //Then
        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
    }

    @Test
    void testGetMemberByIdNotFound() {
        //Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        //Expect exception
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(memberId));
        verify(memberRepository, times(1)).findById(memberId);
    }

    @Test
    void testCreateMemberSuccess() throws MemberNotFoundException {
        //Arrange
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(memberMapper.toEntity(request)).thenReturn(member);
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toResponse(member)).thenReturn(response);

        //When
        MemberResponse result = memberService.createMember(request);

        //Then
        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
    }

    @Test
    void testCreateMemberAlreadyExists() {
        //Arrange
        when(memberRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(member));

        //Expect exception
        assertThrows(ResourceAlreadyExistsException.class, () -> memberService.createMember(request));
        verify(memberRepository, never()).save(any());
    }

    @Test
    void testUpdateMemberSuccess() throws MemberNotFoundException {
        //Arrange
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        doNothing().when(memberMapper).updateEntity(member,request);
        when(memberRepository.save(member)).thenReturn(member);
        when(memberMapper.toResponse(member)).thenReturn(response);

        //When
        MemberResponse result = memberService.updateMember(memberId, request);

        //Then
        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
    }

    @Test
    void updateMemberShouldThrowExceptionWhenMemberNotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(memberId, request));
        verify(memberRepository, never()).save(any());
    }

    @Test
    void testDeleteMemberSuccess()  {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        memberService.deleteMember(memberId);

        verify(memberRepository).delete(member);
    }

    @Test
    void testDeleteMemberShouldThrowExceptionWhenMemberNotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.deleteMember(memberId));
        verify(memberRepository, never()).delete(any(MemberEntity.class));
    }
}
