package com.finance.service;

import com.finance.enums.TransactionType;
import com.finance.model.Transaction;
import com.finance.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, Object> getSummary() {
        double totalIncome = Optional.ofNullable(
                transactionRepository.sumByType(TransactionType.INCOME)).orElse(0.0);

        double totalExpense = Optional.ofNullable(
                transactionRepository.sumByType(TransactionType.EXPENSE)).orElse(0.0);

        double netBalance = totalIncome - totalExpense;

        // Category totals - uses PostgreSQL native GROUP BY
        List<Object[]> categoryRaw = transactionRepository.getCategoryWiseTotals();
        Map<String, Double> categoryTotals = new LinkedHashMap<>();
        for (Object[] row : categoryRaw) {
            categoryTotals.put((String) row[0], ((Number) row[1]).doubleValue());
        }

        // Monthly trends - uses PostgreSQL TO_CHAR date formatting
        List<Object[]> monthlyRaw = transactionRepository.getMonthlyTrends();
        Map<String, Double> monthlyTrends = new LinkedHashMap<>();
        for (Object[] row : monthlyRaw) {
            monthlyTrends.put((String) row[0], ((Number) row[1]).doubleValue());
        }

        // Recent 10 transactions
        List<Transaction> recentActivity = transactionRepository
                .findRecentActivity(PageRequest.of(0, 10));

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalIncome", totalIncome);
        summary.put("totalExpense", totalExpense);
        summary.put("netBalance", netBalance);
        summary.put("categoryTotals", categoryTotals);
        summary.put("monthlyTrends", monthlyTrends);
        summary.put("recentActivity", recentActivity);

        return summary;
    }
}
