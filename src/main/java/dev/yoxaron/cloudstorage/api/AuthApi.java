package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.auth.SignInDocs;
import dev.yoxaron.cloudstorage.docs.auth.SignOutDocs;
import dev.yoxaron.cloudstorage.docs.auth.SignUpDocs;
import dev.yoxaron.cloudstorage.dto.request.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.dto.response.UserAuthResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface AuthApi {

    @PostMapping("/sign-up")
    @SignUpDocs
    ResponseEntity<UserAuthResponseDto> signUp(
            @RequestBody @Valid UserAuthRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    );

    @PostMapping("/sign-in")
    @SignInDocs
    ResponseEntity<UserAuthResponseDto> signIn(
            @RequestBody @Valid UserAuthRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    );

    @PostMapping("/sign-out")
    @SignOutDocs
    ResponseEntity<Void> signOut(HttpServletRequest request);
}
