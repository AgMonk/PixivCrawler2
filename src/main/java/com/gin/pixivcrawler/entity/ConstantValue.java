package com.gin.pixivcrawler.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 常量
 *
 * @author Gin
 * @date 2021/2/17 14:32
 */
public class ConstantValue {
    /**
     * 逗号分隔符
     */
    public static final String DELIMITER_COMMA = ",";
    /**
     * 空格分隔符
     */
    public static final String DELIMITER_SPACE = " ";
    /**
     * 回调任务 ：添加标签
     */
    public static final String CALLBACK_TASK_ADD_TAG = "添加标签";
    /**
     * 回调任务 ：下载
     */
    public static final String CALLBACK_TASK_DOWNLOAD = "下载";
    /**
     * 回调任务 ：搜索下载
     */
    public static final String CALLBACK_TASK_SEARCH_DOWNLOAD = "搜索下载";
    /**
     * 回调任务 :移动到未分类
     */
    public static final String CALLBACK_TASK_MOVE_TO_UNTAGGED = "移动到未分类";
    /**
     * 详情类型：搜索
     */
    public static final String TYPE_OF_QUERY_SEARCH = "2.搜索下载:";
    /**
     * 详情类型:未分类
     */
    public static final String TYPE_OF_QUERY_UNTAGGED = "3.未分类";
    public static final String NGINX_I_PIXIV_CAT = "i.pixiv.cat";
    public static final String DOMAIN_I_PXIMG_NET = "i.pximg.net";

    /**
     * 插画
     */
    public final static int ILLUST_TYPE_ILLUSTRATION = 0;
    /**
     * 漫画
     */
    public final static int ILLUST_TYPE_MANGA = 1;
    /**
     * 动图
     */
    public final static int ILLUST_TYPE_GIF = 2;

    /**
     * 文件名的非法字符
     *
     * @date 2021/2/18 9:26
     */
    public final static Map<String, String> ILLEGAL_CHAR = new HashMap<>();

    static {
        ILLEGAL_CHAR.put(":", "：");
        ILLEGAL_CHAR.put("\n", "");
        ILLEGAL_CHAR.put("?", "？");
        ILLEGAL_CHAR.put("<", "《");
        ILLEGAL_CHAR.put(">", "》");
        ILLEGAL_CHAR.put("*", "×");
        ILLEGAL_CHAR.put("|", "^");
        ILLEGAL_CHAR.put("\"", "“");
        ILLEGAL_CHAR.put("\\", "_");
        ILLEGAL_CHAR.put("/", "_");
    }

    /**
     * 需要删除的用户名后缀
     *
     * @date 2021/2/18 9:32
     */
    public final static List<String> USERNAME_SUFFIX = Arrays.asList("@", "＠", "|", "FANBOX", "fanbox", "仕事", "■");
}
