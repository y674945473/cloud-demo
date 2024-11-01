package org.demo.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.demo.pay.entity.Order;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 */
public interface OrderMapper extends BaseMapper<Order> {

    int updateByOrderNo(String orderNo, int status);

    int updatePayStatusByOrderNo(String orderNo, int status, String remark);

}
