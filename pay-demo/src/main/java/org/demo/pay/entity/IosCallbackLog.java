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
@TableName("t_ios_callback_log")
public class IosCallbackLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * ios回调请求报文
     */
    private String signedPayload;

    /**
     * 解析状态，0未解析，1已解析，2解析失败
     */
    private Integer status;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private Integer deleted;
}
