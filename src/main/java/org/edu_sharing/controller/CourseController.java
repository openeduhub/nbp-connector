package org.edu_sharing.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseController {

    @PutMapping("/{nodeId}")
    public ResponseEntity addOrUpdateCourse(@PathVariable String nodeId, @RequestParam(required = false, defaultValue = "false") boolean update) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{nodeId}")
    public ResponseEntity deleteCourse(@PathVariable String nodeId) {
        return ResponseEntity.ok().build();
    }
}
