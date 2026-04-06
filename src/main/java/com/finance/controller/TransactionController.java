package com.finance.controller;

import com.finance.dto.*;
import com.finance.enums.TransactionType;
import com.finance.model.Transaction;
import com.finance.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Transaction>> create(
            @Valid @RequestBody TransactionDTO dto,
            Authentication authentication) {
        Transaction created = transactionService.create(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Transaction created successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) LocalDate from,
            @RequestParam(required = false) LocalDate to) {
        List<Transaction> transactions = transactionService.getAll(category, type, from, to);
        return ResponseEntity.ok(ApiResponse.success(transactions, "Transactions retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<Transaction>> getById(@PathVariable Long id) {
        Transaction transaction = transactionService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction retrieved successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Transaction>> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO dto) {
        Transaction updated = transactionService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Transaction updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Long id) {
        transactionService.softDelete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Transaction deleted successfully"));
    }

    // pgvector semantic search endpoint
    @GetMapping("/search/semantic")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<ApiResponse<List<Transaction>>> semanticSearch(
            @RequestParam String query,
            @RequestParam(defaultValue = "5") int topK) {
        List<Transaction> results = transactionService.semanticSearch(query, topK);
        return ResponseEntity.ok(ApiResponse.success(results,
                "Semantic search completed. Found " + results.size() + " results."));
    }
}
