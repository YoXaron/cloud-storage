package dev.yoxaron.cloudstorage.api;

import dev.yoxaron.cloudstorage.docs.user.GetCurrentUserDocs;
import dev.yoxaron.cloudstorage.dto.UserAuthResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@RequestMapping("/api/user")
public interface UserApi {

    @GetMapping("/me")
    @GetCurrentUserDocs
    ResponseEntity<UserAuthResponseDto> me(Principal principal);
}
