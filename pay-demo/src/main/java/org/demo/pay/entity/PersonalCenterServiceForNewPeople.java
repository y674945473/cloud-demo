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
 * 个人中心-服务分类表
 * </p>
 *
 * @author xsq
 * @since 2024-01-24
 */
@Getter
@Setter
@TableName("personal_center_service_for_new_people")
public class PersonalCenterServiceForNewPeople implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 图片地址
     */
    private String img;

    /**
     * 顺序
     */
    private String order;

    /**
     * 分类名
     */
    private String name;

    /**
     * 分类父id
     */
    private String pid;

    /**
     * 服务价格
     */
    private BigDecimal price;

    /**
     * 底部文本
     */
    private String bottomText;

    /**
     * 生效时间数值
     */
    private String value;

    /**
     * 生效时间单位
     */
    private String company;

    /**
     * 优惠折扣比例
     */
    private String discount;

    /**
     * 优惠后价格
     */
    private BigDecimal discountPrice;

    /**
     * 苹果产品id
     */
    private String iphoneProductId;

    /**
     *  Apple ID
     */
    private String iphoneProductAppleId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 底部文本类型,1给划掉
     */
    private Boolean bottomTextType;

    /**
     * 代表是否跳入指定的url，商品类型 0代表可以直接购买的商品 1代表需要跳转到特定入口的商品
     */
    private Boolean type;

    /**
     * 开通年会员的路径
     */
    private String url;

    /**
     * 左上角icon
     */
    private String topLeftIcon;


    private String goodsType;

}
