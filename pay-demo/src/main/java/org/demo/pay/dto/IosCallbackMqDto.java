package org.demo.pay.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class IosCallbackMqDto implements Serializable {

    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */
    private static final long serialVersionUID = 1L;

    private String signedPayload;

    private String callbackId;
}
