package com.rcalves.WEXPB.integrations.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRateResponse {
    private List<ExchangeRateData> data;
    private Meta meta;
}
