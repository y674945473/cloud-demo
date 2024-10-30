package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.WechatCallbackLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xsq
 * @since 2024-01-25
 */
public interface IWechatCallbackLogService extends IService<WechatCallbackLog> {

    Long addLog(WechatCallbackLog wechatCallbackLog);

}
