package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.dto.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.dto.UserAuthResponseDto;
import dev.yoxaron.cloudstorage.exception.UnauthorizedException;
import dev.yoxaron.cloudstorage.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
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
        log.info("User {} registered successfully", dto.username());

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
        log.info("User {} logged in", dto.username());

        return ResponseEntity.ok()
                .body(new UserAuthResponseDto(dto.username()));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new UnauthorizedException("Not authenticated");
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        log.info("User {} logged out", authentication.getName());
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
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
