package org.edu_sharing.models;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record NBPCourseResponseDTO(
     String type,
     String title,
     int status,
     String detail,
     String instance,
     String additionalProp1,
     String additionalProp2,
     String additionalProp3) {
}

