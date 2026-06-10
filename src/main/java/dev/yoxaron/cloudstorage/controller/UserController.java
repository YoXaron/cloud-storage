package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.docs.user.GetCurrentUserDocs;
import dev.yoxaron.cloudstorage.dto.UserAuthResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    @GetCurrentUserDocs
    public ResponseEntity<UserAuthResponseDto> me(Principal principal) {
        return ResponseEntity.ok(new UserAuthResponseDto(principal.getName()));
    }
}
