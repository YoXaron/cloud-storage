package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.auth.SignInDocs;
import dev.yoxaron.cloudstorage.docs.auth.SignOutDocs;
import dev.yoxaron.cloudstorage.docs.auth.SignUpDocs;
import dev.yoxaron.cloudstorage.docs.common.CommonAuthErrorResponses;
import dev.yoxaron.cloudstorage.docs.common.CommonErrorResponses;
import dev.yoxaron.cloudstorage.dto.request.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.dto.response.UserAuthResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/auth")
public interface AuthApi {

    @Operation(
            summary = "Register user",
            description = """
                Creates a new user account.
                
                After successful registration, the user is automatically authenticated
                and a session cookie is created.
                """
    )
    @PostMapping("/sign-up")
    @SignUpDocs
    @CommonAuthErrorResponses
    ResponseEntity<UserAuthResponseDto> signUp(
            @RequestBody @Valid UserAuthRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "Authenticate user",
            description = """
                Authenticates a user using username and password.
                
                After successful authentication, a session cookie is created.
                """
    )
    @PostMapping("/sign-in")
    @SignInDocs
    @CommonAuthErrorResponses
    ResponseEntity<UserAuthResponseDto> signIn(
            @RequestBody @Valid UserAuthRequestDto dto,
            HttpServletRequest request,
            HttpServletResponse response
    );

    @Operation(
            summary = "Sign out",
            description = """
                Invalidates the current user session.
                
                Requires an authenticated user.
                """
    )
    @PostMapping("/sign-out")
    @SignOutDocs
    @CommonErrorResponses
    ResponseEntity<Void> signOut(HttpServletRequest request);
}
