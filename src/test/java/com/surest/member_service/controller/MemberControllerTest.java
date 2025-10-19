package com.surest.member_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.exception.MemberNotFoundException;
import com.surest.member_service.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private MemberRequest validRequest;
    private MemberResponse validResponse;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();

        validRequest = MemberRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        validResponse = MemberResponse.builder()
                .memberId(memberId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }
    // --------------------GET ALL MEMBERS --------------------
    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN"})
    @WithMockUser
    void getAllMembersReturnsPageForRole(String role) throws Exception {
        Page<MemberResponse> page = new PageImpl<>(List.of(validResponse));
        when(memberService.getMembers(any(), any(), any()))
                .thenReturn(page);
        ResultActions perform = mockMvc.perform(get("/api/v1/members")
                .param("page", "0")
                .param("size", "5")
                .param("sort", "lastName,asc"));
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getAllMembersReturns403WhenNoAuth() throws Exception {
        mockMvc.perform(get("/api/v1/members"))
                .andExpect(status().isForbidden());
    }

    // --------------------GET MEMBER BY ID --------------------
    @ParameterizedTest
    @ValueSource(strings = {"USER", "ADMIN"})
    void getMemberByIdReturnsMemberForRoleWhenExists(String role) throws Exception {
        when(memberService.getMemberById(memberId)).thenReturn(validResponse);

        mockMvc.perform(get("/api/v1/members/{id}", memberId)
                        .with(user("test").roles(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void getMemberByIdReturns404WhenNotFound() throws Exception {
        when(memberService.getMemberById(any(UUID.class)))
                .thenThrow(new MemberNotFoundException(HttpStatus.NOT_FOUND,"Member not found"));
        mockMvc.perform(get("/api/v1/members/{id}", memberId))
                .andExpect(status().isNotFound());
    }

    // --------------------CREATE MEMBER --------------------
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void addNewMemberReturns201WhenValidRequest() throws Exception {
        when(memberService.createMember(any(MemberRequest.class))).thenReturn(validResponse);
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void addNewMemberReturns400WhenInValidRequest() throws Exception {
        MemberRequest invalidRequest = MemberRequest.builder().firstName("").lastName("")
                .email("invalid-email").dateOfBirth(LocalDate.now().plusDays(1))
                .build();
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // --------------------UPDATE MEMBER --------------------
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateMemberReturns200ForValidRequest() throws Exception {
        when(memberService.updateMember(eq(memberId), any(MemberResponse.class))).thenReturn(validResponse);
        mockMvc.perform(put("/api/v1/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validResponse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateMemberReturns404WhenMemberNotFound() throws Exception {
        when(memberService.updateMember(any(UUID.class), any(MemberResponse.class)))
                .thenThrow(new MemberNotFoundException(HttpStatus.NOT_FOUND,"Member not found"));
        mockMvc.perform(put("/api/v1/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validResponse)))
                .andExpect(status().isNotFound());
    }

    // --------------------DELETE MEMBER--------------------
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteMemberReturns204WhenSuccess() throws Exception {
        doNothing().when(memberService).deleteMember(memberId);
        mockMvc.perform(delete("/api/v1/members/{id}", memberId))
                .andExpect(status().isNoContent());
        verify(memberService, times(1)).deleteMember(memberId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteMemberReturns404WhenNotFound() throws Exception {
        doThrow(new MemberNotFoundException(HttpStatus.NOT_FOUND,"Member not found"))
                .when(memberService).deleteMember(memberId);
        mockMvc.perform(delete("/api/v1/members/{id}", memberId))
                .andExpect(status().isNotFound());
    }

}
