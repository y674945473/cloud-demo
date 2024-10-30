package org.demo.user.auth;

import lombok.extern.log4j.Log4j2;
import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpParameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.demo.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

@Log4j2
public class TwitterAuth {

    @Value("${twitter.consumerKey}")
    private String twitterConsumerKey;

    @Value("${twitter.consumerSecret}")
    private String twitterConsumerSecret;

    /**
     * 第一步：通过授权code获取token
     * @return
     */
    public String RequestToken(String url) {
        String api = "https://api.twitter.com/oauth/authenticate?oauth_token=";
        CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
        // 创建HttpParameters对象，并添加自定义参数
        HttpParameters parameters = new HttpParameters();
        parameters.put(OAuth.OAUTH_CALLBACK, url);
        consumer.setAdditionalParameters(parameters);
        // 创建API请求，例如获取用户的时间线
        String apiUrl = "https://api.x.com/oauth/request_token";
        HttpGet request = new HttpGet(apiUrl);
        // 对请求进行OAuth1签名
        try {
            consumer.sign(request);
            String authorization = request.getHeaders("Authorization")[0].getValue();

            WebClient webClient = WebClient.create();
            String result = webClient.method(HttpMethod.GET)
                    .uri(apiUrl)
                    .headers(headers -> headers.set("Authorization", authorization))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            if(StringUtils.isEmpty(result)){
                throw new BaseException(501, "授权失败");
            }
            List<NameValuePair> param = URLEncodedUtils.parse(result, StandardCharsets.UTF_8);
            HashMap<String,String> map = new HashMap<>();
            // 遍历参数并输出键值对
            for (NameValuePair parameter : param) {
                String key = parameter.getName();
                String value = parameter.getValue();
                map.put(key,value);
            }
            String oauthToken = map.get("oauth_token");
            if(StringUtils.isEmpty(oauthToken)){
                throw new BaseException(501, "授权失败");
            }
            api = api + oauthToken;
        } catch (Exception e) {
            log.error("RequestToken: {}", e.getMessage());
            throw new BaseException(501, "授权失败");
        }
        return api;
    }


    /**
     * 第二部，页面跳转
     * @param token
     * @param verifier
     * @return
     */
    public String getTwitterId(String token,String verifier) {
        String twitterId = "";
        // 创建CommonsHttpOAuthConsumer对象，设置OAuth1验证参数
        CommonsHttpOAuthConsumer consumer = new CommonsHttpOAuthConsumer(twitterConsumerKey, twitterConsumerSecret);
        // 创建HttpParameters对象，并添加自定义参数
        HttpParameters parameters = new HttpParameters();
        parameters.put(OAuth.OAUTH_TOKEN, token);
        consumer.setAdditionalParameters(parameters);
        // 创建API请求，例如获取用户的时间线
        String apiUrl = "https://api.twitter.com/oauth/access_token";
        HttpPost request = new HttpPost(apiUrl);
        try {
            request.setHeader("Content-Type","application/x-www-form-urlencoded");
            consumer.sign(request);
            String authorization = request.getHeaders("Authorization")[0].getValue();

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("oauth_verifier", verifier);
            WebClient webClient = WebClient.create();
            String result = webClient.method(HttpMethod.POST)
                    .uri(apiUrl)
                    .headers(headers -> headers.set("Authorization", authorization))
                    .body(BodyInserters.fromFormData(map))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            List<NameValuePair> param = URLEncodedUtils.parse(result, StandardCharsets.UTF_8);
            HashMap<String,String> hashMap = new HashMap<>();
            // 遍历参数并输出键值对
            for (NameValuePair parameter : param) {
                String key = parameter.getName();
                String value = parameter.getValue();
                hashMap.put(key,value);
            }
            twitterId = hashMap.get("user_id");
            if(StringUtils.isEmpty(twitterId)){
                throw new BaseException(501, "授权失败");
            }
        } catch (Exception e) {
            throw new BaseException(501, "授权失败");
        }
        return twitterId;
    }
}
