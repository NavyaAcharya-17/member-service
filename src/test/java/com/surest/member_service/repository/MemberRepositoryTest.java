package com.surest.member_service.repository;

import com.surest.member_service.entities.MemberEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest(excludeAutoConfiguration = {org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration.class})
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    private MemberEntity createSampleMember() {
        return MemberEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .build();
    }

    @Test
    @DisplayName("CREATE - should save a member successfully")
    void testSaveMember() {
        MemberEntity saved = memberRepository.saveAndFlush(createSampleMember());

        assertThat(saved.getMemberId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("READ - should find a member by ID")
    void testFindById() {
        MemberEntity saved = memberRepository.saveAndFlush(createSampleMember());

        Optional<MemberEntity> found = memberRepository.findById(saved.getMemberId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("READ - should find a member by email")
    void testFindByEmail() {
        MemberEntity saved = memberRepository.saveAndFlush(createSampleMember());

        Optional<MemberEntity> found = memberRepository.findByEmail("john.doe@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("UPDATE - should update an existing member")
    void testUpdateMember() {
        MemberEntity saved = memberRepository.saveAndFlush(createSampleMember());

        saved.setFirstName("Jane");
        saved.setLastName("Smith");
        MemberEntity updated = memberRepository.saveAndFlush(saved);

        assertThat(updated.getFirstName()).isEqualTo("Jane");
        assertThat(updated.getLastName()).isEqualTo("Smith");
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(updated.getCreatedAt());
    }

    @Test
    @DisplayName("DELETE - should delete a member by ID")
    void testDeleteMember() {
        MemberEntity saved = memberRepository.saveAndFlush(createSampleMember());

        memberRepository.deleteById(saved.getMemberId());

        Optional<MemberEntity> found = memberRepository.findById(saved.getMemberId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("should throw error on duplicate email")
    void testDuplicateEmail() {
        MemberEntity member1 = MemberEntity.builder()
                .firstName("Alice")
                .lastName("Brown")
                .dateOfBirth(LocalDate.of(1991, 7, 15))
                .email("alice@example.com")
                .build();

        MemberEntity member2 = MemberEntity.builder()
                .firstName("Bob")
                .lastName("Green")
                .dateOfBirth(LocalDate.of(1989, 3, 10))
                .email("alice@example.com")
                .build();

        memberRepository.saveAndFlush(member1);

        assertThrows(
                DataIntegrityViolationException.class,
                () -> memberRepository.saveAndFlush(member2)
        );
    }

}
