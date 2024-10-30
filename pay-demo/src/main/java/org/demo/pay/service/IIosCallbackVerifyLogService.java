package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.IosCallbackVerifyLog;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yhx
 * @since 2024-01-30
 */
public interface IIosCallbackVerifyLogService extends IService<IosCallbackVerifyLog> {

    Long getUUID(String uuid);

}
