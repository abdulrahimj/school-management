package com.school.school_management.dto.response;

public record CourseResponse(
        Long id,
        String name,
        String description,
        String teacherName //we show teacher name, not the whole teacher object
) {}
