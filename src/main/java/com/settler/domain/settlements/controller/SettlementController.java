package com.settler.domain.settlements.controller;

import com.settler.domain.settlements.entity.Settlement;
import com.settler.domain.settlements.service.ISettlementService;
import com.settler.dto.common.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements")
public class SettlementController {

    private final ISettlementService settlementService;

    public SettlementController(ISettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createSettlement(@RequestBody Settlement settlement) {
        Settlement saved = settlementService.createSettlement(settlement);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Settlement created successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(saved)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<ApiResponse<Object>> confirmSettlement(@PathVariable UUID id) {
        Settlement updated = settlementService.confirmSettlement(id);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Settlement confirmed")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(updated)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<Object>> getGroupSettlements(@PathVariable UUID groupId) {
        List<Settlement> list = settlementService.getSettlementsByGroup(groupId);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched group settlements successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(list)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUserSettlements(@PathVariable UUID userId) {
        List<Settlement> list = settlementService.getUserSettlements(userId);

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched user settlements successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(list)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
