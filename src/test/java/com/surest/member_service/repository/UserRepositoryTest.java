package com.surest.member_service.repository;

import com.surest.member_service.entities.RoleEntity;
import com.surest.member_service.entities.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = {org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private UserEntity createSampleUser() {
        RoleEntity userRole = roleRepository.save(
                RoleEntity.builder()
                        .name("USER")
                        .build()
        );

        return UserEntity.builder()
                .userName("john_doe")
                .passwordHash("hashed_password")
                .roles(new HashSet<>(Set.of(userRole)))
                .build();
    }

    @Test
    @DisplayName("FIND - should return user if username exists")
    void testFindByUsernameFound() {
        UserEntity saved = userRepository.saveAndFlush(createSampleUser());

        Optional<UserEntity> found = userRepository.findByUserName("john_doe");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john_doe");
        assertThat(found.get().getUserId()).isEqualTo(saved.getUserId());
    }

    @Test
    @DisplayName("FIND - should return empty if username does not exist")
    void testFindByUsernameNotFound() {
        Optional<UserEntity> found = userRepository.findByUserName("nonexistent_user");

        assertThat(found).isEmpty();
    }
}
