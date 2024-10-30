package org.demo.pay.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
*
* 项目名称：bh_pro_base
* 类名称：WechatPayConfig
* 类描述：微信支付配置类
* 创建人：xsq
* 创建时间：2023年6月30日 上午8:28:05
* 修改人：
* 修改时间：2023年6月30日 上午8:28:05
* 修改备注：
* @version V1.0 
*
*/

@Data
@Configuration
public class WechatPayConfigProperties {

    /** 商户号 */
    @Value("${wechat.pay.merchant.id}")
    public String merchantId;

    /** 商户API私钥路径 */
    @Value("${wechat.pay.private.key.path}")
    public String privateKeyPath;

    /** 商户证书序列号 */
    @Value("${wechat.pay.merchant.serial.number}")
    public String merchantSerialNumber;

    /** 商户APIV3密钥 */
    @Value("${wechat.pay.api.v3.key}")
    public String apiV3key;

    /** 商户APPID */
    @Value("${wechat.pay.api.app.id}")
    public  String appId;
}
