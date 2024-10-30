package org.demo.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.demo.pay.entity.PersonalCenterOrderItem;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单单个商品折扣状态件数表 Mapper 接口
 * </p>
 *
 * @author yhx
 * @since 2024-02-19
 */
public interface PersonalCenterOrderItemMapper extends BaseMapper<PersonalCenterOrderItem> {

    List<PersonalCenterOrderItem> list(Map<String,Object> where);
}
