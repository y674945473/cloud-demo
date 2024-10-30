package org.demo.pay.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
@Component
public class AliPayUtil {

    private static final String APP_ID = "2021004145610218";
    private static final String PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC7XwMGBEAQCvYJMTTo+VB+98630vTLUtw3cOnkjR/zg2n9KKyNzwaGxL+9U7V83HF/eUHnOqRaPhocTdEQMo2R3aCcuLv8Eul4qx3gR5/j5RQJeNUuJ8vGcUsOzDeG+BJQG/2slgLE6WzWon66yTvIs6dDfrHBwoWCNtXO83pwSV1RCG0czL49dgjotgT3ssY7IWvEvAi3EK9yC8uo0zkss4AvdvlxEs6FQEAzbv5QbV0fZ5lwbS+gUvmRVpDUgfnnk9vOfJQkGoFIIKEn8/U01qdtkknQ8SwhKq7EwpW97hQDw42n/GQyDgo2TeNnkpR2qAJafkaSb928uuijfoEFAgMBAAECggEBALk0Od3Zl6UD4JvHaqAZxyopMchbmaHb/lZCor5JjFp7++jDKoRlLPNLSKcQNeT8VKeGqNCxE0Er/00Y0dwry+lxYs9mflG0M9gVc8t7oJx3ky2lpTohFffJhkJTM1OSoy9R65WLFaOjgvRI2/Eu9Yv1oBBEC9oUtzi1kPMlkVGoUII7XulwGnLQ5kwURigVHDRM6JDEKyI1CCuw6k5n+QS3xC2uc/ZVBwfQ/id4MA3lKdWzqEOKgHG6+/kNh26XJxcDMWOtX42t0EG4MNnl7yxMd+fQ/Ly48HMljEotHBYzVt09Lj29TRmQgJZ471eD42gBzfHk8hZhpXm8eWb19OUCgYEA7DzuGFS9qKXluOkJif9gTVmlJ+7jYu4SAWT3r4D/K+dH0uVLH8IT2D+yVUfYKELF7bF3sf9uw/2IJ/S8icPwf1HE2CHLCVZCRUzbzlD2PjNz7WB7Qs+esxYF2GS/hF+8IzsUiqFmPceCtlvlJT54AzkQtvNqRll2OQNsimAuJPcCgYEAywuXSB7PmPkkghrcTSRzsth6hM5KksRh3/8LhsbJ0pk7upwchLPriHT8JeeLiR0OxEDmO+F8S96gUj3tkqAibIMHMmgCroFctMTooad1X8UyGg+14jHDCLfotiT7HJQIt9Krfya1mJA3HrI/dkcYBMhba2X95eHCAVfNF9qtluMCgYBGrSfygY/IX/Gtc5LWBWAzZAmEO/UtiNo8Lo/nouk0olobn1vtYWv8e3oIB5mE2g1LQpfz2d6ypixQ/+hUMxnFjZodq2aKIZNsFkWAoTo6e7xIHWBjKahqHcGb7vBZUyb/R1Kq8wSaoRDbK/0POnQ/SphUk9iMuYEkJ4EWmRXVGwKBgQCFkZJrNxmJoBuMGu9hL+GMfSvbwXj9I4LfP3/toUeS6oqIL7ny8gi3M33L0Y3RHPRh1e+e5K5HmqQMdgCavAzpJSjO1+0rrp21rPL7pJVp8ucKjdsweVu7mH/Vkm4+VoOFqWh3tJcxTW11G/zzacE7JDI/bFChsOlWyNN6Jbib0QKBgHK5l+Qp4IGdBX8elPswfZjxGtqiENDa5eOFKHrbBjtGk5ewRHi7Jl8H+8jNNcsI+fAVoWbFFE3tUHrjh7r9JENVg+IFr0nIDSsZt0ZEhw1L59Gq4wWHDVEqEhVOOikrRpgKMG3vr1PkxA58wLAKm/h0NHi+e15ctxr7/jXocb6e";

    @Resource
    private ResourceUtil resourceUtil;

    public String alipay(String outTradeNo, BigDecimal amount, String subject, String notifyUrl) throws IOException {
        log.info("发起支付宝支付请求,outTradeNo={},amount={},subject={}", outTradeNo, amount, subject);
        File file = getResourceAsFile("appCertPublicKey.crt");
        File file1 = getResourceAsFile("alipayCertPublicKey_RSA2.crt");
        File file2 = getResourceAsFile("alipayRootCert.crt");
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setServerUrl("https://openapi.alipay.com/gateway.do");
        alipayConfig.setAppId(APP_ID);
        alipayConfig.setPrivateKey(PRIVATE_KEY);
        alipayConfig.setAppCertPath(file.getAbsolutePath());
        alipayConfig.setAlipayPublicCertPath(file1.getAbsolutePath());
        alipayConfig.setRootCertPath(file2.getAbsolutePath());
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setSignType("RSA2");
        AlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient(alipayConfig);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        request.setNotifyUrl(notifyUrl);
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("total_amount", amount);
        bizContent.put("subject", subject);
        request.setBizContent(bizContent.toString());
        AlipayTradeAppPayResponse response = null;
        try {
            response = alipayClient.sdkExecute(request);
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
        String orderStr = response.getBody();
        System.out.println(orderStr);
        if (response.isSuccess()) {
            log.info("支付宝支付调用成功orderStr={}", orderStr);
            return orderStr;
        } else {
            log.error("支付宝支付调用失败,orderStr{}", orderStr);
            return "";
        }
    }

    public static File getResourceAsFile(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            InputStream inputStream = resource.getInputStream();
            Path tempFile = Files.createTempFile("temp-", "-" + fileName);
            try (FileOutputStream out = new FileOutputStream(tempFile.toFile())) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile.toFile();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
