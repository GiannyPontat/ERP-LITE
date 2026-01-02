package com.gp_dev.erp_lite.dtos;

import com.gp_dev.erp_lite.models.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RoleDto {
    private Long id;
    private RoleType name;
}
