package com.surest.member_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    private UUID memberId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
