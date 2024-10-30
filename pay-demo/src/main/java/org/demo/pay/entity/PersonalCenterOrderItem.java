package org.demo.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单单个商品折扣状态件数表
 * </p>
 *
 * @author yhx
 * @since 2024-02-19
 */
@Getter
@Setter
@TableName("personal_center_order_item")
public class PersonalCenterOrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 使用折扣后的单价
     */
    private String discountUnitPrice;

    /**
     * 单价
     */
    private String unitPrice;

    /**
     * 件数
     */
    private String item;

    /**
     * 服务表id
     */
    private String serviceTableId;

    /**
     * 是否使用折扣
     */
    private String useDiscount;

    /**
     * 创建分类的时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新分类的时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * java新商品表的id
     */
    private String javaProductId;

    /**
     * 商品名称
     */
    @TableField(exist = false)
    private String goodsName;


    /**
     * 商品价格
     */
    @TableField(exist = false)
    private String goodsPrice;

}
