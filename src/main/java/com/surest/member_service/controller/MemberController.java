package com.surest.member_service.controller;

import com.surest.member_service.dto.MemberRequest;
import com.surest.member_service.dto.MemberResponse;
import com.surest.member_service.exception.MemberException;
import com.surest.member_service.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Page<MemberResponse> getMembers(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName,asc") String sort
    ) {
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        Sort.Direction sortDirection = sortParams.length > 1 ? Sort.Direction.fromString(sortParams[1]) : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        return memberService.getMembers(firstName, lastName, pageable);
    }

    @GetMapping("/members/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable("id") UUID id) throws MemberException {
        MemberResponse memberResponse = memberService.getMemberById(id);
        return ResponseEntity.ok(memberResponse);
    }

    @PostMapping("/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponse> addNewMember(@Valid @RequestBody MemberRequest request) throws MemberException {
        MemberResponse memberResponse = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(memberResponse);
    }

    @PutMapping("/members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable("id") UUID id,
            @RequestBody MemberResponse memberResponse) throws MemberException {
        MemberResponse updatedMember = memberService.updateMember(id, memberResponse);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/members/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMember(@PathVariable("id") UUID memberId) throws MemberException {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
