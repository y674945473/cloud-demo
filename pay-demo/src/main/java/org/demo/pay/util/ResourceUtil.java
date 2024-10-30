package org.demo.pay.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ResourceUtil {


    @Value("appCertPublicKey.crt")
    private Resource appCertPublicKeyPath;

    @Value("alipayCertPublicKey_RSA2.crt")
    private Resource alipayCertPublicPath;

    @Value("alipayRootCert.crt")
    private Resource alipayRootCertPath;

    public String getAppCertPublicPath() {
        try {
            return appCertPublicKeyPath.getURI().getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAlipayCertPublicPath() {
        try {
            return alipayCertPublicPath.getURI().getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getAlipayRootCertPath() {
        try {
            return alipayRootCertPath.getURI().getPath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args)throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("alipayRootCert.crt");
        String classPathStr = classPathResource.getFile().getPath();
    }




}
