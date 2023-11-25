package com.rcalves.wexpb.dto;

import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TransactionForm {
    @NotBlank(message = "Description is required")
    @Size(max = 50, message = "Description must not exceed 50 characters")
    private String description;

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    @NotNull(message = "Purchase amount is required")
    @DecimalMin(value = "0.01", message = "Purchase amount must be a valid positive amount")
    @Digits(integer = 10, fraction = 2, message = "Purchase amount must be a valid amount rounded to the nearest cent")
    private BigDecimal purchaseAmount;
}
