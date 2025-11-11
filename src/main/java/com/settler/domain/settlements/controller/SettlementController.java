package com.settler.domain.settlements.controller;

import com.settler.domain.settlements.dto.SettlementRequest;
import com.settler.domain.settlements.dto.SettlementResponse;
import com.settler.domain.settlements.service.ISettlementService;
import com.settler.dto.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settlements")
@RequiredArgsConstructor
@Slf4j
public class SettlementController {

    private final ISettlementService settlementService;

    /**
     * ✅ Create and immediately confirm a new settlement.
     * This records the transaction and updates group balances.
     */
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Object>> confirmSettlement(
            @RequestBody SettlementRequest request,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {

        if (correlationId == null)
            correlationId = UUID.randomUUID().toString();

        log.info("[{}] Confirming settlement request: {}", correlationId, request);

        SettlementResponse saved = settlementService.confirmSettlement(request, correlationId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Settlement confirmed successfully")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(saved)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Fetch all settlements for a group
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<Object>> getGroupSettlements(@PathVariable UUID groupId) {
        List<SettlementResponse> list = settlementService.getSettlementsByGroup(groupId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched group settlements successfully")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(list)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Fetch all settlements involving a specific user (as payer or receiver)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Object>> getUserSettlements(@PathVariable UUID userId) {
        List<SettlementResponse> list = settlementService.getUserSettlements(userId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched user settlements successfully")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(list)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
