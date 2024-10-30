package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.WechatCallbackJsonLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xsq
 * @since 2024-01-25
 */
public interface IWechatCallbackJsonLogService extends IService<WechatCallbackJsonLog> {

    void addMessageLog(WechatCallbackJsonLog wechatCallbackJsonLog);

}
