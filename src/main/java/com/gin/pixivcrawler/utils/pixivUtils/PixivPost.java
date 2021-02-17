package com.gin.pixivcrawler.utils.pixivUtils;

import com.alibaba.fastjson.JSONObject;
import com.gin.pixivcrawler.utils.JsonUtil;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivBookmarks;
import com.gin.pixivcrawler.utils.pixivUtils.entity.PixivSearchResults;
import com.gin.pixivcrawler.utils.pixivUtils.entity.details.PixivIllustDetail;
import com.gin.pixivcrawler.utils.requestUtils.GetRequest;
import com.gin.pixivcrawler.utils.requestUtils.PostRequest;
import com.gin.pixivcrawler.utils.requestUtils.RequestBase;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.gin.pixivcrawler.utils.JsonUtil.printJson;

/**
 * Pixiv请求工具类
 *
 * @author bx002
 * @date 2021/2/1 17:43
 */
public class PixivPost {
    private static String proxyHost = null;
//    private static String proxyHost = "127.0.0.1";
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PixivPost.class);
    public static final String DOMAIN = "https://www.pixiv.net";
    /**
     * 作品详情接口
     */
    private final static String URL_ILLUST_DETAIL = DOMAIN + "/ajax/illust/%d";
    /**
     * 获取用户收藏接口
     */
    private final static String URL_USER_BOOKMARKS = DOMAIN + "/ajax/user/%d/illusts/bookmarks";
    /**
     * 添加收藏Tag接口
     */
    private final static String URL_TAG_ADD = DOMAIN + "/bookmark_add.php?id=";

    private final static String URL_SEARCH_ARTWORKS = DOMAIN + "/ajax/search/artworks/";

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
        LOG.debug(msg, pid);
        String result = GetRequest.create()
                .setProxyHost(proxyHost)
                .addCookie(cookie).get(String.format(URL_ILLUST_DETAIL, pid));
        PixivIllustDetail body = null;
        try {
            body = getBody(result, PixivIllustDetail.class);
        } catch (RuntimeException e) {
            LOG.warn("pid = {} {}", pid, e.getMessage());
        }
        LOG.debug(msg + TIME_COST, pid, RequestBase.timeCost(start));
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
        String result = GetRequest.create()
                .setProxyHost(proxyHost)
                .addCookie(cookie)
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

    /**
     * 给作品添加tag
     *
     * @param pid    pid
     * @param cookie cookie
     * @param tt     tt
     * @param tags   tags
     * @author bx002
     * @date 2021/2/3 17:38
     */
    public static void addTags(Long pid, String cookie, String tt, String tags) {
        tags = tags.replace(",", " ");
        LOG.info("给作品添加tag {} -> {}", pid, tags);
        PostRequest.create()
                .setProxyHost(proxyHost)
                .setMaxTimes(1)
                .setTimeout(3)
                .addCookie(cookie)
                .addEntityString("tt", tt)
                .addEntityString("id", String.valueOf(pid))
                .addEntityString("tag", tags)
                .addEntityString("mode", "add")
                .addEntityString("type", "illust")
                .addEntityString("from_sid", "")
                .addEntityString("original_tag", "")
                .addEntityString("original_untagged", "0")
                .addEntityString("original_p", "1")
                .addEntityString("original_rest", "")
                .addEntityString("original_order", "")
                .addEntityString("comment", "")
                .addEntityString("restrict", "0")
                .post(URL_TAG_ADD + pid);

    }

    /**
     * 搜索作品
     *
     * @param keyword     关键字
     * @param p           页数(每页固定上限60个)
     * @param cookie      cookie(可选 不提供时不能搜索R-18作品)
     * @param searchTitle true = 搜索标题 false =搜 索tag
     * @param mode        模式 可取值： all safe r18
     * @return 搜索结果
     */
    public static PixivSearchResults search(String keyword, Integer p, String cookie, boolean searchTitle, String mode) {
        LOG.info("搜索关键字 {}", keyword);
        String result = GetRequest.create()
                .setProxyHost(proxyHost)
                .addCookie(cookie)
                .addParam("s_mode", searchTitle ? "s_tc" : "s_tag")
                .addParam("mode", mode)
                .addParam("p", p)
                .get(URL_SEARCH_ARTWORKS + URLEncoder.encode(keyword, StandardCharsets.UTF_8).replace("+", "%20"));

//        printJson(JSONObject.parseObject(result));

        PixivSearchResults body = null;
        try {
            body = getBody(result, PixivSearchResults.class);
        } catch (RuntimeException e) {
            LOG.warn("搜索错误 {} {}", keyword, e.getMessage());
        }
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
