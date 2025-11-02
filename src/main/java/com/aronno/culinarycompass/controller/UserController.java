package com.aronno.culinarycompass.controller;

import com.aronno.culinarycompass.entity.User;
import com.aronno.culinarycompass.service.UserService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/user/profile")
@Validated
public class UserController {

    private static final String RATE_LIMIT_REMAINING_HEADER = "X-Rate-Limit-Remaining";
    private static final int RATE_LIMIT_CAPACITY = 20;
    private static final Duration RATE_LIMIT_REFILL_DURATION = Duration.ofMinutes(1);
    
    private final UserService userService;
    private final Bucket bucket;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        Bandwidth limit = Bandwidth.classic(RATE_LIMIT_CAPACITY, Refill.greedy(RATE_LIMIT_CAPACITY, RATE_LIMIT_REFILL_DURATION));
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @GetMapping
    @Cacheable(value = "users", key = "#root.method.name + '_' + authentication?.name", condition = "#result != null")
    public ResponseEntity<User> getUser() {
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        User user = userService.getUser();
        return ResponseEntity.ok()
                .header(RATE_LIMIT_REMAINING_HEADER, String.valueOf(bucket.getAvailableTokens()))
                .body(user);
    }

    @PostMapping
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(RATE_LIMIT_REMAINING_HEADER, String.valueOf(bucket.getAvailableTokens()))
                .body(createdUser);
    }

    @PutMapping
    @CacheEvict(value = "users", key = "#root.method.name + '_' + authentication?.name")
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok()
                .header(RATE_LIMIT_REMAINING_HEADER, String.valueOf(bucket.getAvailableTokens()))
                .body(updatedUser);
    }

    @DeleteMapping("/delete")
    @CacheEvict(value = "users", allEntries = true)
    public ResponseEntity<HttpStatus> deleteUser() {
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        
        userService.deleteUser();
        return ResponseEntity.noContent()
                .header(RATE_LIMIT_REMAINING_HEADER, String.valueOf(bucket.getAvailableTokens()))
                .build();
    }
}
