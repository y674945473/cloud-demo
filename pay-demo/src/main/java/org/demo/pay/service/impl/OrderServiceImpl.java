package org.demo.pay.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.demo.pay.entity.Order;
import org.demo.pay.entity.PersonalCenterOrder;
import org.demo.pay.entity.PersonalCenterOrderItem;
import org.demo.pay.mapper.OrderMapper;
import org.demo.pay.mapper.PersonalCenterOrderItemMapper;
import org.demo.pay.mapper.PersonalCenterOrderMapper;
import org.demo.pay.qo.UserOrderPageQuery;
import org.demo.pay.service.IOrderService;
import org.demo.pay.vo.OrderListVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author xsq
 * @since 2023-10-10
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private PersonalCenterOrderMapper personalCenterOrderMapper;
    @Resource
    private PersonalCenterOrderItemMapper personalCenterOrderItemMapper;

    @Override
    public void addOrder(Order order){
        orderMapper.insert(order);
    }

    @Override
    public Order selectByOrderNo(String orderNo){
        Map<String, Object> map = new HashMap<>();
        map.put("order_no",orderNo);
        List<Order> orders = orderMapper.selectByMap(map);
        if (CollectionUtils.isNotEmpty(orders)){
            return orders.get(0);
        }
        return null;
    }

    @Override
    public int updateByOrderNo(String orderNo, int status) {
        return orderMapper.updateByOrderNo(orderNo,status);
    }

    @Override
    public int updatePayStatusByOrderNo(String orderNo, int status, String remark) {
        return orderMapper.updatePayStatusByOrderNo(orderNo,status,remark);
    }

    @Override
    public Order selectByOriginalTransactionId(String originalTransactionId) {
        Map<String, Object> map = new HashMap<>();
        map.put("original_transaction_id",originalTransactionId);
        List<Order> orders = orderMapper.selectByMap(map);
        if (CollectionUtils.isNotEmpty(orders)){
            orders.sort(Comparator.comparing(Order::getCreatedAt));
            return orders.get(orders.size()-1);
        }
        return null;
    }

    @Override
    public Order selectByTransactionId(String transactionId) {
        Map<String, Object> map = new HashMap<>();
        map.put("third_order_no",transactionId);
        List<Order> orders = orderMapper.selectByMap(map);
        if (CollectionUtils.isNotEmpty(orders)){
            return orders.get(0);
        }
        return null;
    }

    @Override
    public Order selectByProductId(String userId, String productId, LocalDateTime startDate,LocalDateTime endDate) {

        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("product_id",productId);
        queryWrapper.eq("order_pay_status",0);
        queryWrapper.between("created_at",startDate,endDate);
        List<Order> orders = orderMapper.selectList(queryWrapper);
//        Map<String, Object> map = new HashMap<>();
//        map.put("user_id",userId);
//        map.put("product_id",productId);
//        map.put("order_pay_status",0);
//        List<Order> orders = orderMapper.selectByMap(map);
        if (CollectionUtils.isNotEmpty(orders)){
            return orders.get(0);
        }
        return null;
    }

    /**
     * 用户订单列表
     * @param userOrderPageQuery
     * @param userId
     * @return
     */
    @Override
    public Page<OrderListVo> userOrderList(UserOrderPageQuery userOrderPageQuery, String userId) {
        Page<OrderListVo> pageData = new Page<>();
        Page<PersonalCenterOrder> page = new Page<>(userOrderPageQuery.getNumber(), userOrderPageQuery.getSize());
        Page<PersonalCenterOrder> pageInfo = personalCenterOrderMapper.selectPageInfo(page,userId);
        pageData.setTotal(pageInfo.getTotal());
        pageData.setSize(pageInfo.getSize());
        pageData.setCurrent(pageInfo.getCurrent());
        List<PersonalCenterOrder> records = pageInfo.getRecords();
        Set<Integer> orderIdList = records.stream().map(PersonalCenterOrder::getId).collect(Collectors.toSet());
        Map<String,Object> itemWhere = new HashMap<>();
        itemWhere.put("orderIdList",orderIdList);
        List<PersonalCenterOrderItem> personalCenterOrderItemList = personalCenterOrderItemMapper.list(itemWhere);
        Map<String, List<PersonalCenterOrderItem>> personalCenterOrderItemMap = personalCenterOrderItemList.stream().collect(Collectors.groupingBy(PersonalCenterOrderItem::getOrderId));
        List<OrderListVo> orderListVoList = new ArrayList<>();
        for (PersonalCenterOrder personalCenterOrder:records){
            OrderListVo orderListVo = new OrderListVo();
            BeanUtil.copyProperties(personalCenterOrder,orderListVo);
            if (StringUtils.isEmpty(orderListVo.getPrice())){
                orderListVo.setPrice("--");
            }
            List<PersonalCenterOrderItem> personalCenterOrderItems = personalCenterOrderItemMap.get(String.valueOf(personalCenterOrder.getId()));
            if (CollectionUtils.isNotEmpty(personalCenterOrderItems)){
                if (orderListVo.getPrice().equals("--")){
                    orderListVo.setPrice(personalCenterOrderItems.get(0).getGoodsPrice());
                }
                if (orderListVo.getPrice().equals("45元") || orderListVo.getPrice().equals("45")){
                    if (!StringUtils.isEmpty(personalCenterOrder.getOriginalTransactionId()) && personalCenterOrder.getAppleStatus().equals("SUBSCRIBED") && personalCenterOrderItems.get(0).getServiceTableId().equals("7")){
                        orderListVo.setPrice("30元");
                    }
                }
                StringBuilder title = new StringBuilder();
                for (PersonalCenterOrderItem personalCenterOrderItem:personalCenterOrderItems){
                    title.append(personalCenterOrderItem.getGoodsName()).append("+");
                }
                orderListVo.setTitle(title.substring(0,title.length() - 1));
            }
            orderListVoList.add(orderListVo);
        }
        pageData.setRecords(orderListVoList);
        return pageData;
    }
}
