package com.school.school_management.dto.response;

public record TeacherResponse(
        Long id,
        String name,
        String email,
        String specialization,
        int totalCourses // we show how many courses, not the whole list
) {}
