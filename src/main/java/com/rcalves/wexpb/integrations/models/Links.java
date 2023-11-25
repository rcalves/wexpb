package com.rcalves.wexpb.integrations.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Links {
    private String self;
    private String first;
    private String prev;
    private String next;
    private String last;
}
