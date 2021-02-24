package com.gin.pixivcrawler.utils.timeUtils;

/**
 * 时间单位
 *
 * @author bx002
 * @date 2021/2/23 10:34
 */
public enum TimeUnit {
    /**
     * 年
     */
    YEAR("年", 12),
    MONTH("月", 30),
    DAY("日", 24),
    HOUR("小时", 60),
    MINUTES("分钟", 60),
    SECONDS("秒", 1000),
    MS("毫秒", 1),
    ;
    /**
     * 名称
     */
    String name;
    /**
     * 下级倍率
     */
    int times;


    TimeUnit(String name, int times) {
        this.name = name;
        this.times = times;
    }
}
