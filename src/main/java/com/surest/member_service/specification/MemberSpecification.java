package com.surest.member_service.specification;

import com.surest.member_service.entities.MemberEntity;
import org.springframework.data.jpa.domain.Specification;

public class MemberSpecification {

    private MemberSpecification() {
    }

    public static Specification<MemberEntity> trueSpec() {
        return (root, query, cb) -> cb.conjunction();
    }

    public static Specification<MemberEntity> firstNameContains(String firstName) {
        return (root, query, cb) -> {
            if (firstName == null || firstName.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%");
        };
    }

    public static Specification<MemberEntity> lastNameContains(String lastName) {
        return (root, query, cb) -> {
            if (lastName == null || lastName.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%");
        };
    }
}
