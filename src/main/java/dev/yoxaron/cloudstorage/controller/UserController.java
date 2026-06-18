package dev.yoxaron.cloudstorage.controller;

import dev.yoxaron.cloudstorage.api.UserApi;
import dev.yoxaron.cloudstorage.dto.UserAuthResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController implements UserApi {

    @Override
    public ResponseEntity<UserAuthResponseDto> me(Principal principal) {
        return ResponseEntity.ok(new UserAuthResponseDto(principal.getName()));
    }
}
