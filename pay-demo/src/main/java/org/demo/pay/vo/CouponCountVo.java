package org.demo.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: CouponCountVo
 * @Description: TODO
 * @author: 55555
 * @date: 2024年05月16日 12:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponCountVo implements Serializable {

    @ApiModelProperty("可用优惠券数量")
    private Integer count;

    @ApiModelProperty("优惠券总数量")
    private Integer totalCount;
}
