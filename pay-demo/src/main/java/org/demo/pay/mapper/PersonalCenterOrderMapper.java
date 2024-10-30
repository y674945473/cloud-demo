package org.demo.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.demo.pay.entity.PersonalCenterOrder;

/**
 * <p>
 * 个人中心-订单表 Mapper 接口
 * </p>
 *
 * @author yhx
 * @since 2024-02-19
 */
public interface PersonalCenterOrderMapper extends BaseMapper<PersonalCenterOrder> {

    Page<PersonalCenterOrder> selectPageInfo(Page<PersonalCenterOrder> page, String uid);
}
