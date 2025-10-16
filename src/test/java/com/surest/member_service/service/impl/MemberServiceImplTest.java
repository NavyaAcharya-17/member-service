package com.surest.member_service.service.impl;


import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.entities.MemberEntity;
import com.surest.member_service.exception.MemberException;
import com.surest.member_service.mapper.MemberMapper;
import com.surest.member_service.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {
    @InjectMocks
    private MemberServiceImpl memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberMapper memberMapper;

    private MemberRequest memberRequest;
    private MemberEntity memberEntity;
    private MemberResponse memberResponse;
    private UUID memberId;
    Timestamp now;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberId = UUID.randomUUID();
        memberRequest = MemberRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        memberEntity = MemberEntity.builder()
                .memberId(memberId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        memberResponse = MemberResponse.builder()
                .memberId(memberId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    void testGetMembersShouldReturnPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MemberEntity> page = new PageImpl<>(List.of(memberEntity));
        when(memberRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        Page<MemberResponse> result = memberService.getMembers(null, null, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getTotalPages()).isEqualTo(1);
        verify(memberRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void test_getMemberById_ShouldReturnMember_WhenFound() throws MemberException {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        when(memberMapper.toResponse(memberEntity)).thenReturn(memberResponse);
        MemberResponse result = memberService.getMemberById(memberId);
        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
    }

    @Test
    void test_getMemberById_ShouldThrowException_WhenNotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.getMemberById(memberId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void test_createMember_ShouldCreateAndReturnMember() throws MemberException {
        when(memberRepository.findByEmail(memberRequest.getEmail())).thenReturn(Optional.empty());
        when(memberMapper.toEntity(memberRequest)).thenReturn(memberEntity);
        when(memberRepository.save(memberEntity)).thenReturn(memberEntity);
        when(memberMapper.toResponse(memberEntity)).thenReturn(memberResponse);

        MemberResponse result = memberService.createMember(memberRequest);

        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
    }

    @Test
    void test_createMember_ShouldThrowException_WhenEmailExists() {
        when(memberRepository.findByEmail(memberRequest.getEmail())).thenReturn(Optional.of(memberEntity));

        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.createMember(memberRequest));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getHttpStatus());
    }

    @Test
    void test_updateMember_ShouldUpdateAndReturnMember() throws MemberException {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(memberEntity));
        doNothing().when(memberMapper).updateEntityFromResponse(memberResponse, memberEntity);
        when(memberRepository.save(memberEntity)).thenReturn(memberEntity);
        when(memberMapper.toResponse(memberEntity)).thenReturn(memberResponse);

        MemberResponse result = memberService.updateMember(memberId, memberResponse);

        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
    }

    @Test
    void test_updateMember_ShouldThrowException_WhenMemberNotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.updateMember(memberId, memberResponse));

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }

    @Test
    void test_deleteMember_ShouldDelete_WhenMemberExists()  {
        when(memberRepository.existsById(memberId)).thenReturn(true);
        doNothing().when(memberRepository).deleteById(memberId);

        assertDoesNotThrow(() -> memberService.deleteMember(memberId));
    }

    @Test
    void test_deleteMember_ShouldThrowException_WhenMemberNotFound() {
        when(memberRepository.existsById(memberId)).thenReturn(false);
        MemberException exception = assertThrows(MemberException.class,
                () -> memberService.deleteMember(memberId));
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
    }
}
