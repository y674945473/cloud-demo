package org.demo.common.util;

/**
 * @author BuShuangLi
 * @date 2019/1/18
 * 本类主要用于生成主键ID，方法参考twitter的SnowFlake。
 * SnowFlake的优点是，整体上按照时间自增排序， 
 * 并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高。
 * SnowFlake的示例结构如下(每部分用-分开)
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截得到的值），
 * 这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序SnowflakeIdUtil类的twepoch属性）。
 * 41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId，可以合在一起使用，也可以分开使用
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号
 * 加起来刚好64位，为一个Long型。
 */
public class SnowflakeUtil {
    /**
     * 序列id所占的位数 支持每毫秒产生1024个id序号
     */
    private final long sequenceBits = 10L;
    /**
     * 机器id所占的位数 支持256台机器
     */
    private final long workerIdBits = 8L;
    /**
     * 区域id所占的位数 支持32个区域（即支持最大机器数为256*32=8192）
     */
    private final long datacenterIdBits = 5L;
    /**
     * 开始时间截 (2018-01-01 00:00:00) 可使用至2052年
     */
    private final long twepoch = 3514736000000L;
    /**
     * 机器id左移位
     */
    private final long workerIdShift = sequenceBits;
    /**
     * 区域id左移位
     */
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    /**
     * 时间截左移位
     */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    /**
     * 生成序列的掩码
     */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);
    /**
     * 支持的最大机器id
     */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    /**
     * 支持的最大区域id
     */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    /**
     * 当前毫秒内序列
     */
    private long sequence = 0L;
    /**
     * 当前机器id
     */
    private long workerId;
    /**
     * 当前区域id
     */
    private long datacenterId;
    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;
    /**
     * 一台机子只需要一个实例，以保证产生有序的、不重复的ID
     */
    private static SnowflakeUtil snowflakeUtil = new SnowflakeUtil();

    private SnowflakeUtil() {
        // 设置workerId和datacenterId
        // TODO workerId和datacenterId可以通过数据库、配置文件、缓存等方式获取，这里为方便演示默认都设置为0
        long workerId = 0;
        long datacenterId = 0;
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId（０～%d）设置错误", maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenterId（０～%d）设置错误", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;

    }

    public static SnowflakeUtil getInstance() {
        return snowflakeUtil;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized String nextId() {
        long timestamp = timeGen();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        // 也就是说当应用运行时是不能将时钟改小的，要么异常退出，要么ID重复
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("系统时钟回退%d秒", lastTimestamp - timestamp));
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
        // 上次生成ID的时间截
        lastTimestamp = timestamp;
        // 移位并通过或运算拼到一起组成64位的ID
        long id = ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift)
            | (workerId << workerIdShift) | sequence;
        return String.valueOf(id);
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        // 考虑到当前系统时钟不准确以及修改时钟产生的ID问题，
        // 这里可以根据自身业务使用网络时钟或其他更加准确及稳定的时钟
        return System.currentTimeMillis();
    }

}