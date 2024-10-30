package org.demo.pay.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.demo.common.response.Result;
import org.demo.pay.entity.Order;
import org.demo.pay.qo.IOSCallBackQo;
import org.demo.pay.qo.IOSPayAgainVerifyQo;
import org.demo.pay.qo.IOSPayVerifyQo;
import org.demo.pay.qo.WechatPayQo;
import org.demo.pay.service.IOSPayService;
import org.demo.pay.vo.IOSOrderVo;
import org.demo.pay.vo.IOSStatusVo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("ios")
public class IosPayController {

    @Resource
    private IOSPayService iosPayService;


    @ApiOperationSupport(order = 1)
    @ApiOperation("创建订单")
    @PostMapping("/createOrder")
    public Result<IOSOrderVo> getData(@RequestBody @Valid WechatPayQo wechatPayQo, BindingResult bindingResult, HttpServletRequest request) {
        if(bindingResult.hasErrors()){
            return Result.paramFail();
        }
        String userId = "UserUtil.getCurrentUserId()";
        if(StringUtils.isEmpty(userId)){
            return Result.loginFail();
        }
        String os = request.getHeader("os");
        IOSOrderVo iosOrderVo = iosPayService.createOrder(wechatPayQo, userId,os);
        if(StringUtils.isEmpty(iosOrderVo.getOrderNo())){
            return Result.fail();
        }
        return Result.ok(iosOrderVo);
    }

    @ApiOperationSupport(order = 2)
    @ApiOperation("ios支付验证")
    @PostMapping("/iosVerify")
    public Result<IOSStatusVo> iosVerify(@RequestBody @Valid IOSPayVerifyQo iosPayVerifyQo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return Result.paramFail();
        }
        String userId = "UserUtil.getCurrentUserId()";
        if(StringUtils.isEmpty(userId)){
            return Result.loginFail();
        }
        return Result.ok(iosPayService.iosVerify(iosPayVerifyQo,userId));
    }

    @ApiOperationSupport(order = 3)
    @ApiOperation("ios订阅回调V2(为苹果服务器准备)")
    @PostMapping("/callbackV2")
    public Result<IOSStatusVo> callbackV2(@RequestBody @Valid IOSCallBackQo iosCallBackQo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return Result.paramFail();
        }
        iosPayService.callbackV2(iosCallBackQo);
        return Result.ok();
    }

    @ApiOperationSupport(order = 4)
    @ApiOperation("自测会员权益接口")
    @PostMapping("/test")
    public Result<IOSStatusVo> test(@RequestBody @Valid Order Order, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return Result.paramFail();
        }
        iosPayService.sendMq(Order);
        return Result.ok();
    }


    @ApiOperationSupport(order = 5)
    @ApiOperation("ios支付验证（验签补偿）")
    @PostMapping("/iosAgainVerify")
    public Result<IOSStatusVo> iosAgainVerify(@RequestBody @Valid IOSPayAgainVerifyQo iOSPayAgainVerifyQo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return Result.build(null, 0,"支付失败","30003");
        }
        String userId = "UserUtil.getCurrentUserId()";
        if(StringUtils.isEmpty(userId)){
            return Result.build(null, 0,"支付失败","30003");
        }
        IOSStatusVo vo = iosPayService.iosAgainVerify(iOSPayAgainVerifyQo, userId);
        if(vo == null){
            return Result.build(null, 0,"支付失败","30003");
        }
        return Result.ok(vo);
    }
}
