package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.WechatCallbackLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IWechatCallbackLogService extends IService<WechatCallbackLog> {

    Long addLog(WechatCallbackLog wechatCallbackLog);

}
