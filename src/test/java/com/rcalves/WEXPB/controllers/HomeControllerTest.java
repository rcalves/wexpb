package com.rcalves.WEXPB.controllers;

import com.rcalves.WEXPB.dto.TransactionForm;
import com.rcalves.WEXPB.dto.TransactionResponse;
import com.rcalves.WEXPB.services.CurrencyExchangeService;
import com.rcalves.WEXPB.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private CurrencyExchangeService currencyExchangeService;

    private TransactionForm transactionForm;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {
        transactionForm = new TransactionForm();
        transactionForm.setDescription("Test Description");
        transactionForm.setTransactionDate(LocalDate.now());
        transactionForm.setPurchaseAmount(new BigDecimal("100.00"));

        transactionResponse = new TransactionResponse();
    }

    @Test
    void getAllTransactions_ShouldReturnTransactionsList() throws Exception {
        List<TransactionResponse> allTransactions = Arrays.asList(transactionResponse);
        given(transactionService.getAllTransactions()).willReturn(allTransactions);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void createTransaction_ShouldCreateTransaction() throws Exception {
        given(transactionService.insertTransaction(transactionForm)).willReturn(transactionResponse);

        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Test Description\",\"transactionDate\":\"2023-01-01\",\"purchaseAmount\":100.00}"))
                .andExpect(status().isCreated());
    }

    @Test
    void updateTransaction_ShouldUpdateTransaction() throws Exception {
        given(transactionService.updateTransaction(1L, transactionForm)).willReturn(transactionResponse);

        mockMvc.perform(put("/transaction/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Test Description\",\"transactionDate\":\"2023-01-01\",\"purchaseAmount\":100.00}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTransaction_ShouldDeleteTransaction() throws Exception {
        doNothing().when(transactionService).deleteTransaction(1L);

        mockMvc.perform(delete("/transaction/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionById_ShouldReturnTransaction() throws Exception {
        given(transactionService.getTransactionById(1L)).willReturn(transactionResponse);

        mockMvc.perform(get("/transaction/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getTransactionByIdAndCurrency_ShouldReturnTransactionWithCurrency() throws Exception {
        given(transactionService.getTransactionByIdAndCurrency(1L, "USD")).willReturn(transactionResponse);

        mockMvc.perform(get("/transaction/{id}/{currency}", 1L, "USD"))
                .andExpect(status().isOk());
    }

    @Test
    void getSupportedCurrencies_ShouldReturnValidCurrencies() throws Exception {
        List<String> supportedCurrencies = Arrays.asList("Brazil-Real", "Canada-Dollar", "Euro Zone-Euro", "France-Euro");
        given(currencyExchangeService.getSupportedCurrencies()).willReturn(supportedCurrencies);

        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0]", is("Brazil-Real")))
                .andExpect(jsonPath("$[1]", is("Canada-Dollar")))
                .andExpect(jsonPath("$[2]", is("Euro Zone-Euro")))
                .andExpect(jsonPath("$[3]", is("France-Euro")));
    }

    @Test
    void getExchangeRate_ShouldReturnExchangeRate() throws Exception {
        BigDecimal exchangeRate = new BigDecimal("1.25");
        given(currencyExchangeService.getExchangeRate("EUR", LocalDate.of(2023, 1, 1))).willReturn(exchangeRate);

        mockMvc.perform(get("/exchange-rate")
                        .param("currencyCode", "EUR")
                        .param("date", "2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().string("1.25"));
    }
}
