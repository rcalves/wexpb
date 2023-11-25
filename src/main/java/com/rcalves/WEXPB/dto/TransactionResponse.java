package com.rcalves.WEXPB.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class TransactionResponse {
    private Long id;
    private String description;
    private LocalDate transactionDate;
    private BigDecimal originalAmount;
    private BigDecimal exchangeRate;
    private BigDecimal convertedAmount;
    private String currency;
}
