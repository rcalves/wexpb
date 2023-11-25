package com.rcalves.wexpb.controllers;

import com.rcalves.wexpb.dto.TransactionForm;
import com.rcalves.wexpb.dto.TransactionResponse;
import com.rcalves.wexpb.exceptions.BusinessException;
import com.rcalves.wexpb.services.CurrencyExchangeService;
import com.rcalves.wexpb.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
public class HomeController {

    private final TransactionService transactionService;
    private final CurrencyExchangeService currencyExchangeService;

    @Autowired
    public HomeController(TransactionService transactionService, CurrencyExchangeService currencyExchangeService) {
        this.transactionService = transactionService;
        this.currencyExchangeService = currencyExchangeService;
    }

    @GetMapping("/")
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionForm transactionForm) {
        return new ResponseEntity<>(transactionService.insertTransaction(transactionForm), HttpStatus.CREATED);
    }

    @PutMapping("/transaction/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionForm transactionForm) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionForm));
    }

    @DeleteMapping("/transaction/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transaction/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/transaction/{id}/{currency}")
    public ResponseEntity<TransactionResponse> getTransactionByIdAndCurrency(@PathVariable Long id, @PathVariable String currency) {
        return ResponseEntity.ok(transactionService.getTransactionByIdAndCurrency(id, currency));
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getSupportedCurrencies() {
        return ResponseEntity.ok(currencyExchangeService.getSupportedCurrencies());
    }

    @GetMapping("/exchange-rate")
    public ResponseEntity<BigDecimal> getExchangeRate(@RequestParam String currencyCode, @RequestParam LocalDate date) {
        return ResponseEntity.ok(currencyExchangeService.getExchangeRate(currencyCode, date));
    }
}
