package org.demo.user.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.demo.common.exception.BaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

@Log4j2
public class FacebookAuth {

    // 应用编号
    @Value("${facebook.appId}")
    private String appId;
    // 应用秘钥
    @Value("${facebook.appSecret}")
    private String appSecret;
    // 获取临时口令（code）
    private String codeUrl = "https://www.facebook.com/v21.0/dialog/oauth?";

    private ObjectMapper objectMapper = new ObjectMapper();


    public String getFacebookUrl(String url) {
        return codeUrl + "client_id=" + appId + "&redirect_uri=" + url;
    }

    public String getFacebookId(String code,String redirectUrl) {
        String facebookId = "";
        // 根据code获取access_token
        String facebookAccessToken = getFacebookAccessToken(code, redirectUrl);
        // 验证accessToken
        boolean verify = verify(facebookAccessToken);
        if(verify){
            // 获取用户信息
            facebookId = getUserInfo(facebookAccessToken);
            if(StringUtils.isEmpty(facebookId)){
                throw new BaseException(501, "授权失败");
            }
        }else{
            throw new BaseException(501, "授权失败");
        }
        return facebookId;
    }


    /**
     * 根据code获取access_token
     */
    public String getFacebookAccessToken(String code,String redirectUrl) {
        String accessToken;
        try {
            // 获取tokenUrl
            String tokenUrl = "https://graph.facebook.com/v21.0/oauth/access_token";
            String api = tokenUrl + "?client_id=" + appId + "&redirect_uri=" + redirectUrl + "&client_secret=" + appSecret + "&code=" + code;
            WebClient webClient = WebClient.create();
            String result = webClient.method(HttpMethod.GET)
                    .uri(api)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode readValue = parseJSONObject(result);
            Object object = readValue.get("access_token");
            if(ObjectUtils.isNotEmpty(object)){
                accessToken = object.toString().replace("\"","");
            }else{
                throw new BaseException(501, "授权失败");
            }
        } catch (Exception e) {
            log.error("accessToken: {}", e.getMessage());
            throw new BaseException(501, "授权失败");
        }
        return accessToken;

    }

    /**
     * 根据accessToken获取用户信息
     */
    private String getUserInfo(String accessToken) {
        if (null != accessToken) {
            try {
                String fields = "id,name,birthday,gender,hometown,email,picture";
                // 获取用户信息
                WebClient webClient = WebClient.create();
                String userUrl = "https://graph.facebook.com/me?access_token="+ accessToken + "&fields" + fields;
                String result = webClient.method(HttpMethod.GET)
                        .uri(userUrl)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                JsonNode readValue = parseJSONObject(result);
                if(ObjectUtils.isEmpty(readValue)){
                    log.error("facebook getUserInfo readValue: {}", readValue);
                    throw new BaseException(501, "授权失败");
                }
                JsonNode id = readValue.get("id");
                if (null != id) {
                    return id.toString().replace("\"","");
                }
            } catch (Exception e) {
                log.error("facebook getUserInfo: {}", e.getMessage());
                throw new BaseException(501, "授权失败");
            }
        }
        return "";
    }

    public JsonNode parseJSONObject(String jsonString) {
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            log.error("JSONString转为JsonNode失败：{}", e.getMessage());
        }
        return jsonNode;
    }

    /**
     * 防止别人拿其他平台的appId授权的token来请求
     */
    public boolean verify(String access_token) {
        String verifyUrl = "https://graph.facebook.com/debug_token?access_token=";
        try {
            WebClient webClient = WebClient.create();
            String result = webClient.method(HttpMethod.GET)
                    .uri(verifyUrl + appId + "%7C" + appSecret + "&input_token=" + access_token)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode jsonNode = parseJSONObject(result);
            if(ObjectUtils.isEmpty(jsonNode)){
                log.error("facebook verify readValue: {}", jsonNode);
                throw new BaseException(501, "授权失败");
            }
            JsonNode data = jsonNode.get("data");
            if (null != data) {
                String isValid = data.get("is_valid").toString();
                return "true".equals(isValid);
            }
        } catch (Exception e) {
            log.error("facebook verify: {}", e.getMessage());
            throw new BaseException(501, "授权失败");
        }
        return false;
    }
}
