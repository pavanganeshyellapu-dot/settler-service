package com.settler.domain.groups.controller;

import com.settler.domain.groups.entity.Group;
import com.settler.domain.groups.service.IGroupService;
import com.settler.dto.common.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final IGroupService groupService;

    public GroupController(IGroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createGroup(@RequestBody Map<String, Object> req) {
        String name = (String) req.get("name");
        String currencyCode = (String) req.get("currencyCode");
        UUID ownerId = UUID.fromString((String) req.get("ownerId"));

        Group group = groupService.createGroup(name, currencyCode, ownerId);

        ApiResponse<Object> response = ApiResponse.builder()
            .responseInfo(ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .responseCode("00")
                .responseMessage("Group created successfully")
                .build())
            .body(ResponseBodyWrapper.builder()
                .statusCode("200")
                .statusMessage("Success")
                .data(group)
                .build())
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getGroup(@PathVariable UUID id) {
        Group group = groupService.getGroupById(id);

        ApiResponse<Object> response = ApiResponse.builder()
            .responseInfo(ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .responseCode("00")
                .responseMessage("Success")
                .build())
            .body(ResponseBodyWrapper.builder()
                .statusCode("200")
                .statusMessage("Fetched group successfully")
                .data(group)
                .build())
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllGroups() {
        List<Group> groups = groupService.getAllGroups();

        ApiResponse<Object> response = ApiResponse.builder()
            .responseInfo(ResponseInfo.builder()
                .timestamp(OffsetDateTime.now())
                .responseCode("00")
                .responseMessage("Success")
                .build())
            .body(ResponseBodyWrapper.builder()
                .statusCode("200")
                .statusMessage("Fetched all groups")
                .data(groups)
                .build())
            .build();

        return ResponseEntity.ok(response);
    }
}
