package com.gin.pixivcrawler.utils.timeUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.gin.pixivcrawler.utils.timeUtils.TimeUnit.*;


/**
 * 时间工具类
 *
 * @author bx002
 * @date 2021/2/23 10:29
 */
public class TimeUtils {
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DEFAULT_FULL_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 根据时间单位获得时长毫秒数
     *
     * @param time     时间
     * @param timeUnit 时间单位
     * @return long 毫秒数
     * @author bx002
     * @date 2021/2/23 10:52
     */
    public static long getDurationOfTimeUnit(long time, TimeUnit timeUnit) {
        return switch (timeUnit) {
            case YEAR -> time * getDurationOfTimeUnit(YEAR.times, MONTH) + getDurationOfTimeUnit(5, DAY);
            case MONTH -> time * getDurationOfTimeUnit(MONTH.times, DAY);
            case DAY -> time * getDurationOfTimeUnit(DAY.times, HOUR);
            case HOUR -> time * getDurationOfTimeUnit(HOUR.times, MINUTES);
            case MINUTES -> time * getDurationOfTimeUnit(MINUTES.times, SECONDS);
            case SECONDS -> time * getDurationOfTimeUnit(SECONDS.times, MS);
            case MS -> time;
        };
    }

    /**
     * 当前时间毫秒
     *
     * @return long
     * @author bx002
     * @date 2021/2/23 11:10
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    /**
     * 以默认格式和时区 输出格式化当前时间
     *
     * @return java.lang.String
     * @author bx002
     * @date 2021/2/23 11:12
     */
    public static String formatTime() {
        return formatTime(now(), MS, DEFAULT_FULL_FORMATTER, DEFAULT_ZONE_ID);
    }

    /**
     * 以默认格式和时区 输出格式化时间
     *
     * @param time     时间（秒，毫秒）
     * @param timeUnit 时间单位（秒，毫秒）
     * @return java.lang.String
     * @author bx002
     * @date 2021/2/23 11:12
     */
    public static String formatTime(long time, TimeUnit timeUnit) {
        return formatTime(time, timeUnit, DEFAULT_FORMATTER, DEFAULT_ZONE_ID);
    }

    /**
     * 格式化输出指定时间
     *
     * @param time      时间（秒，毫秒）
     * @param timeUnit  时间单位（秒，毫秒）
     * @param formatter 输出格式
     * @param zoneId    时区
     * @return java.lang.String
     * @author bx002
     * @date 2021/2/23 11:11
     */
    public static String formatTime(long time, TimeUnit timeUnit, DateTimeFormatter formatter, ZoneId zoneId) {
        long duration = getDurationOfTimeUnit(time, timeUnit);
        Instant instant = Instant.ofEpochMilli(duration);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(instant, zoneId);
        return formatter.format(zonedDateTime);
    }

    /**
     * 根据默认格式和时区解析时间字符串到毫秒
     *
     * @param timeString 时间字符串
     * @return long
     * @author bx002
     * @date 2021/2/23 11:38
     */
    public static long parse(String timeString) {
        return parse(timeString, DEFAULT_FORMATTER, DEFAULT_ZONE_ID);
    }

    /**
     * 根据格式和时区解析时间字符串到毫秒
     *
     * @param timeString 时间字符串
     * @param formatter  格式
     * @param zoneId     时区
     * @return long
     * @author bx002
     * @date 2021/2/23 11:38
     */
    public static long parse(String timeString, DateTimeFormatter formatter, ZoneId zoneId) {
        LocalDateTime localDateTime = LocalDateTime.parse(timeString, formatter);
        ZonedDateTime zonedDateTime = ZonedDateTime.ofLocal(localDateTime, zoneId, null);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    /**
     * 当前时间距离指定时间的时长格式化输出
     *
     * @param start 指定时间（毫秒）
     * @return java.lang.String
     * @author bx002
     * @date 2021/2/23 12:00
     */
    public static String timeCostFrom(long start) {
        return timeCost(now() - start);
    }

    /**
     * 时长格式化输出
     *
     * @param duration 时长（毫秒）
     * @return java.lang.String
     * @author bx002
     * @date 2021/2/23 12:00
     */
    public static String timeCost(long duration) {
        long unit;
        unit = getDurationOfTimeUnit(1, HOUR);
        if (duration > unit) {
            return String.format("%.2f %s", 1.0 * duration / unit, HOUR.name);
        }
        unit = getDurationOfTimeUnit(1, MINUTES);
        if (duration > unit) {
            return String.format("%.1f %s", 1.0 * duration / unit, MINUTES.name);
        }
        unit = getDurationOfTimeUnit(1, SECONDS);
        if (duration > unit) {
            return String.format("%.1f %s", 1.0 * duration / unit, SECONDS.name);
        }

        return String.format("%d %s", duration, MS.name);
    }
}
