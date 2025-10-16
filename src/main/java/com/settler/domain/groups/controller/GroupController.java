package com.settler.domain.groups.controller;

import com.settler.domain.groups.entity.Group;
import com.settler.domain.groups.service.IGroupService;
import com.settler.domain.groups.mapper.GroupMapper;
import com.settler.domain.groups.dto.GroupResponse;
import com.settler.dto.common.ApiResponse;
import com.settler.dto.common.ResponseBodyWrapper;
import com.settler.dto.common.ResponseInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final IGroupService groupService;

    /** 🆕 Create a new group **/
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Object>> createGroup(@RequestBody Map<String, Object> req) {
        String name = (String) req.get("name");
        String currencyCode = (String) req.get("currencyCode");
        UUID ownerId = UUID.fromString((String) req.get("ownerId"));

        Group group = groupService.createGroup(name, currencyCode, ownerId);
        GroupResponse dto = GroupMapper.toDto(group);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Group created successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(dto)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /** 📋 Get all groups for a specific user **/
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Object>> getGroupsByUser(@PathVariable UUID userId) {
        List<Group> groups = groupService.getGroupsByUser(userId);
        List<GroupResponse> dtos = GroupMapper.toDtoList(groups);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched user's groups successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(dtos)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /** 🔍 Get a single group by ID **/
    @GetMapping("/{groupId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Object>> getGroupById(@PathVariable UUID groupId) {
        Group group = groupService.getGroupById(groupId);
        GroupResponse dto = GroupMapper.toDto(group);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Group fetched successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(dto)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /** 👤 Get all groups for the logged-in user **/
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Object>> getMyGroups(Authentication authentication) {
        String email = authentication.getName();
        List<Group> groups = groupService.getGroupsByUserEmail(email);
        List<GroupResponse> dtos = GroupMapper.toDtoList(groups);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched my groups successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(dtos)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /** 🗑️ Delete a group (admin only) **/
    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteGroup(@PathVariable UUID groupId) {
        groupService.deleteGroup(groupId);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Group deleted successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Deleted")
                        .data(null)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
