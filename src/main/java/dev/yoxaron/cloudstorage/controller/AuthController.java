package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.dto.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.dto.UserAuthResponseDto;
import dev.yoxaron.cloudstorage.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SecurityContextRepository securityContextRepository;

    @PostMapping("/sign-up")
    public ResponseEntity<UserAuthResponseDto> signUp(
            @RequestBody @Valid UserAuthRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = authService.register(dto);
        createSecurityContext(authentication, request, response);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UserAuthResponseDto(dto.username()));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<UserAuthResponseDto> signIn(
            @RequestBody @Valid UserAuthRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = authService.login(dto);
        createSecurityContext(authentication, request, response);

        return ResponseEntity.ok()
                .body(new UserAuthResponseDto(dto.username()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserAuthResponseDto> me(Principal principal) {
        return ResponseEntity.ok(new UserAuthResponseDto(principal.getName()));
    }

    private void createSecurityContext(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextRepository.saveContext(context, request, response);
    }
}
