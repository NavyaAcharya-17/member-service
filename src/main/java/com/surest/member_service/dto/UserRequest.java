package com.surest.member_service.dto;

import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "User name is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    @NotEmpty(message = "At least one role is required")
    private Set<String> roles;

    public UserEntity toEntity(String encodePass, Set<RoleEntity> roles) {
        return UserEntity.builder()
                .userName(this.username)
                .passwordHash(encodePass)
                .roles(roles)
                .build();
    }
}
