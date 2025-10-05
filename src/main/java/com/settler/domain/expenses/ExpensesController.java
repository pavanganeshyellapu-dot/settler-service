package com.settler.domain.expenses;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.settler.expenses.dto.AddEqualExpenseRequest;

@RestController
@RequestMapping("/groups/{groupId}/expenses")
public class ExpensesController {	

	private final ExpenseService service;
	private final ExpenseRepository expenses;
	private final ExpenseSplitRepository splits;

	public ExpensesController(ExpenseService service, ExpenseRepository expenses, ExpenseSplitRepository splits) {
		this.service = service;
		this.expenses = expenses;
		this.splits = splits;
	}

	@PostMapping("/equal")
	public ResponseEntity<?> addEqual(@PathVariable UUID groupId, @RequestBody AddEqualExpenseRequest req) {
		if (req.getPaidBy() == null || req.getAmountPaise() <= 0 || req.getParticipantUserIds() == null
				|| req.getParticipantUserIds().isEmpty())
			return ResponseEntity.badRequest().body(Map.of("error", "invalid_input"));

		UUID id = service.addEqualExpense(groupId, req.getDescription(), req.getAmountPaise(), req.getPaidBy(),
				req.getParticipantUserIds(), req.getOccurredAt() != null ? req.getOccurredAt() : OffsetDateTime.now());
		return ResponseEntity.ok(Map.of("expenseId", id));
	}

	@GetMapping
	public ResponseEntity<?> list(@PathVariable UUID groupId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		return ResponseEntity.ok(expenses.findByGroupIdOrderByOccurredAtDesc(groupId, PageRequest.of(page, size)));
	}

	@GetMapping("/{expenseId}/splits")
	public ResponseEntity<?> splits(@PathVariable UUID expenseId) {
		return ResponseEntity.ok(this.splits.findByExpenseId(expenseId));
	}
}
