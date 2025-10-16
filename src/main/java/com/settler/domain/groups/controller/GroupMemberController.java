package com.settler.domain.groups.controller;

import com.settler.domain.groups.service.IGroupMemberService;
import com.settler.dto.common.ApiResponse;
import com.settler.dto.common.ResponseBodyWrapper;
import com.settler.dto.common.ResponseInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/members")
@RequiredArgsConstructor
public class GroupMemberController {

    private final IGroupMemberService groupMemberService;

    /**
     * ➕ Add member to group
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Object>> addMember(
            @PathVariable UUID groupId,
            @RequestBody Map<String, Object> req,
            Authentication authentication) {

        UUID userId = UUID.fromString((String) req.get("userId"));
        String addedByEmail = authentication.getName();

        groupMemberService.addMember(groupId, userId, addedByEmail);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Member added successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(null)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ❌ Remove member from group
     */
    @DeleteMapping("/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Object>> removeMember(
            @PathVariable UUID groupId,
            @PathVariable UUID memberId,
            Authentication authentication) {

        String removedByEmail = authentication.getName();

        groupMemberService.removeMember(groupId, memberId, removedByEmail);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Member removed successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Deleted")
                        .data(null)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * 📋 List all members in the group
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Object>> listMembers(@PathVariable UUID groupId) {

        // ✅ directly fetch from service
        List<Map<String, Object>> members = groupMemberService.getMembers(groupId);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched members successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(members)  // ✅ flat list (no extra wrapping)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

}
