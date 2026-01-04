package com.gp_dev.erp_lite.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopClientDto {

    private Long clientId;
    private String clientName;
    private BigDecimal totalRevenue;
    private Integer invoiceCount;
}
