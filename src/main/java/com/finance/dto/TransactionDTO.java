package com.finance.dto;

import com.finance.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TransactionDTO {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Type is required (INCOME or EXPENSE)")
    private TransactionType type;

    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
