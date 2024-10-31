package org.demo.user.controller;

import org.demo.common.response.Result;
import org.demo.common.util.JwtUtil;
import org.demo.user.qo.UserQo;
import org.demo.user.vo.TokenVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @PostMapping("/register")
    public Result<TokenVo> registerUser(@RequestBody UserQo userQo){
        //增加自己的注册落库逻辑
        TokenVo vo = new TokenVo();
        //正常token
        vo.setToken(JwtUtil.getToken(1L, userQo.getName(), true));
        vo.setExpireTime(JwtUtil.TOKEN_EXP_TIME/1000);
        //刷新token
        vo.setRefreshToken(JwtUtil.getToken(1L, userQo.getName(), false));
        return Result.ok(vo);
    }

    @PostMapping("/refreshToken")
    public Result<Object> refreshToken(@RequestBody String refreshToken){
        TokenVo vo = new TokenVo();
        if(JwtUtil.isExpired(refreshToken)){
            return Result.fail("refreshToken已过期，请重新登录");
        }
        //解密刷新token，然后重新生成token
        Map<String, Object> stringObjectMap = JwtUtil.extractInfo(refreshToken);
        if (stringObjectMap != null) {
            String uid = stringObjectMap.get("uid").toString();
            String name = stringObjectMap.get("name").toString();

            //正常token
            vo.setToken(JwtUtil.getToken(Long.parseLong(uid), name, true));
            vo.setExpireTime(JwtUtil.TOKEN_EXP_TIME/1000);
            //刷新token
            vo.setRefreshToken(JwtUtil.getToken(Long.parseLong(uid), name,  false));
        }
        return Result.ok(vo);
    }
}
