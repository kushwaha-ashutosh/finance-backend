package com.finance;

import com.finance.enums.TransactionType;
import com.finance.repository.TransactionRepository;
import com.finance.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void shouldReturnCorrectSummary() {
        when(transactionRepository.sumByType(TransactionType.INCOME)).thenReturn(10000.0);
        when(transactionRepository.sumByType(TransactionType.EXPENSE)).thenReturn(4000.0);
        when(transactionRepository.getCategoryWiseTotals()).thenReturn(List.of());
        when(transactionRepository.getMonthlyTrends()).thenReturn(List.of());
        when(transactionRepository.findRecentActivity(any(Pageable.class))).thenReturn(List.of());

        Map<String, Object> summary = dashboardService.getSummary();

        assertEquals(10000.0, summary.get("totalIncome"));
        assertEquals(4000.0, summary.get("totalExpense"));
        assertEquals(6000.0, summary.get("netBalance"));
    }

    @Test
    void shouldReturnZeroWhenNoData() {
        when(transactionRepository.sumByType(any())).thenReturn(null);
        when(transactionRepository.getCategoryWiseTotals()).thenReturn(List.of());
        when(transactionRepository.getMonthlyTrends()).thenReturn(List.of());
        when(transactionRepository.findRecentActivity(any(Pageable.class))).thenReturn(List.of());

        Map<String, Object> summary = dashboardService.getSummary();

        assertEquals(0.0, summary.get("totalIncome"));
        assertEquals(0.0, summary.get("totalExpense"));
        assertEquals(0.0, summary.get("netBalance"));
    }

    @Test
    void shouldContainAllRequiredKeys() {
        when(transactionRepository.sumByType(any())).thenReturn(0.0);
        when(transactionRepository.getCategoryWiseTotals()).thenReturn(List.of());
        when(transactionRepository.getMonthlyTrends()).thenReturn(List.of());
        when(transactionRepository.findRecentActivity(any(Pageable.class))).thenReturn(List.of());

        Map<String, Object> summary = dashboardService.getSummary();

        assertTrue(summary.containsKey("totalIncome"));
        assertTrue(summary.containsKey("totalExpense"));
        assertTrue(summary.containsKey("netBalance"));
        assertTrue(summary.containsKey("categoryTotals"));
        assertTrue(summary.containsKey("monthlyTrends"));
        assertTrue(summary.containsKey("recentActivity"));
    }
}
