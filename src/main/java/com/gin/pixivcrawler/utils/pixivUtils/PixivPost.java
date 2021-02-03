package com.gin.pixivcrawler.utils.pixivUtils;

import com.alibaba.fastjson.JSONObject;
import com.gin.pixivcrawler.utils.JsonUtil;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import com.gin.pixivcrawler.utils.requestUtils.GetRequest;
import com.gin.pixivcrawler.utils.requestUtils.RequestBase;

import static com.gin.pixivcrawler.utils.JsonUtil.printJson;

/**
 * Pixiv请求工具类
 *
 * @author bx002
 * @date 2021/2/1 17:43
 */
public class PixivPost {
    private static final String DOMAIN = "https://www.pixiv.net";
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PixivPost.class);
    /**
     * 作品详情接口
     */
    private final static String URL_ILLUST_DETAIL = "https://www.pixiv.net/ajax/illust/%d";
    private final static String URL_USER_BOOKMARKS = "https://www.pixiv.net/ajax/user/%d/illusts/bookmarks";
    public static final String TIME_COST = " 完成 耗时:{}";


    /**
     * 请求作品详情
     *
     * @param pid    pid
     * @param cookie cookie
     * @return com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail
     * @author bx002
     * @date 2021/2/2 16:45
     */
    public static PixivIllustDetail getIllustDetail(long pid, String cookie) {
        String msg = "请求作品详情 pid = {}";
        long start = System.currentTimeMillis();
        LOG.info(msg, pid);
        String result = GetRequest.create().addCookie(cookie).get(String.format(URL_ILLUST_DETAIL, pid));
        PixivIllustDetail body = null;
        try {
            body = getBody(result, PixivIllustDetail.class);
        } catch (RuntimeException e) {
            LOG.warn("pid = {} {}", pid, e.getMessage());
        }
        LOG.info(msg + TIME_COST, pid, RequestBase.timeCost(start));
        return body;
    }

    /**
     * 获取用户收藏列表
     *
     * @param cookie cookie
     * @param userId 用户id
     * @param offset offset
     * @param limit  limit
     * @param tag    标签
     * @return com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks
     * @author bx002
     * @date 2021/2/3 15:19
     */
    public static PixivBookmarks getBookmarks(String cookie, long userId, int offset, int limit, String tag) {
        String msg = "请求收藏作品 userID = {} tag = {} offset = {} limit = {}";
        long start = System.currentTimeMillis();
        LOG.info(msg, userId, tag, offset, limit);
        String result = GetRequest.create().addCookie(cookie)
                .addParam("lang", "zh")
                .addParam("rest", "show")
                .addParam("offset", offset)
                .addParam("limit", limit)
                .addParam("tag", tag)
                .get(String.format(URL_USER_BOOKMARKS, userId));
        PixivBookmarks body = null;
        try {
            body = getBody(result, PixivBookmarks.class);
        } catch (RuntimeException e) {
            LOG.warn("userId = {} {}", userId, e.getMessage());
        }
        LOG.info(msg + TIME_COST, userId, tag, offset, limit, RequestBase.timeCost(start));
        return body;
    }


    private static <T> T getBody(String result, Class<T> clazz) {
        JSONObject json = JSONObject.parseObject(result);
        if (!json.getBoolean("error")) {
            return JSONObject.parseObject(json.getJSONObject("body").toJSONString(), clazz);
        }
        JsonUtil.printJson(json);
        throw new RuntimeException(json.getString("message"));
    }


    public static void main(String[] args) {
//        long pid = 87460135;
        printJson(getIllustDetail(87460135, null));
        printJson(getIllustDetail(87475198, null));
    }
}
