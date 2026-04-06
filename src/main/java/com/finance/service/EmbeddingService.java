package com.finance.service;

import com.finance.model.Transaction;
import com.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * EmbeddingService - Handles pgvector AI embedding operations.
 *
 * This service demonstrates how to:
 * 1. Generate text embeddings for transactions (pluggable - connect any AI provider)
 * 2. Store embeddings in pgvector column
 * 3. Perform semantic similarity search using cosine distance (<->)
 *
 * To use with a real AI provider (e.g. OpenAI):
 *   - Add spring-ai-openai or call OpenAI REST API
 *   - Replace generateMockEmbedding() with real API call
 *   - Set OPENAI_API_KEY in environment
 */
@Service
public class EmbeddingService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Generate and store an embedding for a transaction.
     * The embedding is built from category + type + notes text.
     */
    public void generateAndStoreEmbedding(Transaction transaction) {
        String text = buildTextForEmbedding(transaction);
        float[] embedding = generateMockEmbedding(text);
        transaction.setEmbedding(embedding);
        transactionRepository.save(transaction);
    }

    /**
     * Semantic search: find transactions similar to a query string.
     * Uses pgvector cosine distance operator (<->)
     */
    public List<Transaction> semanticSearch(String query, int topK) {
        float[] queryEmbedding = generateMockEmbedding(query);
        String vectorString = toVectorString(queryEmbedding);
        return transactionRepository.findSimilarByEmbedding(vectorString, topK);
    }

    /**
     * Batch generate embeddings for all transactions that don't have one yet.
     */
    public int batchGenerateEmbeddings() {
        List<Transaction> missing = transactionRepository.findTransactionsWithoutEmbeddings();
        for (Transaction t : missing) {
            generateAndStoreEmbedding(t);
        }
        return missing.size();
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------

    private String buildTextForEmbedding(Transaction t) {
        return String.join(" ",
                t.getCategory() != null ? t.getCategory() : "",
                t.getType() != null ? t.getType().name() : "",
                t.getNotes() != null ? t.getNotes() : ""
        ).trim();
    }

    /**
     * Mock embedding generator - produces a deterministic 8-dimensional vector.
     * Replace with real OpenAI / HuggingFace / local model call for production.
     * Real OpenAI embeddings are 1536-dimensional.
     */
    private float[] generateMockEmbedding(String text) {
        int dimensions = 8; // Use 1536 for real OpenAI embeddings
        float[] embedding = new float[dimensions];
        int hash = text.hashCode();
        for (int i = 0; i < dimensions; i++) {
            embedding[i] = (float) Math.sin(hash * (i + 1)) * 0.5f + 0.5f;
        }
        return embedding;
    }

    /**
     * Converts float[] to PostgreSQL vector literal string e.g. "[0.1,0.2,0.3]"
     */
    private String toVectorString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(String.format("%.6f", embedding[i]));
            if (i < embedding.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
