package com.school.school_management.dto.response;

import java.io.Serial;
import java.io.Serializable;

public record StudentResponse(
        Long id,
        String name,
        String email,
        int age
) implements Serializable {
   @Serial
   private static final long serialVersionUID = 1L;
}
