package com.rcalves.wexpb.services;

import com.rcalves.wexpb.exceptions.BusinessException;
import com.rcalves.wexpb.integrations.models.ExchangeRateData;
import com.rcalves.wexpb.integrations.models.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyExchangeService {

    private final RestTemplate restTemplate;
    private final String baseUrl = "https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange";

    @Autowired
    public CurrencyExchangeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BigDecimal getExchangeRate(String currencyCode, LocalDate date) {
        String formattedDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate sixMonthsAgo = date.minusMonths(6);

        String url = String.format("%s?fields=exchange_rate,record_date&filter=country_currency_desc:eq:%s,record_date:gte:%s,record_date:lte:%s&sort=-record_date&format=json&page[number]=1&page[size]=1",
                baseUrl, currencyCode, sixMonthsAgo, formattedDate);

        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

        if (response != null && !response.getData().isEmpty()) {
            ExchangeRateData latestRate = response.getData().get(0);
            LocalDate rateDate = LocalDate.parse(latestRate.getRecordDate());

            if (rateDate.isBefore(sixMonthsAgo)) {
                throw new BusinessException("No currency conversion rate available within the last 6 months for the target currency.");
            }
            return latestRate.getExchangeRate().setScale(2, RoundingMode.HALF_EVEN);
        } else {
            throw new BusinessException("No currency conversion rate available for the target currency.");
        }
    }

    public BigDecimal convertCurrency(BigDecimal amount, BigDecimal exchangeRate) {
        return amount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);
    }

    public List<String> getSupportedCurrencies() {
        String url = String.format("%s?fields=country_currency_desc&format=json&page[number]=1&page[size]=1000", baseUrl);

        ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

        if(response != null) {
            return response.getData().stream()
                    .map(ExchangeRateData::getCountryCurrencyDesc)
                    .distinct()
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
