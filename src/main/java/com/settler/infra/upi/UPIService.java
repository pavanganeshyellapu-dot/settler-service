package com.settler.infra.upi;

import com.settler.dto.common.ApiResponse;
import com.settler.dto.common.ResponseBodyWrapper;
import com.settler.dto.common.ResponseInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class UPIService {

    public ResponseEntity<ApiResponse<Object>> processPayment(String upiId, Double amount, String mode) {
        String transactionId = UUID.randomUUID().toString();

        Map<String, Object> paymentInfo = Map.of(
                "transactionId", transactionId,
                "upiId", upiId,
                "amount", amount,
                "mode", mode,
                "status", "SUCCESS",
                "timestamp", OffsetDateTime.now().toString()
        );

        ApiResponse<Object> response = ApiResponse.builder()
                .responseInfo(ResponseInfo.builder()
                        .timestamp(OffsetDateTime.now())
                        .responseCode("00")
                        .responseMessage("Payment processed successfully")
                        .build())
                .body(ResponseBodyWrapper.builder()
                        .statusCode("200")
                        .statusMessage("Success")
                        .data(paymentInfo)
                        .build())
                .build();

        return ResponseEntity.ok(response);
    }
}
