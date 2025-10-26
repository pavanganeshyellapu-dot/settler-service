package com.settler.domain.groupbalances.controller;

import com.settler.domain.groupbalances.service.IGroupBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/group-balances")
public class GroupBalanceController {

    private final IGroupBalanceService groupBalanceService;

    public GroupBalanceController(IGroupBalanceService groupBalanceService) {
        this.groupBalanceService = groupBalanceService;
    }

    @PostMapping("/group/{groupId}/recalculate")
    public ResponseEntity<Void> recalcBalances(
            @PathVariable UUID groupId,
            @RequestHeader(value = "Correlation-Id", required = false) String correlationId) {
        groupBalanceService.recalculateBalances(groupId, correlationId);
        return ResponseEntity.ok().build();
    }
}
