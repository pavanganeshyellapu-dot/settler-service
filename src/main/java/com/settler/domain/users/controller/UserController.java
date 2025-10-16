package com.settler.domain.users.controller;

import com.settler.domain.users.dto.UserResponse;
import com.settler.domain.users.entity.User;
import com.settler.domain.users.service.IUserService;
import com.settler.dto.common.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    /** 👥 ADMIN can view all users **/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();

        ApiResponse<List<UserResponse>> response = ApiResponse.<List<UserResponse>>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Success")
                        .build())
                .body(ResponseBodyWrapper.<List<UserResponse>>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(users)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }


    /** 🧑‍🔍 USER can view their own profile **/
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);

        return getApiResponseResponseEntity(user);
    }

    private ResponseEntity<ApiResponse<UserResponse>> getApiResponseResponseEntity(UserResponse user) {
        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Success")
                        .build())
                .body(ResponseBodyWrapper.<UserResponse>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(user)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /** 👤 Get current logged-in user (from JWT) **/
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserResponse user = userService.getUserByEmail(email);

        return getApiResponseResponseEntity(user);
    }


}
