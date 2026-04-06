package com.finance.service;

import com.finance.dto.TransactionDTO;
import com.finance.enums.TransactionType;
import com.finance.exception.ResourceNotFoundException;
import com.finance.model.*;
import com.finance.repository.TransactionRepository;
import com.finance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmbeddingService embeddingService;

    public Transaction create(TransactionDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Transaction transaction = Transaction.builder()
                .amount(dto.getAmount())
                .type(dto.getType())
                .category(dto.getCategory().trim())
                .date(dto.getDate())
                .notes(dto.getNotes())
                .createdBy(user)
                .isDeleted(false)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        // Asynchronously generate and store pgvector embedding
        try {
            embeddingService.generateAndStoreEmbedding(saved);
        } catch (Exception e) {
            // Embedding failure must not block transaction creation
        }

        return saved;
    }

    public List<Transaction> getAll(String category,
                                     TransactionType type,
                                     LocalDate from,
                                     LocalDate to) {
        if (category != null && !category.isBlank()) {
            return transactionRepository
                    .findByCategoryIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(category.trim());
        }
        if (type != null) {
            return transactionRepository
                    .findByTypeAndIsDeletedFalseOrderByCreatedAtDesc(type);
        }
        if (from != null && to != null) {
            if (from.isAfter(to)) {
                throw new IllegalArgumentException("'from' date cannot be after 'to' date");
            }
            return transactionRepository
                    .findByDateBetweenAndIsDeletedFalseOrderByDateDesc(from, to);
        }
        return transactionRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
    }

    public Transaction getById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));
        if (transaction.getIsDeleted()) {
            throw new ResourceNotFoundException("Transaction not found with id: " + id);
        }
        return transaction;
    }

    public Transaction update(Long id, TransactionDTO dto) {
        Transaction transaction = getById(id);
        transaction.setAmount(dto.getAmount());
        transaction.setType(dto.getType());
        transaction.setCategory(dto.getCategory().trim());
        transaction.setDate(dto.getDate());
        transaction.setNotes(dto.getNotes());
        Transaction updated = transactionRepository.save(transaction);

        // Regenerate embedding after update
        try {
            embeddingService.generateAndStoreEmbedding(updated);
        } catch (Exception e) {
            // Non-blocking
        }

        return updated;
    }

    public void softDelete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transaction not found with id: " + id));
        if (transaction.getIsDeleted()) {
            throw new IllegalArgumentException("Transaction already deleted with id: " + id);
        }
        transaction.setIsDeleted(true);
        transactionRepository.save(transaction);
    }

    public List<Transaction> semanticSearch(String query, int topK) {
        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        return embeddingService.semanticSearch(query, topK > 0 ? topK : 5);
    }
}
