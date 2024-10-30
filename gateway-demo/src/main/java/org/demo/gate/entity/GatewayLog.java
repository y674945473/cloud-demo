package org.demo.gate.entity;

import lombok.Data;

/**
*
* 项目名称：bh_pro_gateway
* 类名称：GatewayLog
* 类描述：
* 创建人：xsq
* 创建时间：2023年9月22日 下午3:38:31
* 修改人：
* 修改时间：2023年9月22日 下午3:38:31
* 修改备注：
* @version V1.0 
*
*/
@Data
public class GatewayLog {
    private String requestPath;
    private String requestMethod;
    private String schema;
    private String requestBody;
    private String responseBody;
    private String ip;
    private String requestTime;
    private String responseTime;
    private Long executeTime;
}
