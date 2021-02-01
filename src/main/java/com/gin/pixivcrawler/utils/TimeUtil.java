package com.gin.pixivcrawler.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author bx002
 */
public class TimeUtil {
    public final static DateTimeFormatter FULL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 东八区当前时间
     *
     * @return ZonedDateTime
     */
    public static ZonedDateTime now() {
        return ZonedDateTime.now(ZONE_ID);
    }

    /**
     * 格式输出当前时间
     *
     * @return 当前时间
     */
    public static String nowString() {
        return now().format(FULL_DATE_TIME_FORMATTER);
    }

    public static String second2String(long second) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(second), ZONE_ID);
        return zonedDateTime.format(FULL_DATE_TIME_FORMATTER);
    }

    public static String second2String(long second, DateTimeFormatter dateTimeFormatter) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(second), ZONE_ID);
        return zonedDateTime.format(dateTimeFormatter);
    }

    /**
     * 指定格式输出当前时间
     *
     * @param pattern 格式
     * @return 当前时间
     */
    public static String nowString(String pattern) {
        return now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 当前毫秒数
     *
     * @return 毫秒
     */
    public static long nowMillis() {
        return now().toInstant().toEpochMilli();
    }

    /**
     * 当前秒数
     *
     * @return 秒数
     */
    public static long nowSeconds() {
        return now().toEpochSecond();
    }

    /**
     * 解析时间字符串为ZonedDateTime
     *
     * @param timeString 时间字符串
     * @return ZonedDateTime
     */
    public static ZonedDateTime parse(String timeString) {
        String t = "T";
        if (timeString.contains(t) && timeString.indexOf(t) == 10) {
            return ZonedDateTime.parse(timeString);
        }
        return ZonedDateTime.parse(timeString, FULL_DATE_TIME_FORMATTER);
    }


    /**
     * 解析时间字符串为毫秒
     *
     * @param timeString 时间字符串
     * @return long
     * @author bx002
     * @date 2020/11/9 12:29
     */
    public static long parseToMillis(String timeString) {
        return parse(timeString).toInstant().toEpochMilli();
    }

    /**
     * 解析时间字符串为秒
     *
     * @param timeString 时间字符串
     * @return long
     * @author bx002
     * @date 2020/11/9 12:31
     */
    public static long parseToSeconds(String timeString) {
        return parse(timeString).toEpochSecond();
    }


    public static ZonedDateTime getStartOf() {
        return now().withHour(0).withMinute(0).withSecond(0);
    }

    public static ZonedDateTime getEndOf() {
        return now().withHour(23).withMinute(59).withSecond(59);
    }


    /**
     * 获得当天0点的秒数
     *
     * @return 获得当天0点的秒数
     */
    public static long getStartOfDay() {
        return getStartOf().toEpochSecond();
    }

    /**
     * 获得当天24点的秒数
     *
     * @return 获得当天24点的秒数
     */
    public static long getEndOfDay() {
        return getEndOf().toEpochSecond();
    }

    public static long getStartOfWeek() {
        ZonedDateTime startOf = getStartOf();
        return startOf.minusDays(startOf.getDayOfWeek().getValue() - 1).toEpochSecond();

    }

    public static long getEndOfWeek() {
        ZonedDateTime endOf = getEndOf();
        return endOf.plusDays(7 - endOf.getDayOfWeek().getValue()).toEpochSecond();

    }

    public static long getStartSecondOfDate(String dateString) {
        LocalDate startDate = LocalDate.parse(dateString, DATE_FORMATTER);
        LocalTime startTime = LocalTime.of(0, 0, 0);
        return startDate.toEpochSecond(startTime, ZoneOffset.ofHours(8));
    }

    public static long getEndSecondOfDate(String dateString) {
        LocalDate endDate = LocalDate.parse(dateString, DATE_FORMATTER);
        LocalTime endTime = LocalTime.of(23, 59, 59);
        return endDate.toEpochSecond(endTime, ZoneOffset.ofHours(8));
    }
}
