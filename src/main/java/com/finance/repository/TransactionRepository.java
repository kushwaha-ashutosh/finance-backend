package com.finance.repository;

import com.finance.enums.TransactionType;
import com.finance.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByIsDeletedFalseOrderByCreatedAtDesc();

    List<Transaction> findByTypeAndIsDeletedFalseOrderByCreatedAtDesc(TransactionType type);

    List<Transaction> findByCategoryIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(String category);

    List<Transaction> findByDateBetweenAndIsDeletedFalseOrderByDateDesc(LocalDate from, LocalDate to);

    Page<Transaction> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.type = :type AND t.isDeleted = false")
    Double sumByType(@Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.isDeleted = false ORDER BY t.createdAt DESC")
    List<Transaction> findRecentActivity(Pageable pageable);

    // PostgreSQL native query for category totals
    @Query(value = "SELECT category, SUM(amount) as total " +
                   "FROM transactions WHERE is_deleted = false " +
                   "GROUP BY category ORDER BY total DESC",
           nativeQuery = true)
    List<Object[]> getCategoryWiseTotals();

    // PostgreSQL TO_CHAR for monthly grouping
    @Query(value = "SELECT TO_CHAR(date, 'YYYY-MM') as month, SUM(amount) as total " +
                   "FROM transactions WHERE is_deleted = false " +
                   "GROUP BY TO_CHAR(date, 'YYYY-MM') " +
                   "ORDER BY month DESC",
           nativeQuery = true)
    List<Object[]> getMonthlyTrends();

    // pgvector cosine similarity search
    // Finds transactions semantically similar to the given embedding
    @Query(value = "SELECT * FROM transactions " +
            "WHERE is_deleted = false AND embedding IS NOT NULL " +
            "ORDER BY embedding <-> CAST(:queryVector AS vector) " +
            "LIMIT :topK",
            nativeQuery = true)
    List<Transaction> findSimilarByEmbedding(
            @Param("queryVector") String queryVector,
            @Param("topK") int topK);

    // Find transactions missing embeddings (for batch embedding generation)
    @Query("SELECT t FROM Transaction t WHERE t.embedding IS NULL AND t.isDeleted = false")
    List<Transaction> findTransactionsWithoutEmbeddings();
}
