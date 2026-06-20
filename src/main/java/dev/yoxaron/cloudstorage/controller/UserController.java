package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.api.UserApi;
import dev.yoxaron.cloudstorage.dto.response.UserAuthResponseDto;
import dev.yoxaron.cloudstorage.security.SecurityUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserApi {

    @Override
    public ResponseEntity<UserAuthResponseDto> me(SecurityUser user) {
        return ResponseEntity.ok(new UserAuthResponseDto(user.getUsername()));
    }
}
