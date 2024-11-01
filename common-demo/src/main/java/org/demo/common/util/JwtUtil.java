package org.demo.common.util;

import io.jsonwebtoken.*;
import lombok.extern.log4j.Log4j2;
import org.demo.common.exception.BaseException;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;

@Log4j2
public class JwtUtil {

    private static final String TOKEN_SECRET = "qwe123!@#";

    //token过期时间
    public static final Long TOKEN_EXP_TIME = 1000L * 60 * 60 * 2;

    //refresh_token过期时间
    public static final Long REFRESH_TOKEN_EXP_TIME = 1000L * 60 * 60 * 24 * 3;
    /**
     * 生成Token
     */
    public static String getToken(Long userId, String name,boolean flag) {
        Date now = new Date();
        String sign = null;
        try {
            Map<String, Object> claims = new HashMap<>(2);
            claims.put("uid", userId);
            claims.put("name", name);
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(TOKEN_SECRET);
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            Date date = new Date(now.getTime() + TOKEN_EXP_TIME);
            if(!flag){
                date = new Date(now.getTime() + REFRESH_TOKEN_EXP_TIME);
            }
            sign = Jwts.builder().setClaims(claims).setExpiration(date).signWith(SignatureAlgorithm.HS512, signingKey).compact();
        } catch (Exception e) {
            log.error("token error ,{}", e.getMessage(), e);
        }
        return sign;
    }


    /**
     * 利用jwt解析token信息.
     *
     * @param token  要解析的token信息
     * @param secret 用于进行签名的秘钥
     */
    public static Optional<Claims> getClaimsFromToken(String token, String secret) {
        Claims claims;

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        try {
            claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
            return Optional.of(claims);
        } catch (ExpiredJwtException e){
            return Optional.empty();
        }catch (MalformedJwtException | IllegalArgumentException | SignatureException e) {
            throw new BaseException(503,"jwt解密失败");
        }

    }

    /**
     * 验证token是否过期
     *
     * @return true 表示过期，false表示不过期，如果没有设置过期时间，则也不认为过期
     */
    public static boolean isExpired(String token) {
        Optional<Claims> claims = getClaimsFromToken(token, TOKEN_SECRET);
        if (claims.isPresent()) {
            Date expiration = claims.get().getExpiration();
            return expiration.after(new Date());
        }
        return false;
    }

    /**
     * 获取token中的参数值
     *
     * @param token 要解析的token信息
     */
    public static Map<String, Object> extractInfo(String token) {
        Optional<Claims> claims = getClaimsFromToken(token, TOKEN_SECRET);
        if (claims.isPresent()) {
            Map<String, Object> info = new HashMap<>(16);
            Set<String> keySet = claims.get().keySet();
            for (String key : keySet) {
                Object value = claims.get().get(key);
                info.put(key, value);
            }
            return info;
        }
        return null;
    }

}
