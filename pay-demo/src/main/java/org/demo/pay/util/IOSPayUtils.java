package org.demo.pay.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Locale;
@Log4j2
@Component
public class IOSPayUtils {


    private static class TrustAnyTrustManager implements X509TrustManager {


        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }






    /**
     * 苹果服务器验证
     *
     * @param receipt 账单
     * @return null 或返回结果 沙盒 https://sandbox.itunes.apple.com/verifyReceipt
     * @url 要验证的地址
     */
    public static String buyAppVerify(String receipt , int payType,String url,String secretPassword) {
        //环境判断 线上/开发环境用不同的请求链接
        try {

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());
            HttpsURLConnection conn = getHttpsURLConnection(url, sc);
            BufferedOutputStream hurlBufOus = new BufferedOutputStream(conn.getOutputStream());
            String str = "";
//            if(payType == 0){
//
//                //拼成固定的格式传给平台
//                str = String.format(Locale.CHINA, "{\"receipt-data\":\"" + receipt + "\"}");
//            }else if(payType == 1){
                //连续包月订阅需要加上共享密钥
                str = String.format(Locale.CHINA,
                    "{\"receipt-data\":\"" + receipt + "\",\"password\":\"" + secretPassword + "\"}");
//            }


            // 直接将receipt当参数发到苹果验证就行，不用拼格
            // String str = String.format(Locale.CHINA, receipt);
            hurlBufOus.write(str.getBytes());
            hurlBufOus.flush();

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception ex) {
            log.info("苹果服务器异常,参数：{}",receipt);
            System.out.println("苹果服务器异常");
            ex.printStackTrace();
        }
        return null;

    }

    private static HttpsURLConnection getHttpsURLConnection(String url, SSLContext sc) throws IOException {
        URL console = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        conn.setRequestMethod("POST");
        conn.setRequestProperty("content-type", "text/json");
        conn.setRequestProperty("Proxy-Connection", "Keep-Alive");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(3000);
        return conn;
    }

    /**
     * 用BASE64加密
     *
     * @param str
     * @return
     */
    public static String getBASE64(String str) {
        byte[] b = str.getBytes();
        String s = null;
        if (b != null) {
            s = new sun.misc.BASE64Encoder().encode(b);
        }
        return s;
    }
}
