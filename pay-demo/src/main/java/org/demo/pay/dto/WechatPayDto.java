package org.demo.pay.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WechatPayDto {

    private BigDecimal price;

    private String description;

    private String orderNo;
}
