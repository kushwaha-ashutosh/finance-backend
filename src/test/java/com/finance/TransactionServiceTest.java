package com.finance;

import com.finance.dto.TransactionDTO;
import com.finance.enums.TransactionType;
import com.finance.exception.ResourceNotFoundException;
import com.finance.model.*;
import com.finance.repository.*;
import com.finance.service.TransactionService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private Transaction testTransaction;
    private TransactionDTO testDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).name("Test User")
                .email("test@test.com").build();

        testTransaction = Transaction.builder()
                .id(1L).amount(5000.0)
                .type(TransactionType.INCOME)
                .category("Salary")
                .date(LocalDate.now())
                .isDeleted(false)
                .createdBy(testUser).build();

        testDTO = new TransactionDTO();
        testDTO.setAmount(5000.0);
        testDTO.setType(TransactionType.INCOME);
        testDTO.setCategory("Salary");
        testDTO.setDate(LocalDate.now());
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(testUser));
        when(transactionRepository.save(any())).thenReturn(testTransaction);

        Transaction result = transactionService.create(testDTO, "test@test.com");

        assertNotNull(result);
        assertEquals(5000.0, result.getAmount());
        assertEquals(TransactionType.INCOME, result.getType());
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFoundOnCreate() {
        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                transactionService.create(testDTO, "unknown@test.com"));
    }

    @Test
    void shouldGetTransactionById() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));

        Transaction result = transactionService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenTransactionNotFound() {
        when(transactionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                transactionService.getById(99L));
    }

    @Test
    void shouldThrowWhenGettingDeletedTransaction() {
        testTransaction.setIsDeleted(true);
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));

        assertThrows(ResourceNotFoundException.class, () ->
                transactionService.getById(1L));
    }

    @Test
    void shouldSoftDeleteSuccessfully() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any())).thenReturn(testTransaction);

        transactionService.softDelete(1L);

        assertTrue(testTransaction.getIsDeleted());
        verify(transactionRepository, times(1)).save(testTransaction);
    }

    @Test
    void shouldThrowWhenDeletingAlreadyDeletedTransaction() {
        testTransaction.setIsDeleted(true);
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));

        assertThrows(IllegalArgumentException.class, () ->
                transactionService.softDelete(1L));
    }

    @Test
    void shouldUpdateTransactionSuccessfully() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any())).thenReturn(testTransaction);

        TransactionDTO updateDTO = new TransactionDTO();
        updateDTO.setAmount(7000.0);
        updateDTO.setType(TransactionType.INCOME);
        updateDTO.setCategory("Bonus");
        updateDTO.setDate(LocalDate.now());

        Transaction updated = transactionService.update(1L, updateDTO);

        assertNotNull(updated);
        verify(transactionRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenFromDateAfterToDate() {
        assertThrows(IllegalArgumentException.class, () ->
                transactionService.getAll(null, null,
                        LocalDate.now(), LocalDate.now().minusDays(1)));
    }
}
