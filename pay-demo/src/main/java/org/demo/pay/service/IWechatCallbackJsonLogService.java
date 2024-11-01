package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.WechatCallbackJsonLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IWechatCallbackJsonLogService extends IService<WechatCallbackJsonLog> {

    void addMessageLog(WechatCallbackJsonLog wechatCallbackJsonLog);

}
