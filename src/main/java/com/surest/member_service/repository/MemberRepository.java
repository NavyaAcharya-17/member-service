package com.surest.member_service.repository;

import com.surest.member_service.entities.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID>, JpaSpecificationExecutor<MemberEntity> {

    Optional<MemberEntity> findByEmail(String email);
}
