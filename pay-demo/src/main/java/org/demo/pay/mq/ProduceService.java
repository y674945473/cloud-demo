package org.demo.pay.mq;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.demo.pay.contants.RocketMQEnum;
import org.demo.pay.dto.IosCallbackMqDto;
import org.demo.pay.dto.PayMqDto;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
public class ProduceService {

    @Resource
    private RocketMQTemplate rocketmqTemplate;

    public void sendOrderMQ(PayMqDto dto) {
        log.info("订单数据---发送mq信息:{}到topic:{}",  JSON.toJSONString(dto), RocketMQEnum.ORDER_PAY_TOPIC.getTopic());
        rocketmqTemplate.convertAndSend(RocketMQEnum.ORDER_PAY_TOPIC.getTopic()+":"+dto.getGoodsType(), JSON.toJSONString(dto));
    }

    public void sendIosCallbackMQ(IosCallbackMqDto dto) {
        log.info("ios回调---发送mq信息:{}到topic:{}",  JSON.toJSONString(dto), RocketMQEnum.IOS_CALLBACK_TOPIC.getTopic());
        rocketmqTemplate.convertAndSend(RocketMQEnum.IOS_CALLBACK_TOPIC.getTopic(), JSON.toJSONString(dto));
    }

    public void sendIosSubscribeMQ(PayMqDto dto) {
        log.info("订单数据---发送连续包月消息:{}到topic:{}",  JSON.toJSONString(dto), RocketMQEnum.IOS_SUBSCRIBE.getTopic());
        rocketmqTemplate.convertAndSend(RocketMQEnum.IOS_SUBSCRIBE.getTopic()+":subscribe-"+dto.getGoodsType(), JSON.toJSONString(dto));
    }

    public void sendIosRefundMQ(PayMqDto dto) {
        log.info("订单数据---发送退款消息:{}到topic:{}",  JSON.toJSONString(dto), RocketMQEnum.IOS_REFOUND.getTopic());
        rocketmqTemplate.convertAndSend(RocketMQEnum.IOS_REFOUND.getTopic()+":refound", JSON.toJSONString(dto));
    }

}
