package com.settler.domain.groupbalances.controller;

import com.settler.domain.groupbalances.dto.response.GroupBalanceResponse;
import com.settler.domain.groupbalances.dto.response.GroupBalanceSummaryResponse;
import com.settler.domain.groupbalances.dto.response.GroupSettlementSuggestionResponse;
import com.settler.domain.groupbalances.entity.GroupBalance;
import com.settler.domain.groupbalances.service.IGroupBalanceService;
import com.settler.domain.settlements.dto.SettlementResponse;
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

    @GetMapping("/group/{groupId}")
    public ResponseEntity<ApiResponse<Object>> getGroupBalances(
            @PathVariable UUID groupId,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {

        if (correlationId == null) correlationId = UUID.randomUUID().toString();

        List<GroupBalanceResponse> balances =
                groupBalanceService.getGroupBalances(groupId, correlationId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Fetched group balances successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(balances)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}/settlements")
    public ResponseEntity<ApiResponse<Object>> getSuggestedSettlements(
            @PathVariable UUID groupId,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        List<GroupSettlementSuggestionResponse> settlements =
                groupBalanceService.calculateSettlements(groupId, correlationId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Settlement suggestions generated successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(settlements)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/group/{groupId}/summary")
    public ResponseEntity<ApiResponse<Object>> getGroupSummary(
            @PathVariable UUID groupId,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {

        if (correlationId == null) correlationId = UUID.randomUUID().toString();

        GroupBalanceSummaryResponse summary =
                groupBalanceService.getGroupSummary(groupId, correlationId);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Group summary fetched successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("OK")
                        .data(summary)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }


}


