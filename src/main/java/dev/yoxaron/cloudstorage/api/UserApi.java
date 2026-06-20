package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.common.CommonErrorResponses;
import dev.yoxaron.cloudstorage.docs.user.GetCurrentUserDocs;
import dev.yoxaron.cloudstorage.dto.response.UserAuthResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/user")
public interface UserApi {

    @Operation(
            summary = "Get current user",
            description = """
                Returns username of the currently authenticated user.
                
                Requires an active authenticated session.
                """
    )
    @GetMapping("/me")
    @GetCurrentUserDocs
    @CommonErrorResponses
    ResponseEntity<UserAuthResponseDto> me(@AuthenticationPrincipal SecurityUser user);
}
