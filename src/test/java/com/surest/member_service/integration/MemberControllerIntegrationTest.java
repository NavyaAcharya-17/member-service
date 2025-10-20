package com.surest.member_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.entities.MemberEntity;
import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import com.surest.member_service.repository.MemberRepository;
import com.surest.member_service.repository.RoleRepository;
import com.surest.member_service.repository.UserRepository;
import com.surest.member_service.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        RoleEntity adminRole = roleRepository.save(RoleEntity.builder().name("ROLE_ADMIN").build());
        RoleEntity userRole = roleRepository.save(RoleEntity.builder().name("ROLE_USER").build());

        UserEntity admin = UserEntity.builder()
                .userName("admin")
                .passwordHash(passwordEncoder.encode("password"))
                .roles(Set.of(adminRole))
                .build();
        userRepository.save(admin);

        UserEntity user = UserEntity.builder()
                .userName("user")
                .passwordHash(passwordEncoder.encode("password"))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);

        adminToken = jwtUtil.generateToken(admin.getUsername());
        userToken = jwtUtil.generateToken(user.getUsername());
    }

    @Test
    @DisplayName("GET /api/v1/members - should returns empty list if no members")
    void getMembersReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/v1/members - should returns members with pagination")
    void getMembersWithPagination() throws Exception {
        for (int i = 1; i <= 5; i++) {
            memberRepository.save(MemberEntity.builder()
                    .firstName("Member" + i)
                    .lastName("Last" + i)
                    .email("member" + i + "@example.com")
                    .dateOfBirth(LocalDate.of(1990, 1, i))
                    .build());
        }

        mockMvc.perform(get("/api/v1/members")
                        .header("Authorization", "Bearer " + userToken)
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.totalElements").value(5));
    }

    @Test
    @DisplayName("POST /api/v1/members - should create member as ADMIN")
    void createMemberSuccessForAdmin() throws Exception {
        MemberRequest request = new MemberRequest(
                "Mary", "Elizabeth", LocalDate.of(1990, 1, 1), "mary.elizabeth@example.com"
        );

        mockMvc.perform(post("/api/v1/members")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Mary"))
                .andExpect(jsonPath("$.email").value("mary.elizabeth@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/members - should forbid creation as USER")
    void createMemberForbiddenForRegularUser() throws Exception {
        MemberRequest request = new MemberRequest(
                "Jane", "Smith", LocalDate.of(1995, 5, 5), "jane.smith@example.com"
        );

        mockMvc.perform(post("/api/v1/members")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/members/{id} - should return member by id")
    void getMemberByIdSuccess() throws Exception {
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice@example.com")
                .dateOfBirth(LocalDate.of(1985, 2, 20))
                .build());

        mockMvc.perform(get("/api/v1/members/{id}", member.getMemberId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    @DisplayName("PUT /api/v1/members/{id} - should update member as ADMIN")
    void updateMemberSuccess() throws Exception {
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .firstName("Nancy")
                .lastName("Green")
                .email("nancy@example.com")
                .dateOfBirth(LocalDate.of(1970, 6, 6))
                .build());

        MemberRequest updateRequest = new MemberRequest(
                "Nancy", "Green", LocalDate.of(1970, 6, 6), "nancy@example.com"
        );

        mockMvc.perform(put("/api/v1/members/{id}", member.getMemberId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Nancy"))
                .andExpect(jsonPath("$.email").value("nancy@example.com"));
    }

    @Test
    @DisplayName("DELETE /api/v1/members/{id} - should delete member as ADMIN")
    void deleteMemberSuccess() throws Exception {
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .firstName("Lucy")
                .lastName("Turner")
                .email("lucy@example.com")
                .dateOfBirth(LocalDate.of(1980, 3, 3))
                .build());

        mockMvc.perform(delete("/api/v1/members/{id}", member.getMemberId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertThat(memberRepository.findById(member.getMemberId())).isEmpty();
    }

    @Test
    @DisplayName("DELETE /api/v1/members/{id} - should forbid deletion as USER")
    void deleteMemberForbiddenForUser() throws Exception {
        MemberEntity member = memberRepository.save(MemberEntity.builder()
                .firstName("Lola")
                .lastName("Manson")
                .email("lola@example.com")
                .dateOfBirth(LocalDate.of(1975, 4, 4))
                .build());

        mockMvc.perform(delete("/api/v1/members/{id}", member.getMemberId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}

