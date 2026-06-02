package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.ParsedPath;
import dev.yoxaron.cloudstorage.dto.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final ResourceMetadataService resourceMetadataService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Authentication register(UserAuthRequestDto userDto) {
        User user = userService.register(userDto);
        resourceMetadataService.createDirectory(new ParsedPath("/", "/", true), user.getId());
        return this.login(userDto);
    }

    public Authentication login(UserAuthRequestDto userDto) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.username(), userDto.password())
        );
    }
}
