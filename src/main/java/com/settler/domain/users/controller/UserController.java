package com.settler.domain.users.controller;

import com.settler.domain.users.entity.User;
import com.settler.domain.users.service.IUserService;
import com.settler.dto.common.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createUser(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String displayName = body.get("displayName");

        User user = userService.createUser(email, displayName);

        ApiResponse<Object> response = ApiResponse.builder()
            .responseInfo(ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .responseCode("00")
                .responseMessage("User created successfully")
                .build())
            .body(ResponseBodyWrapper.builder()
                .statusCode("200")
                .statusMessage("Success")
                .data(user)
                .build())
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getUser(@PathVariable UUID id) {
        User user = userService.getUserById(id);

        ApiResponse<Object> response = ApiResponse.builder()
            .responseInfo(ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .responseCode("00")
                .responseMessage("Success")
                .build())
            .body(ResponseBodyWrapper.builder()
                .statusCode("200")
                .statusMessage("OK")
                .data(user)
                .build())
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> listUsers() {
        List<User> users = userService.getAllUsers();

        ApiResponse<Object> response = ApiResponse.builder()
            .responseInfo(ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .responseCode("00")
                .responseMessage("Success")
                .build())
            .body(ResponseBodyWrapper.builder()
                .statusCode("200")
                .statusMessage("Fetched all users")
                .data(users)
                .build())
            .build();

        return ResponseEntity.ok(response);
    }
}
