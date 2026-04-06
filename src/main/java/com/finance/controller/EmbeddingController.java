package com.finance.controller;

import com.finance.dto.ApiResponse;
import com.finance.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/embeddings")
public class EmbeddingController {

    @Autowired
    private EmbeddingService embeddingService;

    /**
     * Trigger batch embedding generation for all transactions
     * that don't have an embedding yet. ADMIN only.
     */
    @PostMapping("/generate-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> generateAll() {
        int count = embeddingService.batchGenerateEmbeddings();
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("processed", count),
                count + " transactions had embeddings generated."));
    }
}
