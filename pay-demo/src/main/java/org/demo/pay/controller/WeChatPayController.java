package org.demo.pay.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.demo.common.interceptor.UserHelper;
import org.demo.common.response.Result;
import org.demo.pay.qo.BusinessCallbackQo;
import org.demo.pay.qo.WechatPayQo;
import org.demo.pay.service.WechatPayService;
import org.demo.pay.vo.OrderStatusVo;
import org.demo.pay.vo.WechatOrderVo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;

@Api(tags = "微信支付")
@RestController
@RequestMapping("/wechatPay")
@Slf4j
public class WeChatPayController {

    @Resource
    private WechatPayService wechatPayService;

    @ApiOperationSupport(order = 1)
    @ApiOperation("创建订单")
    @PostMapping("/createOrder")
    public Result<WechatOrderVo> getData(@RequestBody @Valid WechatPayQo wechatPayQo, BindingResult bindingResult, HttpServletRequest request) {
        if(bindingResult.hasErrors()){
            return Result.paramFail();
        }
        String userId = UserHelper.getUserId().toString();
        if(StringUtils.isEmpty(userId)){
            return Result.loginFail();
        }
        String os = request.getHeader("os");
        return Result.ok(wechatPayService.createOrder(wechatPayQo,userId,os));
    }


    @ApiOperationSupport(order = 2)
    @ApiOperation("微信支付回调（为微信支付提供）")
    @PostMapping("/resultCallback")
    public String resultCallback(HttpServletRequest request, HttpServletResponse response) {
        Gson gson = new Gson();
        final HashMap<String, Object> map = new HashMap<>();
        Integer status = wechatPayService.weChatNotificationHandler(request);
        response.setStatus(status);
        if(status != 200){
            map.put("code", "ERROR");
            map.put("message", "通知验签失败");
            return gson.toJson(map);
        }else{
            map.put("code", "SUCCESS");
            map.put("message", "成功");
            return gson.toJson(map);
        }
    }



    @ApiOperationSupport(order = 3)
    @ApiOperation("回写业务结果（为后端业务提供）")
    @PostMapping("/businessCallback")
    public Result<Boolean> businessCallback(@RequestBody @Valid BusinessCallbackQo businessCallbackQo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return Result.paramFail();
        }
        return Result.ok(wechatPayService.businessCallback(businessCallbackQo));
    }


    @ApiOperationSupport(order = 4)
    @ApiOperation("查询订单状态")
    @GetMapping("/getOrderStatus/{orderNo}")
    public Result<OrderStatusVo> getOrderStatus(@PathVariable(name = "orderNo") String orderNo) {
        String userId = UserHelper.getUserId().toString();
        if(StringUtils.isEmpty(userId)){
            return Result.loginFail();
        }
        return Result.ok(wechatPayService.getOrderStatus(orderNo));
    }



    @ApiOperationSupport(order = 5)
    @ApiOperation("关闭订单")
    @GetMapping("/closeOrder/{orderNo}")
    public Result<Boolean> closeOrder(@PathVariable(name = "orderNo") String orderNo) {
        String userId = UserHelper.getUserId().toString();
        if(StringUtils.isEmpty(userId)){
            return Result.loginFail();
        }
        return Result.ok(wechatPayService.closeOrder(orderNo));
    }
}
