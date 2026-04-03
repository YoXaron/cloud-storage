package dev.yoxaron.cloudstorage.service;

import dev.yoxaron.cloudstorage.dto.UserAuthRequestDto;
import dev.yoxaron.cloudstorage.entity.User;
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
    public void register(UserAuthRequestDto user) {
        if (userRepository.findByUsername(user.username()).isPresent()) {
            throw new RuntimeException("Username is already in use"); //todo custom exception
        }

        User userToSave = User.builder()
                .username(user.username())
                .password(passwordEncoder.encode(user.password()))
                .build();

        userRepository.save(userToSave);
    }
}
