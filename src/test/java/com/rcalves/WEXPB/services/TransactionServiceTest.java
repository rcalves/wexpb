package com.rcalves.WEXPB.services;

import com.rcalves.WEXPB.dto.TransactionForm;
import com.rcalves.WEXPB.dto.TransactionResponse;
import com.rcalves.WEXPB.entities.Transaction;
import com.rcalves.WEXPB.exceptions.BusinessException;
import com.rcalves.WEXPB.repositories.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CurrencyExchangeService currencyExchangeService;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;
    private TransactionForm transactionForm;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setDescription("Sample Transaction");
        transaction.setTransactionDate(LocalDate.now());
        transaction.setPurchaseAmount(new BigDecimal("100.00"));

        transactionForm = new TransactionForm();
        transactionForm.setDescription("Sample Transaction");
        transactionForm.setTransactionDate(LocalDate.now());
        transactionForm.setPurchaseAmount(new BigDecimal("100.00"));

        transactionResponse = new TransactionResponse(1L,
                "Sample Transaction",
                LocalDate.now(),
                new BigDecimal("100.00"),
                BigDecimal.ONE,
                new BigDecimal("100.00"),
                "USD");
    }

    @Test
    void getAllTransactions_ShouldReturnTransactions() {
        when(transactionRepository.findAll()).thenReturn(Collections.singletonList(transaction));
        var result = transactionService.getAllTransactions();
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getTransactionById_ShouldReturnTransaction() {
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        var result = transactionService.getTransactionById(1L);
        assertNotNull(result);
        assertEquals("Sample Transaction", result.getDescription());
    }

    @Test
    void getTransactionById_ShouldThrowException_WhenNotFound() {
        when(transactionRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> transactionService.getTransactionById(1L));
    }

    @Test
    void getTransactionByIdAndCurrency_ShouldReturnConvertedTransaction() {
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        when(currencyExchangeService.getExchangeRate(anyString(), any(LocalDate.class))).thenReturn(BigDecimal.ONE);
        when(currencyExchangeService.convertCurrency(any(BigDecimal.class), any(BigDecimal.class))).thenReturn(new BigDecimal("100.00"));

        var result = transactionService.getTransactionByIdAndCurrency(1L, "EUR");
        assertNotNull(result);
        assertEquals("EUR", result.getCurrency());
    }

    @Test
    void insertTransaction_ShouldReturnTransactionResponse() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        var result = transactionService.insertTransaction(transactionForm);
        assertNotNull(result);
        assertEquals("Sample Transaction", result.getDescription());
    }

    @Test
    void updateTransaction_ShouldReturnUpdatedTransactionResponse() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        var result = transactionService.updateTransaction(1L, transactionForm);
        assertNotNull(result);
        assertEquals("Sample Transaction", result.getDescription());
    }

    @Test
    void deleteTransaction_ShouldCallDeleteMethod() {
        doNothing().when(transactionRepository).deleteById(anyLong());
        transactionService.deleteTransaction(1L);
        verify(transactionRepository, times(1)).deleteById(1L);
    }
}
