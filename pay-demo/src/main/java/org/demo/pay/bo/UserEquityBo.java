package org.demo.pay.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserEquityBo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String userId;
    private String equityId;
}
