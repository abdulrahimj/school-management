package com.school.school_management.dto.response;

public record StudentResponse(
        Long id,
        String name,
        String email,
        int age
) {}
