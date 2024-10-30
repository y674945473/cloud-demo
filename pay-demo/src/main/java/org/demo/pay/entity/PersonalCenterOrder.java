package org.demo.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 个人中心-订单表
 * </p>
 *
 * @author ycb
 * @since 2023-12-18
 */
@Getter
@Setter
@TableName("personal_center_order")
public class PersonalCenterOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * java写的新商品表的id
     */
    private String javaProductId;

    /**
     * 用户id，如果未登录的情况下购买传设备号
     */
    private String uid;

    /**
     * 渠道编号
     */
    private String channelNumber;

    /**
     * 订单编号
     */
    private String orderNumber;

    /**
     * 微信订单流水号
     */
    private String wechatTransactionId;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 支付方式 1微信支付 2苹果支付
     */
    private String paymentMethod;

    /**
     * 订单状态 1 支付成功 0 未支付 2 支付失败 3 取消订单4.退款订单
     */
    private Integer orderStatus;

    /**
     * 订单金额 订单总金额
     */
    private BigDecimal price;

    /**
     * 订单类型，是否属于赠送订单 0 属于 1 不属于3是手动赠送订单4是手动赠送展会年会员
     */
    private String type;

    /**
     * 赠送订单标识
     */
    private String typeMark;

    /**
     * 苹果transaction_id
     */
    private String appleTransactionId;

    /**
     * 苹果订单原始id
     */
    private String originalTransactionId;

    /**
     * 苹果票据凭证
     */
    private String receipt;

    private String expiresDateMs;

    /**
     * 创建分类的时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新分类的时间
     */
    private LocalDateTime updatedAt;

    private String applePush;

    /**
     * 苹果订单的状态
     */
    private String appleStatus;

    /**
     * 发票状态0未提交1已提交2已完成
     */
    private String invoiceType;

    /**
     * 苹果订单日期
     */
    private LocalDateTime appleOrderDate;

    /**
     * 苹果产品名称
     */
    private String appleProductId;

    /**
     * 判断权益是否已经发放 1 代表已发放
     */
    private Boolean equityGiven;

    /**
     * 如果只买了一个服务的话，存这个id，后加的
     */
    private Integer singleServiceId;

    /**
     * 订单来源1.超值套餐入口
     */
    private Boolean sourceId;

    /**
     * 苹果订单同步检验同步
     */
    private Integer receiptCheckCount;

    /**
     * 定时任务创建的订单
     */
    private Boolean scheduledTasksCreated;

    /**
     * 1是ai 选址权益已发放。0是未发放
     */
    private Boolean aiYearEqGiven;

    /**
     * 标识记录
     */
    private String mark;
}
