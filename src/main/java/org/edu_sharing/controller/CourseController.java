package org.edu_sharing.controller;

import lombok.RequiredArgsConstructor;
import org.edu_sharing.models.NBPCourseDTO;
import org.edu_sharing.models.NBPCourseResponseDTO;
import org.edu_sharing.services.NBPLomPushService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    private final NBPLomPushService nbpLomPushService;

    @PutMapping("/{nodeId}")
    public Mono<Void> addOrUpdateCourse(@PathVariable String nodeId) {
        return nbpLomPushService.addOrUpdateCourse(nodeId);
    }

    @DeleteMapping("/{nodeId}")
    public Mono<Void> deleteCourse(@PathVariable String nodeId) {
        return nbpLomPushService.deleteCourse(nodeId);
    }
}
