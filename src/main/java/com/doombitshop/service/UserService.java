package com.doombitshop.service;

import com.doombitshop.constants.EmailRegex;
import com.doombitshop.dto.UserDTO;
import com.doombitshop.dto.UserRegisterRequest;
import com.doombitshop.exception.GlobalExceptionHandler;
import com.doombitshop.model.User;
import com.doombitshop.repository.UserRepository;
import com.doombitshop.utils.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;


    }

    public Mono<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::userToUserDTO)
                .switchIfEmpty(Mono.error(new GlobalExceptionHandler.UserNotFoundException(username)));
    }

    public Mono<UserDTO> getUserById(String id) {
        return userRepository.findById(id)
                .map(userMapper::userToUserDTO)
                .switchIfEmpty(Mono.error(new GlobalExceptionHandler.UserNotFoundException(id)));
    }

    public Flux<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .map(userMapper::userToUserDTO)
                .switchIfEmpty(Flux.error(() ->
                        Exceptions.propagate(new GlobalExceptionHandler.NoUsersFoundException()))
                );
    }

    public Mono<String> createUser(UserRegisterRequest userRegisterRequest) {
        return validateUsername(userRegisterRequest.getUsername())
                .then(validateEmail(userRegisterRequest.getEmail()))
                .then(Mono.defer(() -> saveUser(userRegisterRequest)))
                .thenReturn("User created, check email!")
                .onErrorResume(e -> {
                    System.out.println("Error during user creation: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    private Mono<Void> validateUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMap(existingUser -> Mono.error(new GlobalExceptionHandler.UsernameAlreadyExistsException(username)));
    }

    private Mono<Void> validateEmail(String email) {
        if (!Pattern.compile(EmailRegex.EMAIL_REGEX).matcher(email).matches()) {
            return Mono.error(new GlobalExceptionHandler.InvalidEmailException(email));
        }
        return userRepository.findByEmail(email)
                .flatMap(existingEmail -> Mono.error(new GlobalExceptionHandler.EmailAlreadyExistsException(email)));
    }

    private Mono<User> saveUser(UserRegisterRequest userRegisterRequest) {
        User newUser = createUserFromRequest(userRegisterRequest);
        return userRepository.save(newUser);
    }

    private User createUserFromRequest(UserRegisterRequest userRegisterRequest) {
        User newUser = new User();
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setEmail(userRegisterRequest.getEmail());
        newUser.setUsername(userRegisterRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        return newUser;
    }


}
