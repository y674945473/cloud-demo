package org.demo.user.vo;

import lombok.Data;

@Data
public class TokenVo {

    private String token;

    private Long expireTime;

    private String refreshToken;
}
