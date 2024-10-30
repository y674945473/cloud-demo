package org.demo.pay.contants;

public enum RocketMQEnum {

    ORDER_PAY_TOPIC("order-pay"),
    IOS_CALLBACK_TOPIC("ios-callback"),
    IOS_SUBSCRIBE("ios-subscribe"),
    IOS_REFOUND("ios-refound");

    private final String topic;

    RocketMQEnum(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }
}
