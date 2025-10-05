package com.settler.infra.upi;

import com.settler.dto.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
public class UPIController {

    private final UPIService upiService;

    public UPIController(UPIService upiService) {
        this.upiService = upiService;
    }

    @PostMapping("/upi")
    public ResponseEntity<ApiResponse<Object>> makePayment(
            @RequestParam String upiId,
            @RequestParam Double amount,
            @RequestParam(defaultValue = "GPAY") String mode) {

        return upiService.processPayment(upiId, amount, mode);
    }
}
