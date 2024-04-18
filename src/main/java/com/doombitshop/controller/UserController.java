package com.doombitshop.controller;

import com.doombitshop.dto.UserDTO;
import com.doombitshop.dto.UserRegisterRequest;
import com.doombitshop.exception.GlobalExceptionHandler;
import com.doombitshop.model.User;
import com.doombitshop.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/{username}")
//    public Mono<ResponseEntity<UserDTO>> getUserByUsername(@PathVariable String username) {
//        return userService.getUserByUsername(username)
//                .map(ResponseEntity::ok);
//    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> getUserById(@Validated @PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public Flux<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> createUser(@Validated @RequestBody UserRegisterRequest userRegisterRequest) {
        return userService.createUser(userRegisterRequest)
                .map(ResponseEntity::ok)
                .onErrorResume(GlobalExceptionHandler.UsernameAlreadyExistsException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage())))
                .onErrorResume(GlobalExceptionHandler.EmailAlreadyExistsException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage())))
                .onErrorResume(GlobalExceptionHandler.InvalidEmailException.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage())))
                .onErrorResume(Exception.class, e ->
                        Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error")));
    }

}
