package com.rcalves.wexpb.integrations.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateData {
    @JsonProperty("country_currency_desc")
    private String countryCurrencyDesc;
    @JsonProperty("exchange_rate")
    private BigDecimal exchangeRate;
    @JsonProperty("record_date")
    private String recordDate;
}
