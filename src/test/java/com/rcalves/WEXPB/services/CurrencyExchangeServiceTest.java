package com.rcalves.wexpb.services;

import com.rcalves.wexpb.exceptions.BusinessException;
import com.rcalves.wexpb.integrations.models.ExchangeRateData;
import com.rcalves.wexpb.integrations.models.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CurrencyExchangeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CurrencyExchangeService currencyExchangeService;

    private ExchangeRateResponse mockResponse;

    @BeforeEach
    void setUp() {
        ExchangeRateData data = new ExchangeRateData();
        data.setCountryCurrencyDesc("Brazil-Real");
        data.setExchangeRate(new BigDecimal("5.00"));
        data.setRecordDate(LocalDate.now().minusMonths(1).toString());

        mockResponse = new ExchangeRateResponse();
        mockResponse.setData(Arrays.asList(data));
    }


    @Test
    void getExchangeRate_ShouldReturnRate() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);

        BigDecimal rate = currencyExchangeService.getExchangeRate("BRL", LocalDate.now());

        assertNotNull(rate);
        assertEquals(0, rate.compareTo(new BigDecimal("5.00").setScale(2, RoundingMode.HALF_EVEN)));
    }

    @Test
    void getExchangeRate_ShouldThrowException_WhenNoRateIsAvailable() {
        ExchangeRateResponse emptyResponse = new ExchangeRateResponse();
        emptyResponse.setData(new ArrayList<>());

        when(restTemplate.getForObject(anyString(), any())).thenReturn(emptyResponse);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> currencyExchangeService.getExchangeRate("BRL", LocalDate.now())
        );

        assertTrue(exception.getMessage().contains("No currency conversion rate available"));
    }


    @Test
    void convertCurrency_ShouldConvertCorrectly() {
        BigDecimal amount = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("5");

        BigDecimal convertedAmount = currencyExchangeService.convertCurrency(amount, rate);

        assertNotNull(convertedAmount);
        assertEquals(0, convertedAmount.compareTo(new BigDecimal("500.00").setScale(2, RoundingMode.HALF_UP)));
    }

    @Test
    void getSupportedCurrencies_ShouldReturnCurrencies() {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(mockResponse);

        List<String> currencies = currencyExchangeService.getSupportedCurrencies();

        assertNotNull(currencies);
        assertFalse(currencies.isEmpty());
        assertTrue(currencies.contains("Brazil-Real"));
    }
}