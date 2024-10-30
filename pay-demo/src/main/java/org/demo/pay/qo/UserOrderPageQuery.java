package org.demo.pay.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserOrderPageQuery implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 页码
     */
    @ApiModelProperty("页码")
    private Integer number = 1;

    /**
     * 数量
     */
    @ApiModelProperty("每页几条")
    private Integer size = 10;

}
