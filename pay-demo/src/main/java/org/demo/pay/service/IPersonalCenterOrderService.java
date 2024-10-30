package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.Order;
import org.demo.pay.entity.PersonalCenterOrder;

/**
 * <p>
 * 个人中心-订单表 服务类
 * </p>
 *
 * @author yhx
 * @since 2024-02-19
 */
public interface IPersonalCenterOrderService extends IService<PersonalCenterOrder> {

    PersonalCenterOrder selectOrder(Order order);

}
