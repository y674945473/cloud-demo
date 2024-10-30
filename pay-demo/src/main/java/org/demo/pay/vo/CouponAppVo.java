package org.demo.pay.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @ClassName: CouponAppVo
 * @Description: TODO
 * @author: 55555
 * @date: 2024年05月15日 16:33
 */
@Data
public class CouponAppVo {

    @ApiModelProperty("主键")
    private Long id;

    /**
     * 优惠券名称
     */
    @ApiModelProperty("优惠券名称")
    private String name;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String endTime;


    /**
     * 活动类型
     */
    @ApiModelProperty("图标")
    private String icon;

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String image;

    /**
     * 是否可用
     */
    @ApiModelProperty("是否可用")
    private Boolean available;

    /**
     * 不可用原因
     */
    @ApiModelProperty("不可用原因")
    private String reason;

    /**
     * 限制
     */
    @ApiModelProperty("限制描述")
    private String limitDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;

    @ApiModelProperty("ios路由")
    private String iosRoute;



    @ApiModelProperty("安卓路由")
    private String androidRoute;




}
