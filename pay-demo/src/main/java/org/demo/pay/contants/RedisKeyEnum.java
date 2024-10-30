package org.demo.pay.contants;

public enum RedisKeyEnum {

    /**
     * 支付订单编号
     */
    PAY_OREDER_NO("orderNo:");

    RedisKeyEnum(String redisKey) {
        this.redisKey = redisKey;
    }

    private final String redisKey;

    public String getRedisKey() {
        String systemName = new SystemPrefix().getSystemName();
        return systemName + redisKey;
    }

    private static class SystemPrefix {
        /**
         * 统一前缀
         */
        private String getSystemName() {
            return "order:pay:";
        }
    }
}
