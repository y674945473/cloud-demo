package org.demo.user.controller;

import com.alibaba.nacos.common.utils.UuidUtils;
import org.demo.common.response.Result;
import org.demo.common.util.JwtUtil;
import org.demo.user.qo.UserQo;
import org.demo.user.vo.TokenVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
