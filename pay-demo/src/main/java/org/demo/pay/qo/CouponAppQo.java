package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName: CouponAppQo
 * @Description: TODO
 * @author: 55555
 * @date: 2024年05月15日 16:26
 */
@Data
public class CouponAppQo {

    /**
     * 金额
     */
    @ApiModelProperty("价格")
    private BigDecimal price;

    /**
     * 支付类型 0代表微信支付 1代表ios支付2边界贝支付3支付宝支付
     */
    @ApiModelProperty("设备")
    private String payType;

    /**
     * 商品id
     */
    @ApiModelProperty("商品id")
    private String goodsId;;

}
