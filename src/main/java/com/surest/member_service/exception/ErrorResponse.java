package com.surest.member_service.exception;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
public class ErrorResponse {
    @Builder.Default
    private List<String> errors = new ArrayList<>();
}

