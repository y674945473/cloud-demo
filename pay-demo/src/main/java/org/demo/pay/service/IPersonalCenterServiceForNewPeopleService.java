package org.demo.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.demo.pay.entity.PersonalCenterServiceForNewPeople;

/**
 * <p>
 * 个人中心-服务分类表 服务类
 * </p>
 *
 * @author xsq
 * @since 2024-01-24
 */
public interface IPersonalCenterServiceForNewPeopleService extends IService<PersonalCenterServiceForNewPeople> {

    PersonalCenterServiceForNewPeople getId(Integer id);

}
