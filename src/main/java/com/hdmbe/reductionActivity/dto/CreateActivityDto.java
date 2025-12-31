package com.hdmbe.reductionActivity.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class CreateActivityDto {
    private LocalDate periodStart;

    private LocalDate periodEnd;

    private String activityName;

    private String activityDetails;

    private BigDecimal costAmount;

    private String expectedEffect;
}
