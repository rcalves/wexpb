package com.rcalves.wexpb.services;

import com.rcalves.wexpb.dto.TransactionForm;
import com.rcalves.wexpb.dto.TransactionResponse;
import com.rcalves.wexpb.entities.Transaction;
import com.rcalves.wexpb.exceptions.BusinessException;
import com.rcalves.wexpb.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final CurrencyExchangeService currencyExchangeService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              CurrencyExchangeService currencyExchangeService) {
        this.transactionRepository = transactionRepository;
        this.currencyExchangeService = currencyExchangeService;
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToTransactionResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Transaction not found with id: " + id));
        return convertToTransactionResponse(transaction);
    }

    public TransactionResponse getTransactionByIdAndCurrency(Long id, String currency) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Transaction not found with id: " + id));
        BigDecimal exchangeRate = currencyExchangeService.getExchangeRate(currency, transaction.getTransactionDate());
        BigDecimal convertedAmount = currencyExchangeService.convertCurrency(transaction.getPurchaseAmount(), exchangeRate);
        return convertToTransactionResponse(transaction, exchangeRate, convertedAmount, currency);
    }

    public TransactionResponse insertTransaction(TransactionForm transactionForm) {
        Transaction transaction = convertToTransactionEntity(transactionForm);
        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    public TransactionResponse updateTransaction(Long id, TransactionForm transactionForm) {
        Transaction transaction = convertToTransactionEntity(transactionForm);
        transaction.setId(id);
        return convertToTransactionResponse(transactionRepository.save(transaction));
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    private TransactionResponse convertToTransactionResponse(Transaction transaction) {
        return convertToTransactionResponse(transaction, BigDecimal.ONE, transaction.getPurchaseAmount(), "USD");
    }

    private TransactionResponse convertToTransactionResponse(Transaction transaction, BigDecimal exchangeRate, BigDecimal convertedAmount, String currency) {
        return new TransactionResponse(transaction.getId(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getPurchaseAmount(),
                exchangeRate,
                convertedAmount,
                currency);
    }

    private Transaction convertToTransactionEntity(TransactionForm transactionForm) {
        return new Transaction().builder()
                .description(transactionForm.getDescription())
                .transactionDate(transactionForm.getTransactionDate())
                .purchaseAmount(transactionForm.getPurchaseAmount())
                .build();
    }
}
