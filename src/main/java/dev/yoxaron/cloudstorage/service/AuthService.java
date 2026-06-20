package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.request.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final ResourceMetadataService resourceMetadataService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Authentication register(UserAuthRequestDto userDto) {
        User user = userService.register(userDto);
        resourceMetadataService.createRootDirectoryForNewUser(user.getId());
        log.debug("User {} registered with id {}", user.getUsername(), user.getId());
        return this.login(userDto);
    }

    public Authentication login(UserAuthRequestDto userDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.username(), userDto.password()));
        log.info("User {} logged in", authentication.getName());
        return authentication;
    }
}
