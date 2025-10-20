package com.surest.member_service.repository;

import com.surest.member_service.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Set<RoleEntity> findByNameIn(Set<String> roles);
}
