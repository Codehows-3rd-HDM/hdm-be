package com.hdmbe.reductionActivity.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ResponseActivityDto {

    private Long id;

    private LocalDate periodStart;

    private LocalDate periodEnd;

    private String activityName;

    private String activityDetails;

    private BigDecimal costAmount;

    private String expectedEffect;

    private String imageUrl;

    private List<String> imageUrls;
}
