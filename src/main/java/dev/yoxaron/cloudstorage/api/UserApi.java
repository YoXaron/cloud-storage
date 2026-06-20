package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.user.GetCurrentUserDocs;
import dev.yoxaron.cloudstorage.dto.response.UserAuthResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/user")
public interface UserApi {

    @GetMapping("/me")
    @GetCurrentUserDocs
    ResponseEntity<UserAuthResponseDto> me(@AuthenticationPrincipal SecurityUser user);
}
