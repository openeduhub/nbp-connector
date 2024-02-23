package org.edu_sharing.controller;

import lombok.RequiredArgsConstructor;
import org.edu_sharing.models.NbpNodeDTO;
import org.edu_sharing.services.NBPLomPushService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/node")
public class NbpController {

    private final NBPLomPushService nbpLomPushService;

    @GetMapping("/{nodeId}")
    public Mono<String> getNode(@PathVariable String nodeId){
        return nbpLomPushService.getNode(nodeId);
    }


    @PutMapping("/{nodeId}")
    public Mono<Void> addOrUpdateNode(@PathVariable String nodeId) {
        return nbpLomPushService.addOrUpdateNode(nodeId);
    }

    @DeleteMapping("/{nodeId}")
    public Mono<Void> deleteNode(@PathVariable String nodeId) {
        return nbpLomPushService.deleteNode(nodeId);
    }
}
