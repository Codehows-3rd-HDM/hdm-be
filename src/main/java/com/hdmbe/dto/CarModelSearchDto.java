package com.hdmbe.dto;

import com.hdmbe.constant.FuelType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModelSearchDto {
    private Long categoryId;
    private FuelType fuelType;

}
