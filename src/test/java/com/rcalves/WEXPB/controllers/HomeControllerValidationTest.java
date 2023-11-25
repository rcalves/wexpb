package com.rcalves.WEXPB.controllers;

import com.rcalves.WEXPB.services.CurrencyExchangeService;
import com.rcalves.WEXPB.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = HomeController.class)
public class HomeControllerValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private CurrencyExchangeService currencyExchangeService;

    private String transactionJson;

    @BeforeEach
    void setUp() {
        // JSON representing a TransactionForm with some fields missing or invalid
        transactionJson = "{\"description\":\"\",\"transactionDate\":\"2023-25-11\",\"purchaseAmount\":\"-100.00\"}";
    }

    @Test
    void whenPostRequestToTransactionsAndInvalidData_thenCorrectResponse() throws Exception {
        mockMvc.perform(post("/transaction")
                        .content(transactionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid value for MonthOfYear")));
    }


    @Test
    void whenPostRequestToTransactionsAndValidData_thenCorrectResponse() throws Exception {
        transactionJson = "{\"description\":\"\",\"transactionDate\":\"2023-11-21T15:07:16.918\",\"purchaseAmount\":\"-100.00\"}";
        mockMvc.perform(post("/transaction")
                        .content(transactionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description is required"))
                .andExpect(jsonPath("$.purchaseAmount").value("Purchase amount must be a valid positive amount"));
    }

    @Test
    void whenPostRequestToTransactionsAndNoData_thenCorrectResponse() throws Exception {
        mockMvc.perform(post("/transaction")
                        .content("{\"description\":\"\",\"transactionDate\":\"\",\"purchaseAmount\":0.00}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description is required"))
                .andExpect(jsonPath("$.transactionDate").value("Transaction date is required"))
                .andExpect(jsonPath("$.purchaseAmount").value("Purchase amount must be a valid positive amount"));
    }

    @Test
    void whenPostRequestToTransactionsAndMore50Char_thenCorrectResponse() throws Exception {
        transactionJson = "{\"description\":\"123456789012345678901234567890123456789012345678901234567890\",\"transactionDate\":\"2023-11-21T15:07:16.918\",\"purchaseAmount\":\"-100.00\"}";
        mockMvc.perform(post("/transaction")
                        .content(transactionJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description must not exceed 50 characters"))
                .andExpect(jsonPath("$.purchaseAmount").value("Purchase amount must be a valid positive amount"));
    }
}
