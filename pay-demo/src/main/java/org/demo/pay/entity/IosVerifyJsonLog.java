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
 * @author xsq
 * @since 2024-01-29
 */
@Getter
@Setter
@TableName("t_ios_verify_json_log")
public class IosVerifyJsonLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long callbackId;

    private String message;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private String orderNo;

    private String status;
}
