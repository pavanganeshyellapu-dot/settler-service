package com.settler.domain.groupbalances.controller;

import com.settler.domain.groupbalances.dto.response.SettlementResponse;
import com.settler.domain.groupbalances.entity.GroupBalance;
import com.settler.domain.groupbalances.service.IGroupBalanceService;
import com.settler.dto.common.ApiResponse;
import com.settler.dto.common.ResponseBodyWrapper;
import com.settler.dto.common.ResponseInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/group-balances")
@RequiredArgsConstructor
@Slf4j
public class GroupBalanceController {

    private final IGroupBalanceService groupBalanceService;

    /**
     * ✅ Fetch all balances for a group
     */
    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<Object>> getGroupBalances(
            @PathVariable UUID groupId,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {

        if (correlationId == null) correlationId = UUID.randomUUID().toString();
        log.info("[{}] Fetching balances for group {}", correlationId, groupId);

        List<GroupBalance> balances = groupBalanceService.getGroupBalances(groupId, correlationId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched group balances successfully")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(balances)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Get suggested settlements (who owes whom)
     */
    @GetMapping("/group/{groupId}/settlements")
    public ResponseEntity<ApiResponse<Object>> getSuggestedSettlements(
            @PathVariable UUID groupId,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {

        if (correlationId == null) correlationId = UUID.randomUUID().toString();
        log.info("[{}] Fetching settlement suggestions for group {}", correlationId, groupId);

        List<SettlementResponse> settlements = groupBalanceService.calculateSettlements(groupId, correlationId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Settlement suggestions generated")
                        .build())
                .body(ResponseBodyWrapper.<Object>builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(settlements)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

}
