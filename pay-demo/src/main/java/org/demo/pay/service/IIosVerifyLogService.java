package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.IosVerifyLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xsq
 * @since 2024-01-29
 */
public interface IIosVerifyLogService extends IService<IosVerifyLog> {

    void insertLog(IosVerifyLog iosCallbackLog);
}
