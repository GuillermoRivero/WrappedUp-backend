package com.wrappedup.backend.infrastructure.controller;

import com.wrappedup.backend.domain.User;
import com.wrappedup.backend.domain.port.UserProfileService;
import com.wrappedup.backend.dto.UpdateUserProfileDto;
import com.wrappedup.backend.dto.UserProfileDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @PutMapping
    public ResponseEntity<UserProfileDto> updateProfile(
            @Valid @RequestBody UpdateUserProfileDto profileDto,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userProfileService.updateProfile(user.getId(), profileDto));
    }
    
    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal User user) {
        return userProfileService.getProfile(user.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/public/{username}")
    public ResponseEntity<UserProfileDto> getPublicProfile(@PathVariable String username) {
        return userProfileService.getPublicProfile(username)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
} 