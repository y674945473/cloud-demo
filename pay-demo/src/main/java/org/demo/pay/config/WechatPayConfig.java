package org.demo.pay.config;

import com.google.common.io.ByteStreams;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.service.payments.app.AppService;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
*
* 项目名称：bh_pro_base
* 类名称：WechatPayConfig
* 类描述：
* 创建人：xsq
* 创建时间：2023年6月30日 上午8:34:58
* 修改人：
* 修改时间：2023年6月30日 上午8:34:58
* 修改备注：
* @version V1.0 
*
*/

@Component
public class WechatPayConfig {

    @Resource
    private WechatPayConfigProperties wechatPayConfigProperties;


    private static RSAAutoCertificateConfig config = null;

    @Bean
    public RSAAutoCertificateConfig rSAAutoCertificateConfig() {
        if(config == null){
            config = new RSAAutoCertificateConfig.Builder()
                    .merchantId(wechatPayConfigProperties.merchantId).privateKey(loadPrivateKeyFromString(getPrivateKeyPath()))
//             .privateKeyFromPath(resource.getFile().getPath())
                    .merchantSerialNumber(wechatPayConfigProperties.merchantSerialNumber)
                    .apiV3Key(wechatPayConfigProperties.apiV3key).build();
        }
        return config;
    }

    // 自动更新微信支付平台证书
    @Bean
    public Config wechatConfig() {
        RSAAutoCertificateConfig config = rSAAutoCertificateConfig();
        return config;
    }

    @Bean
    public NotificationConfig notificationConfig() {
        NotificationConfig config = rSAAutoCertificateConfig();
        return config;
    }

    public PrivateKey loadPrivateKeyFromString(String keyString) {
        try {
            keyString = keyString.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
            return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(keyString)));
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 
    * @Title: appService 
    * @Description: APP支付服务初始化 
    * @return    
    * @return AppService    返回类型
    * @author xsq 
    * @throws
    * @date 2023年7月1日 下午3:21:05 
    * @version V1.0
     */
    @Bean
    public AppService appService() {
        AppService.Builder builder = new AppService.Builder().config(wechatConfig());
        return builder.build();
    }

    @Bean
    public NativePayService nativePayService() {
        NativePayService service = new NativePayService.Builder().config(wechatConfig()).build();
        return service;
    }

    private String getPrivateKeyPath() {
        ClassPathResource resource = new ClassPathResource(wechatPayConfigProperties.privateKeyPath);
        String keyString = null;
        try {
            InputStream in = resource.getInputStream();
            keyString = new String(ByteStreams.toByteArray(in));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyString;
    }
}
