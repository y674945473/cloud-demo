package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.Order;
import org.demo.pay.qo.UserOrderPageQuery;
import org.demo.pay.vo.OrderListVo;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 */
public interface IOrderService extends IService<Order> {

    void addOrder(Order order);

    Order selectByOrderNo(String orderNo);

    int updateByOrderNo(String orderNo,int status);

    int updatePayStatusByOrderNo(String orderNo, int status, String remark);

    Order selectByOriginalTransactionId(String originalTransactionId);

    Order selectByTransactionId(String originalTransactionId);

    Order selectByProductId(String userId, String productId, LocalDateTime startDate, LocalDateTime endDate);

    Page<OrderListVo> userOrderList(UserOrderPageQuery userOrderPageQuery, String userId);
}
