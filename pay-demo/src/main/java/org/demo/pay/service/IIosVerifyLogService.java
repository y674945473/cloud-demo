package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.IosVerifyLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 */
public interface IIosVerifyLogService extends IService<IosVerifyLog> {

    void insertLog(IosVerifyLog iosCallbackLog);
}
