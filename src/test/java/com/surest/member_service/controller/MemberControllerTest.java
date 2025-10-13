package com.surest.member_service.controller;


import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.exception.MemberException;
import com.surest.member_service.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static com.surest.member_service.util.MemberUtil.EMAIL_ALREADY_EXISTS;
import static com.surest.member_service.util.MemberUtil.MEMBER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class MemberControllerTest {
    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    private MemberResponse memberResponse;
    private MemberRequest memberRequest;
    private UUID memberId;
    Pageable pageable;
    private Page<MemberResponse> memberResponsePage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        memberId = UUID.randomUUID();
        memberResponse = MemberResponse.builder()
                .memberId(memberId)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        memberRequest = MemberRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();

        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "lastName"));
        memberResponsePage = new PageImpl<>(Collections.singletonList(memberResponse));
    }

    @Test
    void testGetMembers_Success() {
        when(memberService.getMembers(any(), any(), any(Pageable.class)))
                .thenReturn(memberResponsePage);
        Page<MemberResponse> result = memberController.getMembers("John", "Doe", 0, 10, "lastName,asc");
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).getFirstName());
    }


    @Test
    void testGetMemberById_Success() throws MemberException {
        when(memberService.getMemberById(memberId)).thenReturn(memberResponse);

        ResponseEntity<MemberResponse> response = memberController.getMemberById(memberId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(memberResponse, response.getBody());
    }

    @Test
    void testGetMemberById_NotFound() throws MemberException {
        when(memberService.getMemberById(memberId)).thenThrow(new MemberException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND));
        assertThrows(MemberException.class, () -> memberController.getMemberById(memberId));
    }

    @Test
    void testAddNewMember_Success() throws MemberException {
        when(memberService.createMember(any())).thenReturn(memberResponse);
        ResponseEntity<MemberResponse> response = memberController.addNewMember(memberRequest);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(memberResponse, response.getBody());
    }

    @Test
    void testAddNewMember_EmailExists() throws MemberException {
        when(memberService.createMember(any())).thenThrow(new MemberException(HttpStatus.INTERNAL_SERVER_ERROR, EMAIL_ALREADY_EXISTS));
        assertThrows(MemberException.class, () -> memberController.addNewMember(memberRequest));
    }

    @Test
    void testUpdateMember_Success() throws MemberException {
        when(memberService.updateMember(eq(memberId), any())).thenReturn(memberResponse);
        ResponseEntity<MemberResponse> response = memberController.updateMember(memberId, memberResponse);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(memberResponse, response.getBody());
    }

    @Test
    void testUpdateMember_NotFound() throws MemberException {
        when(memberService.updateMember(eq(memberId), any())).thenThrow(new MemberException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND));
        assertThrows(MemberException.class, () -> memberController.updateMember(memberId, memberResponse));
    }

    @Test
    void testDeleteMember_Success() throws MemberException {
        doNothing().when(memberService).deleteMember(memberId);

        ResponseEntity<Void> response = memberController.deleteMember(memberId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteMember_NotFound() throws MemberException {
        doThrow(new MemberException(HttpStatus.NOT_FOUND, MEMBER_NOT_FOUND)).when(memberService).deleteMember(memberId);
        assertThrows(MemberException.class, () -> memberController.deleteMember(memberId));
    }

}
