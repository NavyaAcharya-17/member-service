package com.surest.member_service.entities;

import com.surest.member_service.dto.MemberResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
@Table(name = "member")
public class MemberEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID memberId;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "email", length = 255, unique = true)
    private String email;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public MemberResponse toResponse() {
        return MemberResponse.builder()
                .memberId(this.memberId)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .dateOfBirth(this.dateOfBirth)
                .email(this.email)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }
}

