package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.request.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.entity.User;
import dev.yoxaron.cloudstorage.exception.UserAlreadyExistsException;
import dev.yoxaron.cloudstorage.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(UserAuthRequestDto user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new UserAlreadyExistsException("Username is already in use");
        }

        User userToSave = User.builder()
                .username(user.username())
                .password(passwordEncoder.encode(user.password()))
                .build();

        return userRepository.save(userToSave);
    }
}
