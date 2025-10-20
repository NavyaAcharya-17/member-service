package com.surest.member_service.repository;

import com.surest.member_service.entities.RoleEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = {org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class})
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    private RoleEntity createRole(String name) {
        return RoleEntity.builder()
                .name(name)
                .build();
    }

    @Test
    @DisplayName("CREATE - should save and retrieve a role successfully")
    void testSaveRole() {
        RoleEntity role = createRole("ADMIN");

        RoleEntity saved = roleRepository.saveAndFlush(role);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("READ - should find multiple roles by names")
    void testFindByNameIn() {
        roleRepository.saveAllAndFlush(Set.of(
                createRole("USER"),
                createRole("ADMIN"),
                createRole("MANAGER")
        ));

        Set<RoleEntity> found = roleRepository.findByNameIn(Set.of("USER", "ADMIN"));

        assertEquals(2, found.size());

        Set<String> roleNames = found.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toSet());
        assertTrue(roleNames.containsAll(Set.of("USER", "ADMIN")));
    }

    @Test
    @DisplayName("READ - should return empty set when none of the names exist")
    void testFindByNameInNotFound() {
        Set<RoleEntity> found = roleRepository.findByNameIn(Set.of("GUEST", "TEST"));

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("DELETE - should delete a role")
    void testDeleteRole() {
        RoleEntity saved = roleRepository.saveAndFlush(createRole("TEMP"));

        roleRepository.delete(saved);

        Set<RoleEntity> found = roleRepository.findByNameIn(Set.of("TEMP"));
        assertTrue(found.isEmpty());
    }
}
