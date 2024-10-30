package org.demo.pay.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author yhx
 * @since 2024-01-30
 */
@Getter
@Setter
@TableName("t_ios_callback_verify_log")
public class IosCallbackVerifyLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long callbackId;

    /**
     * 回调类型
     */
    private String notificationType;

    private String transactionId;

    private String originalTransactionId;

    private String webOrderLineItemId;

    private String bundleId;

    private String productId;

    private String subscriptionGroupIdentifier;

    private String purchaseDate;

    private String originalPurchaseDate;

    private String expiresDate;

    private String quantity;

    private String type;

    private String inAppOwnershipType;

    private String signedDate;

    private String environment;

    private String transactionReason;

    private String storefrontId;

    private String price;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private Integer deleted;

    private String notificationUuid;

    public String getNotificationUuid() {
        return notificationUuid;
    }

    public void setNotificationUuid(String notificationUuid) {
        this.notificationUuid = notificationUuid;
    }
}
