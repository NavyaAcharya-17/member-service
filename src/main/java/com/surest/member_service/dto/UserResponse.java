package com.surest.member_service.dto;

import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID userId;
    private String username;
    private Set<String> roles;

    public static UserResponse fromEntity(UserEntity entity) {
        return UserResponse.builder()
                .userId(entity.getUserId())
                .username(entity.getUsername())
                .roles(entity.getRoles().stream().map(RoleEntity::getName).collect(java.util.stream.Collectors.toSet()))
                .build();
    }
}
