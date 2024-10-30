package org.demo.pay.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserEquityDto implements Serializable {

    private static final long serialVersionUID = 1L;


    private String equityId;

    //次数
    private Integer count;

    //到期时间
    private LocalDateTime expireTime;
}
