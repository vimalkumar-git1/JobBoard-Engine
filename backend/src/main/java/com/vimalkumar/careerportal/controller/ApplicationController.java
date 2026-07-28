package com.vimalkumar.careerportal.controller;

import com.vimalkumar.careerportal.dto.ApplicationCreateRequest;
import com.vimalkumar.careerportal.dto.ApplicationDto;
import com.vimalkumar.careerportal.dto.ApplicationStatusUpdateRequest;
import com.vimalkumar.careerportal.entity.User;
import com.vimalkumar.careerportal.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Backs the Kanban board: SAVED -> APPLIED -> INTERVIEWING -> OFFERED / REJECTED */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationDto> create(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody ApplicationCreateRequest request) {
        return ResponseEntity.ok(applicationService.create(user, request));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationDto>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getForUser(user.getId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApplicationDto> updateStatus(@AuthenticationPrincipal User user,
                                                         @PathVariable Long id,
                                                         @Valid @RequestBody ApplicationStatusUpdateRequest request) {
        return ResponseEntity.ok(applicationService.updateStatus(id, user.getId(), request.getStatus()));
    }
}
